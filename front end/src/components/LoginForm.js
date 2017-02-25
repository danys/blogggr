import React from 'react'
import jQuery from 'jquery'

export default class LoginForm extends React.Component {

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
        jQuery.ajax({
            url: this.sessionsURL,
            method: 'POST',
            data: JSON.stringify(requestData),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function(data, status){
                console.log("Data = "+JSON.parse(data));
                console.log("Status = "+status);
            },
            error: function(){
                alert("Error!");
            }
        });
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
                                               autoFocus/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Password" name="password"
                                               type="password" />
                                    </div>
                                    <div className="checkbox">
                                        <label>
                                            <input name="remember" type="checkbox" value="Remember Me"/>Remember Me
                                        </label>
                                    </div>
                                    <a onClick={this.handleLoginClick} className="btn btn-lg btn-success btn-block">Login</a>
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