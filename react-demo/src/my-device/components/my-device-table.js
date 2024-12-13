import React from "react";
import Table from "../../commons/tables/table";
import { useHistory } from "react-router-dom";

class MyDeviceTable extends React.Component {
    constructor(props) {
        super(props);
        this.columns = [
            { Header: 'Id', accessor: 'id' },
            { Header: 'Description', accessor: 'description' },
            { Header: 'Address', accessor: 'address' },
            { Header: 'MHEC', accessor: 'maxHourlyEnergyConsumption' },
        ];
        this.filters = [{ accessor: 'address' }];
    }

    handleView(deviceId) {
        // Store device ID in session storage
        sessionStorage.setItem('selectedDeviceId', deviceId);
        // Redirect to the new page
        window.location.href = '/device-details';
    }

    render() {
        return (
            <div>
                <Table
                    data={this.props.tableData}
                    columns={this.columns}
                    search={this.filters}
                    pageSize={5}
                />
            </div>
        );
    }
}

export default MyDeviceTable;
