import React, { useEffect, useState, useRef } from 'react';
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { withRouter } from 'react-router-dom';
import {
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Nav,
  Navbar,
  NavbarBrand,
  UncontrolledDropdown,
  Button,
  Badge
} from 'reactstrap';
import logo from './commons/images/icon.png';
import bellIcon from './commons/images/bell-icon.png';
import userIcon from './commons/images/user-icon.png';
import { jwtDecode } from 'jwt-decode';
import './commons/styles/project-style.module.css';

const NavigationBar = (props) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const stompClientMonitor = useRef(null);
  const personId = sessionStorage.getItem('personId');
  const notificationServerUrl = 'http://localhost:80/api-monitor/ws';

  // Initialize login state
  useEffect(() => {
    checkLoginStatus();
  }, []);

  useEffect(() => {
    if (isLoggedIn && personId) {
      setupWebSocketConnections();
    }
    return () => {
      disconnectWebSocket();
    };
  }, [isLoggedIn, personId]);

  // Check if user is logged in
  const checkLoginStatus = () => {
    const token = sessionStorage.getItem('jwt');
    if (token) {
      setIsLoggedIn(true);
      const decodedToken = jwtDecode(token);
      setUserName(decodedToken.username || 'User');
      setIsAdmin(decodedToken.admin || false);
    }
  };

  // WebSocket setup for notification
  const setupWebSocketConnections = () => {
    const jwtToken = sessionStorage.getItem('jwt');
    const headers = {
      Authorization: `Bearer ${jwtToken}`,
    };
    console.log("JWT -- in notification : ", headers);
    // Notification WebSocket
    const notificationSocket = new SockJS(notificationServerUrl);
    stompClientMonitor.current = Stomp.over(notificationSocket);
    stompClientMonitor.current.connect({ Authorization: `Bearer ${jwtToken}` }, (frame) => {
      console.log('Connected to Notifications: ' + frame);
      // Subscribe to user-specific notification topic
      stompClientMonitor.current.subscribe(`/topic/alert/${personId}`, (message) => {
        const notification = message.body;
        setNotifications((prevNotifications) => [...prevNotifications, notification]);
      });
    });

    stompClientMonitor.current.onStompError = (frame) => {
      console.error('STOMP Error in Notifications:', frame);
    };
  };

  const disconnectWebSocket = () => {
    if (stompClientMonitor.current) {
      stompClientMonitor.current.disconnect();
      stompClientMonitor.current = null;
    }
  };



  // Log out user
  const logOut = () => {
    sessionStorage.clear();
    setIsLoggedIn(false);
    setUserName('');
    setIsAdmin(false);
    setNotifications([]);
    props.history.push('/');
  };

  return (
    <div>
      <Navbar color="dark" light expand="md">
        <NavbarBrand href="/">
          <img src={logo} width="35" height="35" alt="Logo" />
        </NavbarBrand>
        <Nav className="navbar-nav mr-auto" navbar>
          <UncontrolledDropdown nav inNavbar>
            <DropdownToggle nav caret style={{ color: 'white' }}>
              Menu
            </DropdownToggle>
            <DropdownMenu>
              {isAdmin ? (
                <>
                  <DropdownItem>
                    <Button href="/person">Persons</Button>
                  </DropdownItem>
                  <DropdownItem>
                    <Button href="/device">Devices</Button>
                  </DropdownItem>
                </>
              ) : (
                <>
                  <DropdownItem>
                    <Button href="/myDevice">My Devices</Button>
                  </DropdownItem>
                  <DropdownItem>
                    <Button href="/ws">Statistics(WS)</Button>
                  </DropdownItem>
                </>
              )}
            </DropdownMenu>
          </UncontrolledDropdown>
        </Nav>
        <Nav className="navbar-nav">

          {/* General Notifications */}
          <UncontrolledDropdown nav inNavbar>
            <DropdownToggle nav caret>
              <img src={bellIcon} width="30" height="30" alt="Notifications" />
              {notifications.length > 0 && <Badge color="danger">{notifications.length}</Badge>}
            </DropdownToggle>
            <DropdownMenu right>
              {notifications.length > 0 ? (
                notifications.map((notification, index) => (
                  <DropdownItem key={index}>{notification}</DropdownItem>
                ))
              ) : (
                <DropdownItem>No new notifications</DropdownItem>
              )}
            </DropdownMenu>
          </UncontrolledDropdown>
          {/* User Account */}
          {isLoggedIn ? (
            <UncontrolledDropdown nav inNavbar>
              <DropdownToggle nav caret>
                <img src={userIcon} width="30" height="30" alt="User" />
                <span>{userName}</span>
              </DropdownToggle>
              <DropdownMenu right>
                <DropdownItem onClick={logOut}>Log Out</DropdownItem>
              </DropdownMenu>
            </UncontrolledDropdown>
          ) : (
            <Button color="secondary" onClick={() => props.history.push('/auth/login')}>
              Log In
            </Button>
          )}
        </Nav>
      </Navbar>

    </div>
  );
};

export default withRouter(NavigationBar);
