import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import {get} from '../utils/ajax';
import {red}  from '../consts/Constants';
import {FixedHeaderCell, InputHeaderCell, ImageCell} from "../components/table/Cells";
import debounce from 'lodash/debounce';
import {TextCell} from '../components/table/Cells';
import {getErrorMessage} from '../utils/errorExtractor';

import { Table, Column, Cell } from 'fixed-data-table-2';

class Friends extends React.Component{

    constructor(props){
        super(props);
        this.friendsURL = "/api/v1.0/friends";
        this.usersURL = "/api/v1.0/users";
        this.userImageURL = "/api/v1.0/userimages";
        this.state = {
            friendsData: null,
            friendsMaxPageItems: 1000,
            userSearchData: null,
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
        this.fetchFriends = this.fetchFriends.bind(this);
        this.handleUserImageClick = this.handleUserImageClick.bind(this);
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
            if (this.state.userSearchData!=null && this.state.userSearchData.hasOwnProperty(previousPageN)){
                requestParams.after = this.state.userSearchData[previousPageN].pageItems[this.state.searchParams.maxRecordsCount-1].userId;
            } else if (this.state.userSearchData!=null && this.state.userSearchData.hasOwnProperty(nextPageN)){
                requestParams.before = this.state.userSearchData[nextPageN].pageItems[0].userId;
            } else {
                throw "Invalid pageNumber: previous or next pageNumber does not exist!";
            }
        }
        get(this.usersURL,
            requestParams,
            (data)=>{
                let usersData = (this.state.userSearchData==null || clear)?{}:this.state.userSearchData;
                usersData[pageNumber] = data.data;
                delete this.pagesInTransit[pageNumber];
                this.setState({userSearchData: usersData});
            },
            (jqXHR)=>{
                this.props.showOverlayMsg('Error retrieving users!', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    fetchFriends(){
     get(this.friendsURL,
        {},
        (data)=>{
          this.setState({friendsData: data.data});
        },
        (jqXHR)=>{
          this.props.showOverlayMsg('Error retrieving friends!', getErrorMessage(jqXHR.responseText), red);
        },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUsers('0');
        this.fetchFriends();
    }

    searchFormChange(field, value){
        let searchParams = this.state.searchParams;
        searchParams[field] = value;
        this.setState({searchParams: searchParams}, () => {this.debouncedFetchUsers('0',true)});
    }

    handleUserImageClick(userId){
      this.props.history.push('/users/'+userId);
    }

    render() {
        const rowsCount = (this.state.userSearchData!=null && this.state.userSearchData['0'].pageData)?this.state.userSearchData['0'].pageData.filteredCount:0;
        const rowData = (this.state.userSearchData!=null)?this.state.userSearchData:null;
        const friendsRowsCount = (this.state.friendsData!=null)?this.state.friendsData.length:0;
        const friendsRowData = (this.state.friendsData!=null)?this.state.friendsData:null;
        return (
            <div>
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
                                <ImageCell {...props} loadUsers={this.fetchUsers} imageBaseUrl={this.userImageURL} field='image' width={40} height={40} clickHandler={this.handleUserImageClick} data={rowData} isPaged={true} itemsPerPage={this.state.searchParams.maxRecordsCount} >
                                    image
                                </ImageCell>
                            )}
                            width={100}
                        />
                        <Column
                            header={<InputHeaderCell field="First name" onChange={this.searchFormChange.bind(this, 'firstName')} />}
                            cell={props => (
                                <TextCell {...props} loadUsers={this.fetchUsers} field='firstName' data={rowData} isPaged={true} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="Last name" onChange={this.searchFormChange.bind(this, 'lastName')} />}
                            cell={props => (
                                <Cell {...props}>
                                    <TextCell {...props} loadUsers={this.fetchUsers} field='lastName' data={rowData} isPaged={true} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                                </Cell>
                            )}
                            width={130}
                        />
                        <Column
                            header={<InputHeaderCell field="E-mail" onChange={this.searchFormChange.bind(this, 'email')} />}
                            cell={props => (
                                <Cell {...props}>
                                    <TextCell {...props} loadUsers={this.fetchUsers} field='email' data={rowData} isPaged={true} itemsPerPage={this.state.searchParams.maxRecordsCount}/>
                                </Cell>
                            )}
                            width={250}
                        />
                    </Table>
                </div>
              </div>
              <div className="row">
                <div className="col-lg-6">
                  <h1>Your friends</h1>
                  <Table
                      rowsCount={friendsRowsCount}
                      rowHeight={50}
                      width={650}
                      height={250}
                      headerHeight={50}>
                    <Column
                        header={<Cell>Image</Cell>}
                        cell={props => (
                            <ImageCell {...props} loadUsers={this.fetchFriends} imageBaseUrl={this.userImageURL} field='image' width={40} height={40} clickHandler={this.handleUserImageClick} data={rowData} isPaged={false} itemsPerPage={this.state.friendsMaxPageItems} >
                              image
                            </ImageCell>
                        )}
                        width={100}
                    />
                    <Column
                        header={<FixedHeaderCell field="First name" />}
                        cell={props => (
                            <TextCell {...props} loadUsers={this.fetchFriends} field='firstName' data={friendsRowData} isPaged={false} itemsPerPage={this.state.friendsMaxPageItems}/>
                        )}
                        width={130}
                    />
                    <Column
                        header={<FixedHeaderCell field="Last name" />}
                        cell={props => (
                            <Cell {...props}>
                              <TextCell {...props} loadUsers={this.fetchFriends} field='lastName' data={friendsRowData} isPaged={false} itemsPerPage={this.state.friendsMaxPageItems}/>
                            </Cell>
                        )}
                        width={130}
                    />
                    <Column
                        header={<FixedHeaderCell field="E-mail" />}
                        cell={props => (
                            <Cell {...props}>
                              <TextCell {...props} loadUsers={this.fetchFriends} field='email' data={friendsRowData} itemsPerPage={this.state.friendsMaxPageItems}/>
                            </Cell>
                        )}
                        width={250}
                    />
                  </Table>
                </div>
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
