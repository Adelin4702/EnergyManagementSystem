import React from 'react';
import { Modal, ModalHeader, ModalBody, FormGroup, Input, Label } from 'reactstrap';
import validate from "./validators/device-validator";
import Button from "react-bootstrap/Button";
import * as API_DEVICES from "../api/device-api";
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import { Col, Row } from 'reactstrap';

class DeviceForm extends React.Component {
    constructor(props) {
        super(props);
        this.toggleForm = this.props.toggleForm;
        this.reloadHandler = this.props.reloadHandler;
        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: false,
            formControls: {
                description: {
                    value: props.device ? props.device.description : '',
                    placeholder: 'What is your device description?',
                    valid: !!props.device,
                    touched: false,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                address: {
                    value: props.device ? props.device.address : '',
                    placeholder: 'Device address...',
                    valid: !!props.device,
                    touched: false,
                    validationRules: {
                        minLength: 5,
                        isRequired: true
                    }
                },
                maxHourlyEnergyConsumption: {
                    value: props.device ? props.device.maxHourlyEnergyConsumption : '',
                    placeholder: 'Max Hourly Energy Consumption',
                    valid: !!props.device,
                    touched: false,
                    validationRules: {
                        isRequired: true
                    }
                },
                person: {
                    value: props.device ? props.device.person : '',
                    placeholder: 'Person',
                    valid: !!props.device,
                    touched: false,
                }
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.updateDevice = this.updateDevice.bind(this);
        this.registerDevice = this.registerDevice.bind(this);
    }

    componentDidUpdate(prevProps) {
        if (prevProps.personData !== this.props.personData) {
            this.setState({ personData: this.props.personData });
        }
    }

    handleChange(event) {
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
    }

    registerDevice(device) {
        return API_DEVICES.postDevice(device, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully inserted device with id: " + result);
                this.reloadHandler();
                this.toggleForm();
            } else {
                this.setState({ errorStatus: status, error: error });
            }
        });
    }

    updateDevice(device) {
        return API_DEVICES.updateDevice(device, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                console.log("Successfully updated device with id: " + device.id);
                this.reloadHandler();
                this.toggleForm();
            } else {
                this.setState({ errorStatus: status, error: error });
            }
        });
    }

    handleSubmit(event) {
        event.preventDefault(); // Add preventDefault to avoid page reload
        let device = {
            id: this.props.device ? this.props.device.id : null,
            description: this.state.formControls.description.value,
            address: this.state.formControls.address.value,
            maxHourlyEnergyConsumption: this.state.formControls.maxHourlyEnergyConsumption.value,
            person: this.state.formControls.person.value
        };
        console.log(device);
        if (this.props.device) {
            this.updateDevice(device);
        } else {
            this.registerDevice(device);
        }
    }

    render() {
        return (
            <Modal isOpen={this.props.isOpen} toggle={this.toggleForm}>
                <ModalHeader toggle={this.toggleForm}>
                    {this.props.device ? "Update Device" : "Add Device"}
                </ModalHeader>
                <ModalBody>
                    <FormGroup id='description'>
                        <Label for='descriptionField'> Description: </Label>
                        <Input
                            name='description'
                            id='descriptionField'
                            placeholder={this.state.formControls.description.placeholder}
                            onChange={this.handleChange}
                            defaultValue={this.state.formControls.description.value}
                            touched={this.state.formControls.description.touched ? 1 : 0}
                            valid={this.state.formControls.description.valid}
                            required
                        />
                        {this.state.formControls.description.touched && !this.state.formControls.description.valid &&
                            <div className={"error-message row"}> * Description must have at least 3 characters </div>}
                    </FormGroup>
                    <FormGroup id='address'>
                        <Label for='addressField'> Address: </Label>
                        <Input
                            name='address'
                            id='addressField'
                            placeholder={this.state.formControls.address.placeholder}
                            onChange={this.handleChange}
                            defaultValue={this.state.formControls.address.value}
                            touched={this.state.formControls.address.touched ? 1 : 0}
                            valid={this.state.formControls.address.valid}
                            required
                        />
                        {this.state.formControls.address.touched && !this.state.formControls.address.valid &&
                            <div className={"error-message"}> * Address must have at least 5 characters</div>}
                    </FormGroup>
                    <FormGroup id='maxHourlyEnergyConsumption'>
                        <Label for='maxHourlyEnergyConsumptionField'> Max Hourly Energy Consumption: </Label>
                        <Input
                            name='maxHourlyEnergyConsumption'
                            id='maxHourlyEnergyConsumptionField'
                            placeholder={this.state.formControls.maxHourlyEnergyConsumption.placeholder}
                            onChange={this.handleChange}
                            defaultValue={this.state.formControls.maxHourlyEnergyConsumption.value}
                            touched={this.state.formControls.maxHourlyEnergyConsumption.touched ? 1 : 0}
                            valid={this.state.formControls.maxHourlyEnergyConsumption.valid}
                            required
                        />
                    </FormGroup>
                    <FormGroup id='person'>
                        <Label for='personField'> Person: </Label>
                        <Input
                            type='select'
                            name='person'
                            id='personField'
                            onChange={this.handleChange}
                            defaultValue={this.state.formControls.person.value}
                            touched={this.state.formControls.person.touched ? 1 : 0}
                            valid={this.state.formControls.person.valid}
                            required
                        >
                            <option value=''>None</option>
                            {this.props.personData.map((person, index) => (
                                <option key={index} value={person.id}>{person.name}</option>
                            ))}
                        </Input>
                    </FormGroup>
                    <Row>
                        <Col sm={{ size: '4', offset: 8 }}>
                            <Button type={"submit"} disabled={!this.state.formIsValid} onClick={this.handleSubmit}>
                                {this.props.device ? "Update" : "Submit"}
                            </Button>
                        </Col>
                    </Row>
                    {this.state.errorStatus > 0 &&
                        <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error} />}
                </ModalBody>
            </Modal>
        );
    }
}

export default DeviceForm;
