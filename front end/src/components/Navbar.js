import React, {PropTypes} from 'react'

import { connect } from 'react-redux'
import { logoutAction } from '../actions/SessionActions'
import {del} from '../utils/ajax'
import {red}  from '../consts/Constants'

class Navbar extends React.Component{

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
        del(this.state.sessionURL,()=>{},()=>{this.props.showOverlayMsg('Error logging out', 'Error deleting session', red);},{'Authorization': this.state.token});
    }

    render(){
        let loginFunctionality = '';
        const homeProps = this.state.highlight && this.state.highlight==='home'?{className:"active"}:null;
        const friendsProps = this.state.highlight && this.state.highlight==='friends'?{className:"active"}:null;
        const userProps = this.state.highlight && this.state.highlight==='user'?{className:"dropdown active"}:{className:"dropdown"};
        if (this.state.loggedin) {
                loginFunctionality = (
                    <ul className="nav navbar-top-links navbar-right">
                        <li {...homeProps}><a href="/">Home</a></li>
                        <li {...friendsProps}><a href="/friends">Friends</a></li>
                        <li {...userProps}>
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
};

const mapStateToProps = (state) => ({
 loggedin: state.session.loggedin,
 sessionURL: state.session.sessionURL,
 token: state.session.token
 });

const mapDispatchToProps = (dispatch) => {
    return {
        removeToken: () => {
            dispatch(logoutAction())
        }
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Navbar);