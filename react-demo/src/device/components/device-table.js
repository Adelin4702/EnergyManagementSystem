import React from "react";
import Table from "../../commons/tables/table";
import { deleteDevice } from "../api/device-api";
import DeviceForm from "./device-form";
import styles from "../../commons/styles/project-style.module.css";

class DeviceTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedDevice: null,
            showModal: false
        };

        this.handleDelete = this.handleDelete.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.toggleModal = this.toggleModal.bind(this);

        this.columns = [
            { Header: 'Id', accessor: 'id' },
            { Header: 'Description', accessor: 'description' },
            { Header: 'Address', accessor: 'address' },
            { Header: 'MHEC', accessor: 'maxHourlyEnergyConsumption' },
            { Header: 'Person', accessor: (row) => row.person ? row.person : 'N/A', id: 'person.id' },
            {
                Header: "Actions",
                Cell: ({ row }) => (
                    <div>
                        <button className={styles.updateButton} onClick={() => this.handleUpdate(row._original)}>Update</button>
                        <button className={styles.deleteButton} onClick={() => this.handleDelete(row._original)}>Delete</button>
                    </div>
                )
            }
        ];
        this.filters = [{ accessor: 'address' }];
    }

    componentDidUpdate(prevProps) {
        if (prevProps.personData !== this.props.personData) {
            this.setState({ personData: this.props.personData });
        }
    }

    toggleModal() {
        this.setState({ showModal: !this.state.showModal });
        console.log("PERSOANE in tabel", this.props.personData);
    }

    handleUpdate(device) {
        this.setState({ selectedDevice: device, showModal: true });
    }

    handleDelete(device) {
        console.log("Delete device data:", device);
        if (device && device.id) {
            deleteDevice({ id: device.id }, (response, status, error) => {
                if (status === 200 || status === 204) {
                    console.log("Deleted successfully");
                    window.location.reload();
                } else {
                    console.error("Error deleting:", error);
                }
            });
        } else {
            console.error("Device data or ID is missing:", device);
        }
    }

    render() {
        console.log("PERSOANE in tabel", this.props.personData);
        console.log("Rendering table with data:", this.props.tableData);
        return (
            <>
                <div>
                    <Table
                        data={this.props.tableData}
                        columns={this.columns}
                        search={this.filters}
                        pageSize={5}
                    />
                </div>
                <div>
                    {this.state.showModal &&
                        <DeviceForm
                            device={this.state.selectedDevice}
                            toggleForm={this.toggleModal}
                            reloadHandler={this.props.reloadHandler}
                            isOpen={this.state.showModal}
                            personData={this.props.personData}
                        />
                    }
                </div>
            </>
        );
    }
}

export default DeviceTable;
