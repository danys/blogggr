import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post, put} from '../utils/ajax'
import {green, red} from '../consts/Constants'

class User extends React.Component{

    constructor(props){
        super(props);
        this.userBaseURL = "/api/v1.0/users/";
        this.userImagesBaseURL = "/api/v1.0/userimages/";
        this.state = {
            user: null,
            passwordData: null
        };
        this.fetchUser = this.fetchUser.bind(this);
    }

    fetchUser(){
        get(this.userBaseURL+this.props.match.params.userId,
            {},
            (data)=>{this.setState({user: data.data})},
            (jqXHR)=>{
                this.props.showOverlayMsg('Error retrieving details of the post!', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    componentDidMount(){
        this.fetchUser();
    }

    render() {
        let disabledProp = {disabled:true};
        return (
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
