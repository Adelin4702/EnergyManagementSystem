import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import { Button, Card, CardHeader, Col, Row } from 'reactstrap';
import MyDeviceTable from "./components/my-device-table";
import styles from './style/style.module.css'; // Import your styles
import * as API_USERS from './api/my-device-api'

class MyDeviceContainer extends React.Component {
    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reload = this.reload.bind(this);

        this.state = {
            selected: false,
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null
        };
    }

    componentDidMount() {
        const id = sessionStorage.getItem("personId");
        this.fetchDevices(id);
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.selected !== this.state.selected && this.state.selected) {
            const id = sessionStorage.getItem("personId");
            this.fetchDevices(id);
        }
    }

    fetchDevices(params) {
        return API_USERS.getDeviceByPersonId(params, (result, status, err) => {
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

    toggleForm() {
        this.setState({ selected: !this.state.selected });
    }

    reload() {
        this.setState({ isLoaded: false });
        const id = sessionStorage.getItem("personId");
        this.fetchDevices(id);
    }

    render() {
        return (
            <div className={styles.container}>
                <Card className={styles.card}>
                    <CardHeader className={styles.cardHeader}>
                        <strong>Device Management</strong>
                    </CardHeader>
                    <div className={styles.cardBody}>
                        
                        <Row className={styles.row}>
                            <Col sm={{ size: '10', offset: 1 }} className={styles.tableContainer}>
                                {this.state.isLoaded && (
                                    <MyDeviceTable
                                        tableData={this.state.tableData}
                                        reloadHandler={this.reload}
                                        toggleForm={this.toggleForm}
                                        isOpen={this.state.selected}
                                    />
                                )}
                                {this.state.errorStatus > 0 && (
                                    <APIResponseErrorMessage
                                        errorStatus={this.state.errorStatus}
                                        error={this.state.error}
                                    />
                                )}
                            </Col>
                        </Row>
                    </div>
                </Card>
            </div>
        );
    }
}

export default MyDeviceContainer;
