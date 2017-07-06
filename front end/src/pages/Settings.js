import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, put} from '../utils/ajax'
import {green, red} from '../consts/Constants'

class Settings extends React.Component{

    constructor(props){
        super(props);
        this.userMeURL = "/api/v1.0/users/me";
        this.userBaseURL = "/api/v1.0/users/";
        this.state = {
            userMe: null,
            passwordData: null
        };
        this.fetchUserMe = this.fetchUserMe.bind(this);
    }

    fetchUserMe(){
        get(this.userMeURL,
            {},
            (data)=>{this.setState({userMe: data.data})},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error retrieving details of the post!', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUserMe();
    }

    handleInputFieldChange(fieldName, value){
        let userMe = this.state.userMe;
        userMe[fieldName]=value.target.value;
        this.setState({userMe: userMe});
    }

    handlePasswordFieldChange(fieldName, value){
        let passwordData = (this.state.passwordData!=null)?this.state.passwordData:{};
        passwordData[fieldName]=value.target.value;
        this.setState({passwordData: passwordData});
    }

    updateUser(){
        if (!this.state.userMe) return;
        let request = {
            id: this.state.userMe.userID,
            firstName: this.state.userMe.firstName,
            lastName: this.state.userMe.lastName
        };
        put(this.userBaseURL+this.state.userMe.userID,
            request,
            (data)=>{
                let successMsg = "Successfully updated user data!";
                this.props.showOverlayMsg('Success', successMsg, green);
            },
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error updating user!', errorMsg, red);
            },
            {'Authorization': this.props.token});
    }

    updatePassword(){
        if (this.state.passwordData==null) return;
        let request = {
            id: this.state.userMe.userID,
            oldPassword: this.state.passwordData.oldPassword,
            password: this.state.passwordData.password,
            passwordRepeat: this.state.passwordData.passwordRepeat
        };
        put(this.userBaseURL+this.state.userMe.userID,
            request,
            (data)=>{
                let successMsg = "Password changed successfully!";
                this.props.showOverlayMsg('Success', successMsg, green);
                this.setState({passwordData: null});
            },
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error changing password!', errorMsg, red);
                this.setState({passwordData: null});
            },
            {'Authorization': this.props.token});
    }

    render() {
        let userEmailDisabled = {};
        userEmailDisabled.disabled=true;
        return (
            <div className="row">
                <div className="col-lg-6">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Edit user details</h3>
                        </div>
                        <div className="panel-body">
                            <form className="form-horizontal">
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="email">Email:</label>
                                    <div className="col-sm-9">
                                        <input type="email" className="form-control" id="email" value={(this.state.userMe?this.state.userMe.email:'')} {...userEmailDisabled}/>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="firstName">First name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="firstName" onChange={this.handleInputFieldChange.bind(this,'firstName')} value={(this.state.userMe?this.state.userMe.firstName:'')} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="lastName">Last name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="lastName" onChange={this.handleInputFieldChange.bind(this,'lastName')} value={(this.state.userMe?this.state.userMe.lastName:'')} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3" />
                                    <div className="col-sm-9">
                                        <button type="button" className="btn btn-primary btn-block" onClick={this.updateUser.bind(this)}>Update</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div className="col-lg-6">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Change password</h3>
                        </div>
                        <div className="panel-body">
                            <form className="form-horizontal">
                                <div className="form-group">
                                    <label className="control-label col-sm-5" htmlFor="oldPassword">Old password:</label>
                                    <div className="col-sm-7">
                                        <input type="password" className="form-control" id="oldPassword" onChange={this.handlePasswordFieldChange.bind(this,'oldPassword')}
                                               value={(this.state.passwordData && this.state.passwordData.oldPassword?this.state.passwordData.oldPassword:'')}/>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-5" htmlFor="newPassword">New password:</label>
                                    <div className="col-sm-7">
                                        <input type="password" className="form-control" id="password" onChange={this.handlePasswordFieldChange.bind(this,'password')}
                                               value={(this.state.passwordData && this.state.passwordData.password?this.state.passwordData.password:'')} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-5" htmlFor="newPasswordRepeat">Repeat new password:</label>
                                    <div className="col-sm-7">
                                        <input type="password" className="form-control" id="passwordRepeat" onChange={this.handlePasswordFieldChange.bind(this,'passwordRepeat')}
                                               value={(this.state.passwordData && this.state.passwordData.passwordRepeat?this.state.passwordData.passwordRepeat:'')} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-5" />
                                    <div className="col-sm-7">
                                        <button type="button" className="btn btn-primary btn-block" onClick={this.updatePassword.bind(this)}>Change password</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
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
)(Settings));
