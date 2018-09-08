import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post, put} from '../utils/ajax'
import {green, red} from '../consts/Constants'
import {getErrorMessage} from "../utils/errorExtractor";

class User extends React.Component{

    constructor(props){
        super(props);
        this.userBaseURL = "/api/v1.0/users/";
        this.userImagesBaseURL = "/api/v1.0/userimages/";
        this.friendsBaseURL = "/api/v1.0/friends/";
        this.state = {
            user: null,
            passwordData: null,
            userMe: null,
            friendship: null,
            initialFetchDone: false
        };
        this.fetchUser = this.fetchUser.bind(this);
        this.updateFriendship = this.updateFriendship.bind(this);
    }

    fetchUser(){
        get(this.userBaseURL + this.props.match.params.userId,
            {},
            (data)=>{this.setState({user: data.data}, this.fetchUserMe)},
            (jqXHR)=>{
                this.props.showOverlayMsg('Error retrieving details of the post!', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    fetchUserMe(){
      get(this.userBaseURL+'me',
        {},
        (data)=>{this.setState({userMe: data.data}, this.fetchFriendship)},
        (jqXHR)=>{
          this.props.showOverlayMsg('Error retrieving details of the post!', getErrorMessage(jqXHR.responseText), red);
        },{'Authorization': this.props.token});
    }

    fetchFriendship(){
        if (this.state.user==null || this.state.userMe==null) return;
        const userId1 = this.state.user.userId;
        const userId2 = this.state.userMe.userId;
        const friendIdsOrdered = (parseInt(userId1)<parseInt(userId2)) ? userId1+'/'+userId2 : userId2+'/'+userId1;
        get(this.friendsBaseURL+friendIdsOrdered,
          {},
          (data)=>{this.setState({friendship: data.data, initialFetchDone: true})},
          (jqXHR)=>{
            this.setState({initialFetchDone: true});
          },{'Authorization': this.props.token});
    }

    updateFriendship(status, message){
      if (this.state.user==null || this.state.userMe==null) return;
      const userId1 = this.state.user.userId;
      const userId2 = this.state.userMe.userId;
      const friendIdsOrdered = (parseInt(userId1)<parseInt(userId2)) ? userId1+'/'+userId2 : userId2+'/'+userId1;
      let request = {};
      request['action']=status;
      request['userId1']=userId1;
      request['userId2']=userId2;
      put(this.friendsBaseURL+friendIdsOrdered,
          request,
          (data)=>{this.props.history.push('/users/'+this.state.user.userId);},
          (jqXHR)=>{
            this.props.showOverlayMsg(message, getErrorMessage(jqXHR.responseText), red);
          },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUser();
    }

    render() {
        let disabledProp = {disabled:true};
        const addFriendButton = <button type="button" className="btn btn-primary btn-block" onClick={() => this.updateFriendship(0, 'Error adding friend!')}>Add friend</button>;
        const confirmFriendButton = <button type="button" className="btn btn-primary btn-block" onClick={() => this.updateFriendship(1, 'Error confirming friend!')}>Confirm friend</button>;
        const unblockFriendButton = <button type="button" className="btn btn-primary btn-block" onClick={() => this.updateFriendship(1, 'Error unblocking friend!')}>Unblock friend</button>;
        const declineFriendButton = <button type="button" className="btn btn-primary btn-block" onClick={() => this.updateFriendship(2, 'Error declining friend!')}>Decline friendship</button>;
        const blockFriendButton = <button type="button" className="btn btn-primary btn-block" onClick={() => this.updateFriendship(3, 'Error blocking friend!')}>Block friend</button>;
        const pendingRequestLabel = <span>Pending friend request</span>;
        const acceptedLabel = <span>Accepted</span>;
        const declinedLabel = <span>Declined</span>;
        const notFriendsLabel = <span>Not friends</span>;
        let friendActions = '';
        if (this.state.friendship == null && this.state.initialFetchDone){
            friendActions = addFriendButton;
        }else if (this.state.friendship != null && this.state.friendship.status == 0){
            if (this.state.friendship.lastActionUserId !== this.state.userMe.userId){
                friendActions = <div>{confirmFriendButton}{declineFriendButton}</div>;
            }else{
                friendActions = pendingRequestLabel;
            }
        }else if (this.state.friendship != null && this.state.friendship.status == 1){
          friendActions = <div>{acceptedLabel}{blockFriendButton}</div>;
        }else if (this.state.friendship != null && this.state.friendship.status == 2){
            if (this.state.friendship.lastActionUserId != this.state.userMe.userId){
            friendActions = pendingRequestLabel;
            }else{
             friendActions = <div>{declinedLabel}{confirmFriendButton}</div>;
            }
        }else if (this.state.friendship != null && this.state.friendship.status == 3){
          if (this.state.friendship.lastActionUserId != this.state.userMe.userId){
            friendActions = notFriendsLabel;
          }else{
            friendActions = <div>{unblockFriendButton}</div>;
          }
        }
        return (
           <div>
            <div className="row">
                <div className="col-lg-6">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">User details</h3>
                        </div>
                        <div className="panel-body">
                            <form className="form-horizontal">
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="email">Email:</label>
                                    <div className="col-sm-9">
                                        <input type="email" className="form-control" id="email" value={(this.state.user?this.state.user.email:'')} {...disabledProp}/>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="firstName">First name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="firstName" value={(this.state.user?this.state.user.firstName:'')} {...disabledProp}/>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="lastName">Last name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="lastName" value={(this.state.user?this.state.user.lastName:'')} {...disabledProp}/>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <div className="col-lg-6">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">User image</h3>
                        </div>
                        <div className="panel-body">
                            <div className="profile-header-container">
                                <div className="profile-header-img">
                                    <img src={this.state.user && this.state.user.image ? this.userImagesBaseURL+this.state.user.image.name : ''} />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
             {
               this.state.userMe!=null && this.state.user != null && this.state.user.userId != this.state.userMe.userId &&
             <div className="row">
               <div className="col-lg-6">
                 <div className="panel panel-default">
                   <div className="panel-heading">
                     <h3 className="panel-title">Friendship</h3>
                   </div>
                   <div className="panel-body">
                     {friendActions}
                   </div>
                 </div>
               </div>
             </div>
             }
           </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token
});


export default withRouter(connect(
    mapStateToProps,
    null
)(User));
