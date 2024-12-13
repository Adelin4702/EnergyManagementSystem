import React from 'react';

import BackgroundImg from '../commons/images/th.jpeg';

import { Button, Container, Jumbotron } from 'reactstrap';

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: "100%",
    height: "1280px",
    backgroundImage: `url(${BackgroundImg})`
};
const textStyle = { color: 'white', };

class Home extends React.Component {


    render() {

        return (

            <div>
                <Jumbotron fluid style={backgroundStyle}>
                    <Container fluid>
                        <h1 className="display-3" style={textStyle}>Integrated Energy Management Platform for Smart Homes</h1>
                        <p className="lead" style={textStyle}>
                            <b>Enabling real-time monitoring of energy consumption, optimizing resource usage, and automating energy-saving actions for sustainable living.</b>
                        </p>
                        <hr className="my-2" />
                        <p style={textStyle}>
                            <b>This project represents the first module of the distributed software system "Integrated Energy Management Platform for Smart Homes," which serves as the final project for the Distributed Systems course.</b>
                        </p>
                        <p className="lead">
                            <Button color="primary" onClick={() => window.open('https://en.wikipedia.org/wiki/Energy_management_system')}>Learn More</Button>
                        </p>
                    </Container>
                </Jumbotron>

            </div>
        )
    };
}

export default Home
