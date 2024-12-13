import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import LoginForm from "./components/login-form";
import * as API_USERS from "./api/login-api";
import DeviceForm from "./components/device-form";
import styles from './style/style.module.css';

class PersonContainer extends React.Component {

    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reload = this.reload.bind(this);
        this.state = {
            selected: false,
            collapseForm: false,
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null
        };
    }

    componentDidMount() {
        this.logIn();
    }

    logIn() {
        return API_USERS.getPersons((result, status, err) => {
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
        this.setState({
            isLoaded: false
        });
        this.toggleForm();
        this.logIn();
    }

    render() {
        return (
            <div className={styles['person-container']}>
                <div className={styles['login-form-container']}>
                    <LoginForm reloadHandler={this.reload} />
                </div>

                <Modal isOpen={this.state.selected} toggle={this.toggleForm} className={styles['modal-container']} size="lg">
                    <ModalHeader toggle={this.toggleForm} className={styles['modal-header']}>
                        Register:
                    </ModalHeader>
                    <ModalBody className={styles['modal-body']}>
                        <DeviceForm reloadHandler={this.reload} toggleForm={this.toggleForm} />
                    </ModalBody>
                </Modal>

                {this.state.errorStatus > 0 && (
                    <div className={styles['error-message']}>
                        <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error} />
                    </div>
                )}
            </div>

        )
    }
}

export default PersonContainer;
