import React, {PropTypes} from 'react'

import { connect } from 'react-redux'
import { logoutAction } from '../../actions/SessionActions'
import {del} from '../../utils/ajax'
import {red}  from '../../consts/Constants'
import Link from './Link'
import {withRouter} from 'react-router-dom'

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
        this.props.history.push('/');
        del(this.state.sessionURL,()=>{},()=>{this.props.showOverlayMsg('Error logging out', 'Error deleting session', red);},{'Authorization': this.state.token});
    }

    render(){
        console.log("Loca "+this.props.highlight);
        let loginFunctionality = '';
        const homeProps = this.state.highlight && this.state.highlight==='/'?{className:"active"}:null;
        const friendsProps = this.state.highlight && this.state.highlight==='/friends'?{className:"active"}:null;
        const userProps = this.state.highlight && this.state.highlight==='/user'?{className:"dropdown active"}:{className:"dropdown"};
        if (this.state.loggedin) {
                loginFunctionality = (
                    <ul className="nav navbar-top-links navbar-right">
                        <li {...homeProps}><Link url="/" text="Home"></Link></li>
                        <li {...friendsProps}><Link url="/friends" text="Friends"></Link></li>
                        <li {...userProps}>
                            <Link cssClass="dropdown-toggle" data-toggle="dropdown" url="#">
                                <i className="fa fa-user fa-fw"></i> <i className="fa fa-caret-down"></i>
                            </Link>
                            <ul className="dropdown-menu dropdown-user">
                                <li>
                                    <Link url="/user">
                                        <i className="fa fa-user fa-fw"></i> User Profile
                                    </Link>
                                </li>
                                <li>
                                    <Link url="/settings">
                                        <i className="fa fa-gear fa-fw"></i> Settings
                                    </Link>
                                </li>
                                <li className="divider"></li>
                                <li>
                                    <Link url="/" onClick={this.handleLogoutClick}>
                                        <i className="fa fa-sign-out fa-fw"></i> Logout
                                    </Link>
                                </li>
                            </ul>
                        </li>
                    </ul>
                );
        }
        return (
        <nav className="navbar navbar-default navbar-static-top" role="navigation">
            <div className="navbar-header">
                <Link url="/" cssClass="navbar-brand">
                    <i className="blogggr_logo">
                        <u>Blogggr</u>
                    </i>
                </Link>
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

export default withRouter(connect(
    mapStateToProps,
    mapDispatchToProps
)(Navbar));