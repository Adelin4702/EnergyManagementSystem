import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import { Button, Card, CardHeader, Col, Modal, ModalBody, ModalHeader, Row } from 'reactstrap';
import PersonForm from "./components/person-form";
import * as API_USERS from "./api/person-api";
import PersonTable from "./components/person-table";
import styles from './style/style.module.css'; // Import CSS module

class PersonContainer extends React.Component {
    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reload = this.reload.bind(this);
        this.selectPerson = this.selectPerson.bind(this);
        this.state = {
            selected: false,
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null,
            personToUpdate: null // Store selected person for updating
        };
    }

    componentDidMount() {
        this.fetchPersons();
    }

    fetchPersons() {
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
        this.setState({ selected: !this.state.selected, personToUpdate: null });
    }

    reload() {
        this.setState({ isLoaded: false });
        this.fetchPersons();
        this.setState({ selected: false });
    }

    selectPerson(person) {
        this.setState({ personToUpdate: person, selected: true });
    }

    render() {
        return (
            <div className={styles.container}>
                <Card className={styles.card}>
                    <CardHeader className={styles.cardHeader}>
                        <strong>Person Management</strong>
                    </CardHeader>
                    <Row className={styles.row}>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button 
                                className={styles.addButton}
                                onClick={this.toggleForm}
                            >
                                Add Person
                            </Button>
                        </Col>
                    </Row>
                    <Row className={styles.row}>
                        <Col sm={{ size: '8', offset: 1 }}>
                            {this.state.isLoaded && (
                                <div className={styles.tableContainer}>
                                    <PersonTable
                                        tableData={this.state.tableData}
                                        onPersonSelect={this.selectPerson}
                                    />
                                </div>
                            )}
                            {this.state.errorStatus > 0 && (
                                <div className={styles.errorMessage}>
                                    <APIResponseErrorMessage
                                        errorStatus={this.state.errorStatus}
                                        error={this.state.error}
                                    />
                                </div>
                            )}
                        </Col>
                    </Row>
                </Card>

                <Modal isOpen={this.state.selected} toggle={this.toggleForm} size="lg">
                    <ModalHeader toggle={this.toggleForm} className={styles.modalHeader}>
                        {this.state.personToUpdate ? "Update Person" : "Add Person"}
                    </ModalHeader>
                    <ModalBody className={styles.modalBody}>
                        <PersonForm
                            reloadHandler={this.reload}
                            person={this.state.personToUpdate}
                        />
                    </ModalBody>
                </Modal>
            </div>
        );
    }
}

export default PersonContainer;
