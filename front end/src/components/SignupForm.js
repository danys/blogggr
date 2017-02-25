import React from 'react'

const SignupForm = () => (
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
                                <input className="form-control" placeholder="First name" name="firstName" type="text" autoFocus />
                            </div>
                            <div className="form-group">
                                <input className="form-control" placeholder="Last name" name="lastName" type="text" />
                            </div>
                            <div className="form-group">
                                <input className="form-control" placeholder="E-mail" name="email" type="email" />
                            </div>
                            <div className="form-group">
                                <input className="form-control" placeholder="Repeat e-mail" name="emailRepeat" type="email" />
                            </div>
                            <div className="form-group">
                                <input className="form-control" placeholder="Password" name="password" type="password" value="" />
                            </div>
                            <div className="form-group">
                                <input className="form-control" placeholder="Repeat password" name="repeatPassword" type="password" value="" />
                            </div>
                            <a href="/" className="btn btn-lg btn-success btn-block">Sign up</a>
                            <a href="/" className="btn btn-sm btn-primary btn-block">Back</a>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
)

export default SignupForm