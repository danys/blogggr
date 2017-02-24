import React from 'react'

export default class Navbar extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        let loginFunctionality = '';
        if (this.props.loggedin==='true') {
            if (this.props.highlight==='home') {
                loginFunctionality = (
                    <ul className="nav navbar-top-links navbar-right">
                        <li className="active"><a href="/">Home</a></li>
                        <li><a href="/friends">Friends</a></li>
                        <li className="dropdown">
                            <a className="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i className="fa fa-user fa-fw"></i> <i className="fa fa-caret-down"></i>
                            </a>
                            <ul className="dropdown-menu dropdown-user">
                                <li><a href="/user"><i className="fa fa-user fa-fw"></i> User Profile</a>
                                </li>
                                <li><a href="/settings"><i className="fa fa-gear fa-fw"></i> Settings</a>
                                </li>
                                <li className="divider"></li>
                                <li><a href="/logout"><i className="fa fa-sign-out fa-fw"></i> Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
            }
            else if (this.props.highlight==='friends'){
                loginFunctionality = (
                    <ul className="nav navbar-top-links navbar-right">
                        <li><a href="/">Home</a></li>
                        <li className="active"><a href="/friends">Friends</a></li>
                        <li className="dropdown">
                            <a className="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i className="fa fa-user fa-fw"></i> <i className="fa fa-caret-down"></i>
                            </a>
                            <ul className="dropdown-menu dropdown-user">
                                <li><a href="/user"><i className="fa fa-user fa-fw"></i> User Profile</a>
                                </li>
                                <li><a href="/settings"><i className="fa fa-gear fa-fw"></i> Settings</a>
                                </li>
                                <li className="divider"></li>
                                <li><a href="/logout"><i className="fa fa-sign-out fa-fw"></i> Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
            }
            else if (this.props.highlight==='user'){
                loginFunctionality = (
                    <ul className="nav navbar-top-links navbar-right">
                        <li><a href="/">Home</a></li>
                        <li><a href="/friends">Friends</a></li>
                        <li className="dropdown active">
                            <a className="dropdown-toggle" data-toggle="dropdown" href="#">
                                <i className="fa fa-user fa-fw"></i> <i className="fa fa-caret-down"></i>
                            </a>
                            <ul className="dropdown-menu dropdown-user">
                                <li><a href="/user"><i className="fa fa-user fa-fw"></i> User Profile</a>
                                </li>
                                <li><a href="/settings"><i className="fa fa-gear fa-fw"></i> Settings</a>
                                </li>
                                <li className="divider"></li>
                                <li><a href="/login"><i className="fa fa-sign-out fa-fw"></i> Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
            }
        }
        return (
        <nav className="navbar navbar-default navbar-static-top" role="navigation">
            <div className="navbar-header">
                <a className="navbar-brand" href="/">
                    <i className="blogggr_logo">
                        <u>Blogggr</u>
                    </i>
                </a>
            </div>
            {loginFunctionality}
        </nav>
        );
    }
}