import React from 'react'
import Link from './navigation/Link'

export default class Welcome extends React.Component{

    render(){
        return (
                <div className="row">
                    <div className="jumbotron">
                        <h1>Hi, welcome to Blogggr!</h1>
                        <p>Share blog posts with friends and the world!</p>
                        <p>
                            <Link cssClass="btn btn-info btn-lg" url="/login" role="button" text="Login" /> or
                            <Link cssClass="btn btn-success btn-lg linkSpace" url="/signup" role="button" text="Sign up" />
                        </p>
                    </div>
                </div>
        );
    }
}
