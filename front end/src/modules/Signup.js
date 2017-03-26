import React from 'react'
import {Modal} from '../components/Modal'
import {post} from '../utils/ajax'

export class Signup extends React.Component{

    constructor(props){
        super(props);
        this.handleSignupClick = this.handleSignupClick.bind(this);
        this.resetForm = this.resetForm.bind(this);
        this.signUpURL = "/api/v1.0/users";
        this.state = {
            modalMsg: '',
            modalTitle: '',
            color: '#00802b'
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
        const firstName = jQuery("input[name=firstName]").val();
        const lastName = jQuery("input[name=lastName]").val();
        const email = jQuery("input[name=email]").val();
        const emailRepeat = jQuery("input[name=emailRepeat]").val();
        const password = jQuery("input[name=password]").val();
        const passwordRepeat = jQuery("input[name=passwordRepeat]").val();
        if (email!==emailRepeat){
            this.setState({modalMsg:'E-mail addresses must match!', modalTitle: 'Form validation error'});
            $('#modal').modal('show');
            return;
        }
        if (password!==passwordRepeat){
            this.setState({modalMsg:'Passwords must match!', modalTitle: 'Form validation error'});
            $('#modal').modal('show');
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
                this.setState({modalMsg: 'Successfully created user!', modalTitle: 'Error', color: '#00802b'});
                $('#modal').modal('show');
                this.resetForm();
            }, (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.setState({modalMsg: errorMsg, modalTitle: 'Error', color: '#ff3333'});
                $('#modal').modal('show');
                this.resetForm();
            }
        );
    }

    componentDidMount(){
        $('#modal').on('hidden.bs.modal', () => {this.setState({modalMsg:'', modalTitle:''})});
    }

    render(){
        return (
            <div className="row">
                <Modal title={this.state.modalTitle} body={this.state.modalMsg} modalId='modal' color={this.state.color}/>
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
                                    <a href="#" className="btn btn-lg btn-success btn-block" onClick={this.handleSignupClick} tabIndex="7">Sign up</a>
                                    <a href="/" className="btn btn-sm btn-primary btn-block">Back</a>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}