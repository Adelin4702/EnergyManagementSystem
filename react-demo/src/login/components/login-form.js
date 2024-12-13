import React from 'react';
import validate from "./validators/login-validators";
import Button from "react-bootstrap/Button";
import * as API_USERS from "../api/login-api";
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import { Col, Row, FormGroup, Input, Label, Container, Card, CardBody, CardTitle, Modal, ModalHeader, ModalBody } from 'reactstrap';
import { withRouter } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import styles from '../style/style.module.css'; // Import the style module

class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.toggleForm = this.toggleForm.bind(this);
    this.toggleRegisterModal = this.toggleRegisterModal.bind(this);
    this.reloadHandler = this.props.reloadHandler;
    this.state = {
      errorStatus: 0,
      error: null,
      formIsValid: false,
      registerModal: false,
      formControls: {
        email: {
          value: '',
          placeholder: 'Email...',
          valid: false,
          touched: false,
          validationRules: {
            emailValidator: true
          }
        },
        password: {
          value: '',
          placeholder: 'Enter password',
          valid: false,
          touched: false
        },
        name: {
          value: '',
          placeholder: 'Name...',
          valid: false,
          touched: false,
          validationRules: {
            minLength: 3,
            isRequired: true
          }
        },
        emailR: {
          value: '',
          placeholder: 'Email...',
          valid: false,
          touched: false,
          validationRules: {
            emailValidator: true
          }
        },
        passwordR: {
          value: '',
          placeholder: 'Enter password',
          valid: false,
          touched: false
        },
        admin: {
          value: 'false',
          placeholder: 'Admin',
          valid: true,
          touched: false
        }
      }
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleRegister = this.handleRegister.bind(this);
    this.logIn = this.logIn.bind(this);
    this.registerPerson = this.registerPerson.bind(this);
    this.redirectHome = this.redirectHome.bind(this);
  }

  toggleForm() {
    this.setState({ collapseForm: !this.state.collapseForm });
  }

  toggleRegisterModal() {
    this.setState({ registerModal: !this.state.registerModal });
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
    this.setState({ formControls: updatedControls, formIsValid: formIsValid });
  };

  redirectHome() {
    this.props.history.push("/");
  }

  logIn(req) {
    return API_USERS.logIn(req, (result, status, error) => {
      if (result !== null && (status === 200 || status === 201)) {
        console.log("Successfully logged in person with jwt: " + result.jwt);
        sessionStorage.setItem("jwt", result.jwt);
        const decodedToken = jwtDecode(result.jwt);
        sessionStorage.setItem("role", decodedToken.role);
        sessionStorage.setItem("personId", decodedToken.personId);
        if (this.reloadHandler) {
          this.reloadHandler();
        }
        this.redirectHome();
      } else {
        this.setState({
          errorStatus: status,
          error: error
        });
      }
    });
  }

  handleSubmit() {
    let req = {
      email: this.state.formControls.email.value,
      password: this.state.formControls.password.value
    };
    console.log(req);
    this.logIn(req);
  }

  registerPerson(person) {
    return API_USERS.register(person, (result, status, error) => {
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

  handleRegister() {
    let person = {
      name: this.state.formControls.name.value,
      email: this.state.formControls.emailR.value,
      password: this.state.formControls.passwordR.value,
      admin: this.state.formControls.admin.value
    };
    console.log(person);
    this.registerPerson(person);
  }

  render() {
    return (
        <div>
      <Container className={styles['login-form-container']}>
        <Row className="justify-content-center">
          <Col md="6">
            <Card className={styles['login-form-card']}>
              <CardBody>
                <CardTitle tag="h3" className={styles['login-form-title']}>Login</CardTitle>
                <div>
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
                      className={styles['login-input']}
                    />
                    {this.state.formControls.email.touched && !this.state.formControls.email.valid &&
                      <div className="error-message">* Email must have a valid format</div>
                    }
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
                      required
                      className={styles['login-input']}
                    />
                  </FormGroup>
                  <Row>
                    <Col sm={{ size: '6' }}>
                      <Button
                        color="primary"
                        type="submit"
                        onClick={this.handleSubmit}
                        className={styles['login-button']}
                      >
                        Log In
                      </Button>
                    </Col>
                    <Col sm={{ size: '6', textAlign: 'right' }}>
                      <Button
                        color="secondary"
                        onClick={this.toggleRegisterModal}
                        className={styles['login-button']}
                      >
                        Register New User
                      </Button>
                    </Col>
                  </Row>
                  {this.state.errorStatus > 0 &&
                    <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error} />
                  }
                </div>
              </CardBody>
            </Card>
          </Col>
        </Row>

        {/* Register Modal */}
        <Modal isOpen={this.state.registerModal} toggle={this.toggleRegisterModal} className={styles['register-modal']}>
          <ModalHeader toggle={this.toggleRegisterModal}>Register New User</ModalHeader>
          <ModalBody className={styles['modal-body']}>
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
                className={styles['login-input']}
              />
              {this.state.formControls.name.touched && !this.state.formControls.name.valid &&
                <div className={"error-message row"}> * Name must have at least 3 characters </div>}
            </FormGroup>
            <FormGroup id='emailR'>
              <Label for='emailFieldR'> Email: </Label>
              <Input
                name='emailR'
                id='emailFieldR'
                placeholder={this.state.formControls.emailR.placeholder}
                onChange={this.handleChange}
                defaultValue={this.state.formControls.emailR.value}
                touched={this.state.formControls.emailR.touched ? 1 : 0}
                valid={this.state.formControls.emailR.valid}
                required
                className={styles['login-input']}
              />
              {this.state.formControls.emailR.touched && !this.state.formControls.emailR.valid &&
                <div className={"error-message"}> * Email must have a valid format</div>}
            </FormGroup>
            <FormGroup id='passwordR'>
              <Label for='passwordRField'> Password: </Label>
              <Input
                name='passwordR'
                id='passwordRField'
                placeholder={this.state.formControls.passwordR.placeholder}
                type='password'
                onChange={this.handleChange}
                defaultValue={this.state.formControls.passwordR.value}
                touched={this.state.formControls.passwordR.touched ? 1 : 0}
                valid={this.state.formControls.passwordR.valid}
                className={styles['login-input']}
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
                className={styles['login-input']}
              >
                <option value="false">NO</option>
                <option value="true">YES</option>
              </Input>
            </FormGroup>
            <Button color="primary" onClick={this.handleRegister} className={styles['login-button']}>Register</Button>
          </ModalBody>
        </Modal>
      </Container>
      </div>
    );
  }
}

export default withRouter(LoginForm);
