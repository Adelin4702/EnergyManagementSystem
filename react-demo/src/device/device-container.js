import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import { Button, Card, CardHeader, Col, Row } from 'reactstrap';
import DeviceForm from "./components/device-form";
import * as API_USERS from "./api/device-api";
import * as API_USERS_PERSONS from "../person/api/person-api";
import DeviceTable from "./components/device-table";
import styles from './style/style.module.css';

class DeviceContainer extends React.Component {
    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reload = this.reload.bind(this);
        this.state = {
            selected: false,
            collapseForm: false,
            tableData: [],
            personData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null
        };
    }

    componentDidMount() {
        this.fetchDevices();
        this.fetchPersons();
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.selected !== this.state.selected && this.state.selected) {
            this.fetchPersons();
        }
    }

    fetchDevices() {
        return API_USERS.getDevices((result, status, err) => {
            if (result !== null && status === 200) {
                this.setState({
                    tableData: result,
                    isLoaded: true
                });
            } else {
                this.setState({
                    errorStatus: status,
                    error: err
                });
            }
        });
    }

    fetchPersons() {
        return API_USERS_PERSONS.getPersons((result, status, err) => {
            if (result !== null && status === 200) {
                this.setState({
                    personData: result,
                    isLoaded: true
                });
            } else {
                this.setState({
                    errorStatus: status,
                    error: err
                });
            }
        });
    }

    toggleForm() {
        this.setState({ selected: !this.state.selected });
    }

    reload() {
        this.setState({ isLoaded: false });
        this.fetchDevices();
        this.fetchPersons();
    }

    render() {
        return (
            <div className={styles.deviceContainer}>
                <Card className={styles.card}>
                    <CardHeader className={styles.cardHeader}>
                        <strong>Device Management</strong>
                    </CardHeader>
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button className={styles.addDeviceButton} onClick={this.toggleForm}>
                                Add Device
                            </Button>
                        </Col>
                    </Row>
                    <div className={styles.tableContainer}>
                        {this.state.isLoaded && (
                            <DeviceTable
                                tableData={this.state.tableData}
                                reloadHandler={this.reload}
                                toggleForm={this.toggleForm}
                                isOpen={this.state.selected}
                                personData={this.state.personData}
                            />
                        )}
                        {this.state.errorStatus > 0 && (
                            <APIResponseErrorMessage
                                className={styles.errorMessage}
                                errorStatus={this.state.errorStatus}
                                error={this.state.error}
                            />
                        )}
                    </div>
                </Card>
                <DeviceForm
                    reloadHandler={this.reload}
                    toggleForm={this.toggleForm}
                    isOpen={this.state.selected}
                    personData={this.state.personData}
                />
            </div>
        );
    }
}

export default DeviceContainer;
