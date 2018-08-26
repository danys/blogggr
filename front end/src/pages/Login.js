import React from 'react'
import { connect } from 'react-redux'
import {loginAction} from '../actions/SessionActions'
import {post} from '../utils/ajax'
import {red}  from '../consts/Constants'
import Link from '../components/navigation/Link'
import {withRouter} from 'react-router-dom'
import {getErrorMessage} from '../utils/errorExtractor';

export class Login extends React.Component{

    constructor(props){
        super(props);
        this.handleLoginClick = this.handleLoginClick.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.sessionsURL = "/api/v1.0/sessions";
    }

    handleLoginClick(){
        const email = jQuery("input[name=email]").val();
        const password = jQuery("input[name=password]").val();
        const rememberMe = jQuery("input[name=remember]").is(':checked');
        let requestData = {
            "email": email,
            "password": password,
            "rememberMe": rememberMe
        };
        post(this.sessionsURL, requestData,
            (data, status, request)=>{
            //Extract the auth token, update the redux store and redirect the user
            let authToken = data.data.Auth;
            this.props.storeToken(authToken, data.data.ValidUntil, data.data.email);
            this.props.history.push('/');
        }, (jqXHR)=>{
                this.props.showOverlayMsg('Login error', getErrorMessage(jqXHR.responseText), red);
                jQuery("input[name=email]").val('');
                jQuery("input[name=password]").val('');
                jQuery("input[name=remember]").prop('checked',false);
            }
        );
    }

    handleKeyPress(event) {
        if (event.key == 'Enter') this.handleLoginClick();
    }

    render() {
        const { from } = this.props.location.state || { from: { pathname: '/' } };
        const warning = (from.pathname!=='/')?
            <div className="row">
                <div className="col-md-6 col-md-offset-3">
            <div className="login-panel panel panel-danger">
                <div className="panel-heading">
                    <h3 className="panel-title">Access denied</h3>
                </div>
                <div className="panel-body">
                    You need to be authenticated to view this page!
                </div>
            </div>
                </div>
            </div>
            :
            '';
        return (
            <div>
            {warning}
            <div className="row">
                <div className="col-md-4 col-md-offset-4">
                    <div className="login-panel panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Please sign in</h3>
                        </div>
                        <div className="panel-body">
                            <form role="form">
                                <fieldset>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="E-mail" name="email" type="email"
                                               autoFocus tabIndex="1" required />
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Password" name="password"
                                               type="password" tabIndex="2" required onKeyPress={this.handleKeyPress}/>
                                    </div>
                                    <div className="checkbox">
                                        <label>
                                            <input name="remember" type="checkbox" value="Remember Me" tabIndex="3" />Remember Me
                                        </label>
                                    </div>
                                    <Link url="/login" onClick={this.handleLoginClick} cssClass="btn btn-lg btn-success btn-block" text="Login" tabIndex="4"/>
                                    <Link url="/" cssClass="btn btn-sm btn-primary btn-block" text="Back" tabIndex="5"/>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            </div>
        );
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        storeToken: (token, validUntil, email) => {
            dispatch(loginAction(token, validUntil, email))
        }
    }
};

export default withRouter(connect(
    null,
    mapDispatchToProps
)(Login));