import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import {get} from '../utils/ajax';
import {red}  from '../consts/Constants';
import {InputHeaderCell} from "../components/table/Cells";
import debounce from 'lodash/debounce';
import {TextCell} from '../components/table/Cells';
import {getErrorMessage} from '../utils/errorExtractor';

import { Table, Column, Cell } from 'fixed-data-table-2';

class Friends extends React.Component{

    constructor(props){
        super(props);
        this.friendsURL = "/api/v1.0/friends/";
        this.usersURL = "/api/v1.0/users";
        this.state = {
            friendsSearchData: null,
            searchParams: {
                firstName: '',
                lastName: '',
                email: '',
                maxRecordsCount: 10
            }
        };
        this.pagesInTransit={};
        this.debouncedFetchUsers = debounce(this.fetchUsers,200);
        this.fetchUsers = this.fetchUsers.bind(this);
    }

    fetchUsers(pageNumber, clear){
        let requestParams = Object.assign({},this.state.searchParams);
        //First check whether the requested page was already requested but not yet been serviced
        if (this.pagesInTransit.hasOwnProperty(pageNumber)) return;
        this.pagesInTransit[pageNumber] = true;
        if (pageNumber!=='0'){
            const pageN = parseInt(pageNumber);
            const nextPageN = (pageN+1).toString();
            const previousPageN = (pageN-1).toString();
            if (this.state.friendsSearchData!=null && this.state.friendsSearchData.hasOwnProperty(previousPageN)){
                requestParams.after = this.state.friendsSearchData[previousPageN].pageItems[this.state.searchParams.maxRecordsCount-1].userId;
            } else if (this.state.friendsSearchData!=null && this.state.friendsSearchData.hasOwnProperty(nextPageN)){
                requestParams.before = this.state.friendsSearchData[nextPageN].pageItems[0].userId;
            } else {
                throw "Invalid pageNumber: previous or next pageNumber does not exist!";
            }
        }
        get(this.usersURL,
            requestParams,
            (data)=>{
                let friendsData = (this.state.friendsSearchData==null || clear)?{}:this.state.friendsSearchData;
                friendsData[pageNumber] = data.data;
                delete this.pagesInTransit[pageNumber];
                this.setState({friendsSearchData: friendsData});
            },
            (jqXHR)=>{
                this.props.showOverlayMsg('Error retrieving users!', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUsers('0');
    }

    searchFormChange(field, value){
        let searchParams = this.state.searchParams;
        searchParams[field] = value;
        this.setState({searchParams: searchParams}, () => {this.debouncedFetchUsers('0',true)});
    }

    render() {
        const rowsCount = (this.state.friendsSearchData!=null && this.state.friendsSearchData['0'].pageData)?this.state.friendsSearchData['0'].pageData.filteredCount:0;
        const rowData = (this.state.friendsSearchData!=null)?this.state.friendsSearchData:null;
        return (
            <div className="row">
                <div className="col-lg-6">
                    <h1>Search for a user</h1>
                    <Table
                        rowsCount={rowsCount}
                        rowHeight={50}
                        width={650}
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
                                <TextCell {...props} loadUsers={this.fetchUsers} field='firstName' data={rowData} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="Last name" onChange={this.searchFormChange.bind(this, 'lastName')} />}
                            cell={props => (
                                <Cell {...props}>
                                    <TextCell {...props} loadUsers={this.fetchUsers} field='lastName' data={rowData} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                                </Cell>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="E-mail" onChange={this.searchFormChange.bind(this, 'email')} />}
                            cell={props => (
                                <Cell {...props}>
                                    <TextCell {...props} loadUsers={this.fetchUsers} field='email' data={rowData} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                                </Cell>
                            )}
                            width={250}
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
