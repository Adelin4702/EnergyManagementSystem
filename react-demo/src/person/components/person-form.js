import React from 'react';
import validate from "./validators/person-validators";
import Button from "react-bootstrap/Button";
import * as API_USERS from "../api/person-api";
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import { Col, Row, FormGroup, Input, Label } from 'reactstrap';

class PersonForm extends React.Component {
    constructor(props) {
        super(props);
        this.toggleForm = this.toggleForm.bind(this);
        this.reloadHandler = this.props.reloadHandler;

        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: false,
            formControls: {
                name: {
                    value: props.person ? props.person.name : '',
                    placeholder: 'What is your name?...',
                    valid: !!props.person,
                    touched: false,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                email: {
                    value: props.person ? props.person.email : '',
                    placeholder: 'Email...',
                    valid: !!props.person,
                    touched: false,
                    validationRules: {
                        emailValidator: true
                    }
                },
                password: {
                    value: '',
                    placeholder: props.person ? 'Enter new password (optional)' : 'Enter password',
                    valid: !props.person,
                    touched: false
                },
                admin: {
                    value: props.person ? props.person.admin : '',
                    placeholder: 'Are you an admin?',
                    valid: !!props.person,
                    touched: false
                }
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    toggleForm() {
        this.setState({ collapseForm: !this.state.collapseForm });
    }

    handleChange = event => {
        const name = event.target.name;
        const value = event.target.value;
        const updatedControls = this.state.formControls;
        const updatedFormElement = updatedControls[name];

        updatedFormElement.value = value;
        updatedFormElement.touched = true;
        updatedFormElement.valid = validate(value, updatedFormElement.validationRules);
        updatedControls[name] = updatedFormElement;

        let formIsValid = true;
        for (let updatedFormElementName in updatedControls) {
            formIsValid = updatedControls[updatedFormElementName].valid && formIsValid;
        }

        this.setState({
            formControls: updatedControls,
            formIsValid: formIsValid
        });
    };

    registerPerson(person) {
        return API_USERS.postPerson(person, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully inserted person with id: " + result);
                this.reloadHandler();
                this.toggleForm();
            } else {
                this.setState({
                    errorStatus: status,
                    error: error
                });
            }
        });
    }

    updatePerson(person) {
        return API_USERS.updatePerson(person, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully updated person with id: " + person.id);
                this.reloadHandler();
                this.toggleForm();
            } else {
                this.setState({
                    errorStatus: status,
                    error: error
                });
            }
        });
    }

    handleSubmit() {
        let person = {
            id: this.props.person ? this.props.person.id : null,
            name: this.state.formControls.name.value,
            email: this.state.formControls.email.value,
            password: this.state.formControls.password.value,
            admin: this.state.formControls.admin.value
        };
        console.log(person);

        if (this.props.person) {
            this.updatePerson(person);
        } else {
            this.registerPerson(person);
        }
    }

    render() {
        return (
            <div>
                <FormGroup id='name'>
                    <Label for='nameField'> Name: </Label>
                    <Input
                        name='name'
                        id='nameField'
                        placeholder={this.state.formControls.name.placeholder}
                        onChange={this.handleChange}
                        defaultValue={this.state.formControls.name.value}
                        touched={this.state.formControls.name.touched ? 1 : 0}
                        valid={this.state.formControls.name.valid}
                        required
                    />
                    {this.state.formControls.name.touched && !this.state.formControls.name.valid &&
                        <div className={"error-message row"}> * Name must have at least 3 characters </div>}
                </FormGroup>
                <FormGroup id='email'>
                    <Label for='emailField'> Email: </Label>
                    <Input
                        name='email'
                        id='emailField'
                        placeholder={this.state.formControls.email.placeholder}
                        onChange={this.handleChange}
                        defaultValue={this.state.formControls.email.value}
                        touched={this.state.formControls.email.touched ? 1 : 0}
                        valid={this.state.formControls.email.valid}
                        required
                    />
                    {this.state.formControls.email.touched && !this.state.formControls.email.valid &&
                        <div className={"error-message"}> * Email must have a valid format</div>}
                </FormGroup>
                <FormGroup id='password'>
                    <Label for='passwordField'> Password: </Label>
                    <Input
                        name='password'
                        id='passwordField'
                        placeholder={this.state.formControls.password.placeholder}
                        type='password'
                        onChange={this.handleChange}
                        defaultValue={this.state.formControls.password.value}
                        touched={this.state.formControls.password.touched ? 1 : 0}
                        valid={this.state.formControls.password.valid}
                    />
                </FormGroup>
                <FormGroup id='admin'>
                    <Label for='adminField'> Admin: </Label>
                    <Input
                        name='admin'
                        id='adminField'
                        placeholder={this.state.formControls.admin.placeholder}
                        type="select"
                        onChange={this.handleChange}
                        defaultValue={this.state.formControls.admin.value}
                        touched={this.state.formControls.admin.touched ? 1 : 0}
                        valid={this.state.formControls.admin.valid}
                        required
                    >
                        <option value="false">NO</option>
                        <option value="true">YES</option>
                    </Input>
                </FormGroup>
                <Row>
                    <Col sm={{ size: '4', offset: 8 }}>
                        <Button type={"submit"} disabled={!this.state.formIsValid} onClick={this.handleSubmit}>
                            {this.props.person ? "Update" : "Submit"}
                        </Button>
                    </Col>
                </Row>
                {
                    this.state.errorStatus > 0 &&
                    <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error} />
                }
            </div>
        );
    }
}

export default PersonForm;