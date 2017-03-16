import React, {PropTypes} from 'react'
import { connect } from 'react-redux'
import {loginAction} from '../actions/action'
import {post} from '../utils/ajax'

export class Login extends React.Component{

    constructor(props){
        super(props);
        this.handleLoginClick = this.handleLoginClick.bind(this);
        this.sessionsURL = "/api/v1.0/sessions";
    }

    handleLoginClick(){
        const email = jQuery("input[name=email]").val();
        const password = jQuery("input[name=password]").val();
        let requestData = {
            "email": email,
            "password": password
        };
        post(this.sessionsURL, requestData,
            (data, status, request)=>{
            //Extract the auth token, update the redux store and redirect the user
            let authToken = data.data.Auth;
            let sessionURL = request.getResponseHeader('Location');
            let pos = sessionURL.indexOf('/api');
            sessionURL = sessionURL.substr(pos);
            this.props.storeToken(authToken, sessionURL);
            this.props.router.push('/');
        }, ()=>{alert("Error")});
    }

    render() {
        return (
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
                                               autoFocus tabIndex="1" />
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Password" name="password"
                                               type="password" tabIndex="2" />
                                    </div>
                                    <div className="checkbox">
                                        <label>
                                            <input name="remember" type="checkbox" value="Remember Me" tabIndex="3"/>Remember Me
                                        </label>
                                    </div>

                                    <a href="#" onClick={this.handleLoginClick} className="btn btn-lg btn-success btn-block" tabIndex="4">Login</a>
                                    <a href="/" className="btn btn-sm btn-primary btn-block" tabIndex="5">Back</a>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

Login.propTypes = {
    storeToken: PropTypes.func.isRequired
}

const mapDispatchToProps = (dispatch) => {
    return {
        storeToken: (token, sessionURL) => {
            dispatch(loginAction(token, sessionURL))
        }
    }
};

export default connect(
    null,
    mapDispatchToProps
)(Login);