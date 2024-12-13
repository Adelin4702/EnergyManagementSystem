import React, { useEffect, useState, useRef } from 'react';
import { Modal, ModalHeader, ModalBody, Input, Button, Badge, ListGroup, ListGroupItem } from 'reactstrap';
import { jwtDecode } from 'jwt-decode';
import * as API_USERS from './person/api/person-api';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import './commons/styles/project-style.module.css';

const Chat = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userName, setUserName] = useState('');
    //const [lastSeen, setLastSeen] = useState(new Date().toISOString());
    const [correspondents, setCorrespondents] = useState([]); // List of correspondents
    const [selectedCorrespondent, setSelectedCorrespondent] = useState(null); // The selected correspondent for chatting
    const [chatVisible, setChatVisible] = useState(false); // Toggle chat window visibility
    const [messages, setMessages] = useState([]); // Store chat messages
    const [inputMessage, setInputMessage] = useState(''); // Store message input
    const [isTyping, setIsTyping] = useState(false);  // Typing indicator for the selected correspondent
    const stompClientChat = useRef(null);  // STOMP client reference for WebSocket
    const personId = sessionStorage.getItem('personId');
    const role = sessionStorage.getItem('role');
    const chatServerUrl = 'http://localhost:80/api-chat/ws';
    const typingTimeout = useRef(null); // Timeout for typing status
    const hardcodedCorrespondent = {
        id: '0',
        name: 'Group',
    };

    const [typingCorrespondents, setTypingCrespondents] = useState([]);  // Map to track typing status by correspondent id
    const [lastSeen, setLastSeen] = useState(new Map());  // Map to track typing status by correspondent id

    function showMessage(message) {
        // Create a new div element to hold the message
        const messageDiv = document.createElement('div');
    
        // Style the message box
        messageDiv.style.position = 'fixed';
        messageDiv.style.bottom = '20px';
        messageDiv.style.left = '50%';
        messageDiv.style.transform = 'translateX(-50%)';
        messageDiv.style.backgroundColor = '#333';
        messageDiv.style.color = 'white';
        messageDiv.style.padding = '10px 20px';
        messageDiv.style.borderRadius = '5px';
        messageDiv.style.fontSize = '20px';
        messageDiv.style.zIndex = '9999';
        messageDiv.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
        
        // Set the message text
        messageDiv.innerText = message;
    
        // Append the message div to the body
        document.body.appendChild(messageDiv);
    
        // Remove the message after 2 seconds
        setTimeout(() => {
            document.body.removeChild(messageDiv);
        }, 2000);
    }

    useEffect(() => {
        checkLoginStatus();
    }, []);

    function fetchPersons() {
        return API_USERS.getPersons((result, status, err) => {
            const role = sessionStorage.getItem('role');
            if (result !== null && status === 200) {
                result = result.filter((person) => person.id !== personId);
                if (role === 'ROLE_USER') {
                    result = result.filter((person) => person.admin === true);
                }
                result.push(hardcodedCorrespondent);
                setCorrespondents(result);
            } else {
                console.error("CANNOT FETCH PERSONS IN CHAT");
            }
        });
    }

    useEffect(() => {
        if (isLoggedIn && personId) {
            fetchPersons();
            setupWebSocketConnections();
        }
        return () => {
            disconnectWebSocket();
        };
    }, [isLoggedIn, personId]);

    useEffect(() => {
        console.log("Last seen have been updated: ", lastSeen);
    }, [lastSeen]);  // This will log the seen messages whenever they change

    useEffect(() => {
        console.log("Updated messages:", messages);
    }, [messages]);  // This will run whenever 'messages' state is updated

    const checkLoginStatus = () => {
        const token = sessionStorage.getItem('jwt');
        if (token) {
            setIsLoggedIn(true);
            const decodedToken = jwtDecode(token);
            setUserName(decodedToken.username || 'User');
        }
    };

    const setupWebSocketConnections = () => {
        const chatSocket = new SockJS(chatServerUrl);
        stompClientChat.current = Stomp.over(chatSocket);
        stompClientChat.current.connect({}, (frame) => {
            console.log('Connected to Chat: ' + frame);
            stompClientChat.current.subscribe(`/topic/chat/${personId}`, (message) => {
                const newMessage = JSON.parse(message.body);
                setMessages((prevMessages) => {
                    const updatedMessages = [...prevMessages, newMessage];
                    let pers = correspondents.find((person) => person.id == newMessage.sender);
                    showMessage(`${newMessage.sender} sent you a new message`);
                    return updatedMessages;
                });
            });

            // Subscribe to typing notifications
            stompClientChat.current.subscribe(`/topic/typing/${personId}`, (message) => {
                const typingMessage = JSON.parse(message.body);

                if (typingMessage.type === 'typing' && typingMessage.sender !== personId) {
                    setTypingCrespondents((prev) => {
                        if (!prev.includes(typingMessage.sender)) {
                            return [...prev, typingMessage.sender]; // Add sender immutably
                        }
                        return prev; // Prevent duplicates
                    });
                } else {
                    if (typingMessage.type === 'not_typing' && typingMessage.sender !== personId) {
                        setTypingCrespondents((prev) => prev.filter(item => item !== typingMessage.sender)); // Remove sender immutably
                    }
                }
            });

            stompClientChat.current.subscribe(`/topic/seen/${personId}`, (message) => {
                const seenMessage = JSON.parse(message.body);
                setLastSeen((prev) => {
                    const newLastSeen = new Map(prev);
                    newLastSeen.set(seenMessage.sender, seenMessage.date);
                    return newLastSeen;
                });

                // messages.forEach((msg) => {
                //     if (msg.sender === personId && msg.receiver === seenMessage.sender) {
                //         if (!seenMessages.includes(msg.id)) {
                // setSeenMessages((prevMessages) => {
                //     return [...prevMessages, seenMessage.id];
                //         }); 
                //     }
                // }
                // });

                // Optionally log the newly marked seen messages
                //console.log("Updated seen messages: ", newSeenMessages);

            });

        });

        stompClientChat.current.onStompError = (frame) => {
            console.error('STOMP Error in Chat:', frame);
        };
    };

    const disconnectWebSocket = () => {
        if (stompClientChat.current) {
            stompClientChat.current.disconnect();
            stompClientChat.current = null;
        }
    };

    // Handle sending a chat message
    const handleSendMessage = () => {
        if (inputMessage.trim() && stompClientChat.current?.connected) {
            let message = {};
            if (role !== "ROLE_ADMIN") {
                message = {
                    sender: personId,
                    receiver: selectedCorrespondent.id,
                    date: new Date().toISOString(),
                    message: inputMessage,
                    type: "single"
                };
            } else {
                if (selectedCorrespondent.name === "Group") {
                    message = {
                        sender: personId,
                        receiver: selectedCorrespondent.id,
                        date: new Date().toISOString(),
                        message: inputMessage,
                        type: "group"
                    };
                } else {
                    message = {
                        sender: personId,
                        receiver: selectedCorrespondent.id,
                        date: new Date().toISOString(),
                        message: inputMessage,
                        type: "single",
                    };
                }
            }

            if (role === "ROLE_ADMIN" && selectedCorrespondent.name === "Group") {
                correspondents.forEach((correspondent) => {
                    if (correspondent.name !== "Group") {
                        message.receiver = correspondent.id;
                        stompClientChat.current.send(`/app/sendMessage/${personId}`, {}, JSON.stringify(message));
                    }
                });
            } else {
                stompClientChat.current.send(`/app/sendMessage/${personId}`, {}, JSON.stringify(message));
            }

            setMessages((prevMessages) => [...prevMessages, message]);
            setInputMessage('');
            stopTyping();
        }
    };

    // Handle typing indicator
    const handleTyping = () => {
        if (!isTyping) {
            setIsTyping(true);
            sendTypingNotification(true);
        }

        // Clear the previous timeout and start a new one
        clearTimeout(typingTimeout.current);
        typingTimeout.current = setTimeout(() => {
            stopTyping();
        }, 2000); // Stop typing after 2 seconds of inactivity
    };

    const sendTypingNotification = (isTyping) => {
        let typingMessage = {
            sender: personId,
            receiver: selectedCorrespondent.id,
            date: new Date().toISOString(),
            message: "",
            type: "typing"
        };

        if (isTyping === false) {
            typingMessage.type = "not_typing"
        }
        stompClientChat.current.send(`/app/sendTypingNotification/${personId}`, {}, JSON.stringify(typingMessage));
        handleMessageSeen();
    };

    const stopTyping = () => {
        setIsTyping(false);
        sendTypingNotification(false);
    };

    const handleMessageSeen = () => {
        let seenMessage = {
            sender: personId,
            receiver: selectedCorrespondent.id,
            date: new Date().toISOString(),
            type: "seen"
        };
        stompClientChat.current.send(`/app/sendSeenNotification/${personId}`, {}, JSON.stringify(seenMessage));
    };

    const handleScroll = (event) => {
        handleMessageSeen();
    };

    // Open chat window with a correspondent
    const openChatWithCorrespondent = (correspondent) => {
        setSelectedCorrespondent(correspondent);
        setChatVisible(true);
    };

    // Close chat window
    const closeChatWindow = () => {
        setChatVisible(false);
        setSelectedCorrespondent(null);
    };

    return (
        <div>
            {/* Floating Chat Icon */}
            {isLoggedIn && (
                <div
                    style={{
                        position: 'fixed',
                        bottom: '20px',
                        right: '20px',
                        zIndex: 1000,
                    }}
                >
                    <Button
                        color="primary"
                        className="rounded-circle"
                        style={{ width: '60px', height: '60px' }}
                        onClick={() => setChatVisible(!chatVisible)}
                    >
                        💬
                    </Button>
                </div>
            )}

            {/* Correspondents List */}
            <Modal isOpen={chatVisible} toggle={closeChatWindow} size="sm">
                <ModalHeader toggle={closeChatWindow}>
                    <h5>Chats</h5>
                </ModalHeader>
                <ModalBody>
                    <ListGroup>
                        {correspondents.map((correspondent) => (
                            <ListGroupItem
                                key={correspondent.id}
                                className="d-flex justify-content-between align-items-center"
                                action
                                onClick={() => openChatWithCorrespondent(correspondent)}
                            >
                                {correspondent.name}
                            </ListGroupItem>
                        ))}
                    </ListGroup>
                </ModalBody>
            </Modal>

            {/* Chat Window */}
            {selectedCorrespondent && (
                <Modal isOpen={!!selectedCorrespondent} toggle={closeChatWindow} size="lg">
                    <ModalHeader toggle={closeChatWindow}>
                        Chat with {selectedCorrespondent.name}
                    </ModalHeader>
                    <ModalBody>
                        <div
                            style={{
                                height: '400px',
                                overflowY: 'auto',
                                marginBottom: '20px',
                                padding: '10px',
                                backgroundColor: '#f5f5f5', // light gray background
                                borderRadius: '8px', // rounded corners for the chat container
                                display: 'flex',
                                flexDirection: 'column-reverse', // Keeps scroll at the bottom
                            }}
                            onClick={handleScroll}
                            onLoad={handleScroll}
                        >
                            {messages
                                .filter((msg) => {
                                    if (selectedCorrespondent.name === 'Group') {
                                        return msg.type === 'group';
                                    } else {
                                        return (
                                            (msg.receiver === selectedCorrespondent.id || msg.sender === selectedCorrespondent.id) &&
                                            msg.type === 'single'
                                        );
                                    }
                                })
                                .map((msg, index) => (
                                    <div
                                        key={index}
                                        style={{
                                            margin: '10px 0',
                                            display: 'flex',
                                            flexDirection: msg.sender === personId ? 'row-reverse' : 'row', // Align messages based on sender
                                            justifyContent: msg.sender === personId ? 'flex-end' : 'flex-start',
                                            alignItems: 'center', // Align avatars and message boxes vertically
                                        }}
                                    >
                                        <div
                                            style={{
                                                maxWidth: '80%',
                                                padding: '10px',
                                                borderRadius: '15px',
                                                backgroundColor: msg.sender === personId ? '#0084FF' : '#E4E6EB', // Blue for sent, light gray for received
                                                color: msg.sender === personId ? 'white' : 'black',
                                                position: 'relative',
                                                wordWrap: 'break-word',
                                                fontSize: '14px',
                                                fontFamily: 'Arial, sans-serif',
                                                boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)', // Subtle shadow for message bubble
                                            }}
                                        >
                                            <p>{msg.message}</p>

                                            {msg.date < lastSeen.get(selectedCorrespondent.id) && msg.sender === personId && (
                                                <span
                                                    style={{
                                                        color: 'gray',
                                                        fontSize: '12px',
                                                        position: 'absolute',
                                                        bottom: '5px',
                                                        right: '10px',
                                                    }}
                                                >
                                                    Seen
                                                </span>
                                            )}
                                        </div>

                                        {/* Optional Avatar for sender */}
                                        <div
                                            style={{
                                                width: '30px',
                                                height: '30px',
                                                borderRadius: '50%',
                                                backgroundColor: msg.sender === personId ? '#0084FF' : '#E4E6EB',
                                                color: 'white',
                                                display: 'flex',
                                                justifyContent: 'center', // Center the avatar
                                                alignItems: 'center',
                                                marginTop: '10px',
                                                marginLeft: msg.sender === personId ? '0' : '10px',
                                                marginRight: msg.sender === personId ? '10px' : '0',
                                                fontWeight: 'bold',
                                            }}
                                        >
                                            {msg.sender === personId ? 'Y' : selectedCorrespondent.name[0]}
                                        </div>
                                    </div>
                                ))}
                        </div>

                        {typingCorrespondents.includes(selectedCorrespondent.id) && (
                            <div style={{ fontStyle: 'italic', color: 'gray' }}>
                                {selectedCorrespondent.name} is typing...
                            </div>
                        )}
                        <Input
                            type="text"
                            placeholder="Type your message..."
                            value={inputMessage}
                            onChange={(e) => setInputMessage(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                            onInput={handleTyping}
                            disabled={selectedCorrespondent?.name === 'Group' && sessionStorage.getItem('role') === 'ROLE_USER'}
                        />
                        <Button color="primary" onClick={handleSendMessage} style={{ marginTop: '10px' }}>
                            Send
                        </Button>
                    </ModalBody>
                </Modal>
            )}

        </div>
    );
};

export default Chat;
