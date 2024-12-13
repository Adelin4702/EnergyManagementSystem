import React from 'react'
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom'
import NavigationBar from './navigation-bar'
import Home from './home/home';
import PersonContainer from './person/person-container'
import Chat from './chat'
import ErrorPage from './commons/errorhandling/error-page';
import styles from './commons/styles/project-style.module.css';
import LoginForm from './login/components/login-form';
import DeviceContainer from './device/device-container';
import MyDeviceContainer from './my-device/my-device-container';
import WebSocketComponent from './statistics/WebSocketComponent';


class App extends React.Component {



    render() {
        return (
            <div className={styles.back}>
                <Router>
                    <div>
                        <NavigationBar />
                        <Chat/>
                        <Switch>

                            <Route
                                exact
                                path='/'
                                render={() => <Home />}
                            />

                            <Route

                                exact
                                path='/person'
                                render={() => {
                                    const role = sessionStorage.getItem('role');
                                    if (role === 'ROLE_ADMIN') {
                                        return <PersonContainer />
                                    } else {
                                        return <Redirect to="/" />
                                    }
                                }}
                            />

                            <Route
                                exact
                                path='/device'
                                render={() => {
                                    const role = sessionStorage.getItem('role');
                                    if (role === 'ROLE_ADMIN') {
                                        return <DeviceContainer />
                                    } else {
                                        return <Redirect to="/" />
                                    }
                                }}
                            />

                            <Route
                                exact
                                path='/auth/login'
                                render={() => <LoginForm />}
                            />

                            <Route
                                exact
                                path='/myDevice'
                                render={() => {
                                    const role = sessionStorage.getItem('role');
                                    if (role === 'ROLE_USER') {
                                        return <MyDeviceContainer />
                                    } else {
                                        return <Redirect to="/" />
                                    }
                                }}
                            />


                            <Route
                                exact
                                path='/ws' 
                                render={() => {
                                    const personId = sessionStorage.getItem('personId');
                                    const role = sessionStorage.getItem('role');
                                    if (role === 'ROLE_USER') {
                                        return <WebSocketComponent personId={personId}/>
                                    } else {
                                        return <Redirect to="/" />
                                    }
                                }}
                            />

                            {/*Error*/}
                            <Route
                                exact
                                path='/error'
                                render={() => <ErrorPage />}
                            />

                            <Route render={() => <ErrorPage />} />
                        </Switch>

                    </div>
                </Router>
            </div>
        )
    };
}

export default App
