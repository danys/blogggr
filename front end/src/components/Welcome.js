import React from 'react'

export default class Welcome extends React.Component{

    render(){
        return (
                <div className="row">
                    <div className="jumbotron">
                        <h1>Hi, welcome to Blogggr!</h1>
                        <p>Share blog posts with friends and the world!</p>
                        <p>
                            <a className="btn btn-info btn-lg" href="login.html" role="button">Login</a> or
                            <a className="btn btn-success btn-lg" href="signup.html" role="button">Sign up</a>
                        </p>
                    </div>
                </div>
        );
    }
}
