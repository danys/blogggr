import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post} from '../utils/ajax'
import Link from '../components/navigation/Link'

class User extends React.Component{

    constructor(props){
        super(props);
        this.userMeURL = "/api/v1.0/users/me";
        this.state = {
            userMe: null
        };
        this.fetchPost = this.fetchUserMe.bind(this);
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

    render() {
        let userEmailDisabled = {};
        userEmailDisabled.disabled=true;
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
                                        <input type="email" className="form-control" id="email" value={(this.state.userMe?this.state.userMe.email:'')} {...userEmailDisabled}/>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="firstName">First name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="firstName" value={(this.state.userMe?this.state.userMe.firstName:'')} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label className="control-label col-sm-3" htmlFor="lastName">Last name:</label>
                                    <div className="col-sm-9">
                                        <input type="text" className="form-control" id="lastName" value={(this.state.userMe?this.state.userMe.lastName:'')} />
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
)(User));
