import React from "react";
import Table from "../../commons/tables/table";
import { deletePerson } from "../api/person-api";
import styles from "../../commons/styles/project-style.module.css";

class PersonTable extends React.Component {
    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.columns = [
            { Header: 'Id', accessor: 'id' },
            { Header: 'Name', accessor: 'name' },
            { Header: 'Email', accessor: 'email' },
            { Header: 'Admin', accessor: (row) => (row.admin ? 'Yes' : 'No'), id: 'admin' },
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
        this.filters = [{ accessor: 'name' }];
    }

    handleDelete(row) {
        if (row && row.id) {
            deletePerson({ id: row.id }, (response, status, error) => {
                if (status === 200 || status === 204) {
                    window.location.reload();
                }
            });
        }
    }

    handleUpdate(row) {
        this.props.onPersonSelect(row);
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

export default PersonTable;
