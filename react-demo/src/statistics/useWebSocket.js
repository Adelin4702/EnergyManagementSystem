// useWebSocket.js
import { useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const useWebSocket = (userId, onMessageReceived) => {
    const stompClient = useRef(null);

    useEffect(() => {
        // Connect to the WebSocket endpoint
        const jwtToken = sessionStorage.getItem('jwt');
        const headers = {
            Authorization: `Bearer ${jwtToken}`,
        };
        const socket = new SockJS("http://localhost:8082/api-monitor/ws");
        stompClient.current = Stomp.over(socket);

        stompClient.current.connect(headers, (frame) => {
            console.log("Connected: " + frame);

            // Subscribe to the user-specific topic
            stompClient.current.subscribe(`/topic/user/${userId}`, (message) => {
                onMessageReceived(JSON.parse(message.body));
            });
        });

        return () => {
            if (stompClient.current) {
                stompClient.current.disconnect();
            }
        };
    }, [userId, onMessageReceived]);

    // Function to send a message
    const sendMessage = (message) => {
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.send(
                `/app/send/${userId}`,
                {},
                JSON.stringify(message)
            );
        }
    };

    return { sendMessage };
};

export default useWebSocket;
