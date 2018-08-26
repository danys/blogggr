import React from 'react'
import {post} from '../utils/ajax'
import {red, green}  from '../consts/Constants'
import Link from '../components/navigation/Link';
import {getErrorMessage} from '../utils/errorExtractor';

export class Signup extends React.Component{

    constructor(props){
        super(props);
        this.handleSignupClick = this.handleSignupClick.bind(this);
        this.resetForm = this.resetForm.bind(this);
        this.signUpURL = "/api/v1.0/users";
        this.state = {
            sex: 'M',
            lang: 'EN'
        };
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
        if (this.state.email!==this.state.emailRepeat){
            this.props.showOverlayMsg('Form validation error', 'E-mail addresses must match!', red);
            return;
        }
        if (this.state.password!==this.state.passwordRepeat){
            this.props.showOverlayMsg('Form validation error', 'Passwords must match!', red);
            return;
        }
        let requestData = {
            "firstName": this.state.firstName,
            "lastName": this.state.lastName,
            "email": this.state.email,
            "emailRepeat": this.state.emailRepeat,
            "password": this.state.password,
            "passwordRepeat": this.state.passwordRepeat,
            "lang": this.state.lang,
            "sex": this.state.sex
        };
        post(this.signUpURL, requestData,
            (data, status, request)=>{
            this.props.showOverlayMsg('Success', 'Successfully created user!', green);
            this.resetForm();
            }, (jqXHR)=>{
                this.props.showOverlayMsg('Error creating user', getErrorMessage(jqXHR.responseText), red);
                this.resetForm();
            }
        );
    }

    formInputChange(field, event){
        if (field==='sex'){
            if (event.target.value==='Male') this.setState({sex: 'M'});
            else if (event.target.value==='Female') this.setState({sex: 'F'});
        }
        else if (field==='lang'){
            if (event.target.value==='English') this.setState({lang: 'EN'});
            else if (event.target.value==='German') this.setState({lang: 'DE'});
            else if (event.target.value==='French') this.setState({lang: 'FR'});
        }
        else this.setState({[field]: event.target.value});
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
                                        <label>First name:</label>
                                        <input className="form-control" placeholder="First name" name="firstName" type="text" autoFocus pattern=".{3,}" required="true" tabIndex="1" onChange={this.formInputChange.bind(this,"firstName")}/>
                                    </div>
                                    <div className="form-group">
                                        <label>Last name:</label>
                                        <input className="form-control" placeholder="Last name" name="lastName" type="text" pattern=".{3,}" required="true" tabIndex="2" onChange={this.formInputChange.bind(this,"lastName")}/>
                                    </div>
                                    <div className="form-group">
                                        <label>Email:</label>
                                        <input className="form-control" placeholder="E-mail" name="email" type="email" pattern=".{1,}@[az09]{2,}\.[az]{2,}" required="true" tabIndex="3" onChange={this.formInputChange.bind(this,"email")}/>
                                    </div>
                                    <div className="form-group">
                                        <label>Repeat email:</label>
                                        <input className="form-control" placeholder="Repeat e-mail" name="emailRepeat" type="email" pattern=".{1,}@[az09]{2,}\.[az]{2,}" required="true" tabIndex="4" onChange={this.formInputChange.bind(this,"emailRepeat")}/>
                                    </div>
                                    <div className="form-group">
                                        <label>Password:</label>
                                        <input className="form-control" placeholder="Password" name="password" type="password" pattern=".{8,}" required="true" tabIndex="5" onChange={this.formInputChange.bind(this,"password")}/>
                                    </div>
                                    <div className="form-group">
                                        <label>Repeat password:</label>
                                        <input className="form-control" placeholder="Repeat password" name="passwordRepeat" type="password" pattern=".{8,}" required="true" tabIndex="6" onChange={this.formInputChange.bind(this,"passwordRepeat")}/>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="sex">Sex:</label>
                                        <select className="form-control" id="sex" required="true" tabIndex="7" onChange={this.formInputChange.bind(this,"sex")}>
                                            <option>Male</option>
                                            <option>Female</option>
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="lang">Language:</label>
                                        <select className="form-control" id="lang" required="true" tabIndex="8" onChange={this.formInputChange.bind(this,"lang")}>
                                            <option>English</option>
                                            <option>German</option>
                                            <option>French</option>
                                        </select>
                                    </div>
                                    <Link url="/signup" cssClass="btn btn-lg btn-success btn-block" onClick={this.handleSignupClick} text="Sign up" tabIndex="9"/>
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