import React from 'react'
import {post} from '../utils/ajax'
import {red, green}  from '../consts/Constants'
import Link from '../components/Link';

export class Signup extends React.Component{

    constructor(props){
        super(props);
        this.handleSignupClick = this.handleSignupClick.bind(this);
        this.resetForm = this.resetForm.bind(this);
        this.signUpURL = "/api/v1.0/users";
    }

    resetForm(){
        jQuery("input[name=firstName]").val('');
        jQuery("input[name=lastName]").val('');
        jQuery("input[name=email]").val('');
        jQuery("input[name=password]").val('');
        jQuery("input[name=emailRepeat]").val('');
        jQuery("input[name=passwordRepeat]").val('');
    }

    handleSignupClick(){
        const firstName = jQuery("input[name=firstName]").val();
        const lastName = jQuery("input[name=lastName]").val();
        const email = jQuery("input[name=email]").val();
        const emailRepeat = jQuery("input[name=emailRepeat]").val();
        const password = jQuery("input[name=password]").val();
        const passwordRepeat = jQuery("input[name=passwordRepeat]").val();
        if (email!==emailRepeat){
            this.props.showOverlayMsg('Form validation error', 'E-mail addresses must match!', red);
            return;
        }
        if (password!==passwordRepeat){
            this.props.showOverlayMsg('Form validation error', 'Passwords must match!', red);
            return;
        }
        let requestData = {
            "firstName": firstName,
            "lastName": lastName,
            "email": email,
            "password": password,
            "passwordRepeat": passwordRepeat
        };
        post(this.signUpURL, requestData,
            (data, status, request)=>{
            this.props.showOverlayMsg('Success', 'Successfully created user!', green);
            this.resetForm();
            }, (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error creating user', errorMsg, red);
                this.resetForm();
            }
        );
    }

    render(){
        return (
            <div className="row">
                <div className="col-md-4 col-md-offset-4">
                    <div className="login-panel panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">Sign up form</h3>
                        </div>
                        <div className="panel-body">
                            <form role="form">
                                <fieldset>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="First name" name="firstName" type="text" autoFocus pattern=".{3,}" required="true" tabIndex="1"/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Last name" name="lastName" type="text" pattern=".{3,}" required="true" tabIndex="2"/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="E-mail" name="email" type="email" pattern=".{1,}@[az09]{2,}\.[az]{2,}" required="true" tabIndex="3"/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Repeat e-mail" name="emailRepeat" type="email" pattern=".{1,}@[az09]{2,}\.[az]{2,}" required="true" tabIndex="4"/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Password" name="password" type="password" pattern=".{8,}" required="true" tabIndex="5"/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Repeat password" name="passwordRepeat" type="password" pattern=".{8,}" required="true" tabIndex="6"/>
                                    </div>
                                    <Link url="/signup" cssClass="btn btn-lg btn-success btn-block" onClick={this.handleSignupClick} text="Sign up" tabIndex="7"/>
                                    <Link url="/" cssClass="btn btn-sm btn-primary btn-block" text="Back" />
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}