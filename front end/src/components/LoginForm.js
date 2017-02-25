import React from 'react'

export default class LoginForm extends React.Component {

    constructor(props){
        super(props);
        this.handleLoginClick = this.handleLoginClick.bind(this);
    }

    handleLoginClick(){
        //
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
                                               autofocus/>
                                    </div>
                                    <div className="form-group">
                                        <input className="form-control" placeholder="Password" name="password"
                                               type="password" value=""/>
                                    </div>
                                    <div className="checkbox">
                                        <label>
                                            <input name="remember" type="checkbox" value="Remember Me"/>Remember Me
                                        </label>
                                    </div>
                                    <a onClick={this.handleLoginClick()} className="btn btn-lg btn-success btn-block">Login</a>
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