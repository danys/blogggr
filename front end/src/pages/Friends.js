import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import {get, post} from '../utils/ajax';
import {red}  from '../consts/Constants';
import Link from '../components/navigation/Link'
import {Sidebar} from '../components/sidebar/Sidebar'
import PostOptionsSidebarBody from '../components/sidebar/PostOptionsSidebarBody'
import {PostFormModal} from '../components/modal/PostFormModal'
import {CommentFormModal} from '../components/modal/CommentFormModal'
import {blue, green} from '../consts/Constants'
import {put, del} from '../utils/ajax'
import {InputHeaderCell} from "../components/table/Cells";
import debounce from 'lodash/debounce'

import { Table, Column, Cell } from 'fixed-data-table-2';

class Friends extends React.Component{

    constructor(props){
        super(props);
        this.friendsURL = "/api/v1.0/friends/";
        this.usersURL = "/api/v1.0/users";
        this.state = {
            friendsSearchData: [],
            searchParams: {
                firstName: '',
                lastName: '',
                email: '',
                length: 10
            }
        };
        this.debouncedFetchUsers = debounce(this.fetchUsers,200);
    }

    fetchUsers(){
        get(this.usersURL,
            this.state.searchParams,
            (data)=>{this.setState({friendsSearchData: data.data.pageItems})},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error retrieving users!', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUsers();
    }

    searchFormChange(field, value){
        let searchParams = this.state.searchParams;
        searchParams[field] = value;
        this.setState({searchParams: searchParams}, this.debouncedFetchUsers);
    }

    render() {
        return (
            <div className="row">
                <div className="col-lg-6">
                    <h1>Search for a user</h1>
                    <Table
                        rowsCount={this.state.friendsSearchData.length}
                        rowHeight={50}
                        width={600}
                        height={250}
                        headerHeight={50}>
                        <Column
                            header={<Cell>Image</Cell>}
                            cell={props => (
                                <Cell {...props}>
                                    image
                                </Cell>
                            )}
                            width={100}
                        />
                        <Column
                            header={<InputHeaderCell field="First name" onChange={this.searchFormChange.bind(this, 'firstName')} />}
                            cell={props => (
                                <Cell {...props}>
                                    {this.state.friendsSearchData[props.rowIndex].firstName}
                                </Cell>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="Last name" onChange={this.searchFormChange.bind(this, 'lastName')} />}
                            cell={props => (
                                <Cell {...props}>
                                    {this.state.friendsSearchData[props.rowIndex].lastName}
                                </Cell>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="E-mail" onChange={this.searchFormChange.bind(this, 'email')} />}
                            cell={props => (
                                <Cell {...props}>
                                    {this.state.friendsSearchData[props.rowIndex].email}
                                </Cell>
                            )}
                            width={200}
                        />
                    </Table>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token,
    email: state.session.email
});


export default withRouter(connect(
    mapStateToProps,
    null
)(Friends));
