import React, {PropTypes} from 'react'

import { connect } from 'react-redux'
import { logoutAction } from '../actions/action'
import {del} from '../utils/ajax'

export class Navbar extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            loggedin: this.props.loggedin,
            highlight: this.props.highlight,
            sessionURL: this.props.sessionURL,
            token: this.props.token
        };
        this.handleLogoutClick = this.handleLogoutClick.bind(this);
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            loggedin: nextProps.loggedin,
            highlight: nextProps.highlight,
            sessionURL: nextProps.sessionURL,
            token: nextProps.token
        });
    }

    handleLogoutClick(){
        this.props.removeToken();
        this.props.router.push('/');
        console.log("SessionURL: "+this.state.sessionURL);
        del(this.state.sessionURL,()=>{console.log("Success deleting session!")},()=>{alert("Error deleting session!")},{'Authorization': this.state.token});
    }

    render(){
        let loginFunctionality = '';
        if (this.state.loggedin) {
            if (this.state.highlight==='home') {
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
                                <li><a href="#" onClick={this.handleLogoutClick}><i className="fa fa-sign-out fa-fw"></i> Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
            }
            else if (this.state.highlight==='friends'){
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
                                <li><a href="#" onClick={this.handleLogoutClick}><i className="fa fa-sign-out fa-fw"></i> Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
            }
            else if (this.state.highlight==='user'){
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
                                <li><a href="#" onClick={this.handleLogoutClick}><i className="fa fa-sign-out fa-fw"></i> Logout</a>
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

Navbar.propTypes = {
    removeToken: PropTypes.func.isRequired
}

const mapStateToProps = (state) => ({
 loggedin: state.loggedin,
 sessionURL: state.sessionURL,
 token: state.token
 })

const mapDispatchToProps = (dispatch) => {
    return {
        removeToken: () => {
            dispatch(logoutAction())
        }
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Navbar)