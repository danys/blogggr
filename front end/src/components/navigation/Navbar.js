import React, {PropTypes} from 'react'

import { connect } from 'react-redux'
import { logoutAction } from '../../actions/SessionActions'
import Link from './Link'
import {withRouter} from 'react-router-dom'

class Navbar extends React.Component{

    constructor(props){
        super(props);
        this.handleLogoutClick = this.handleLogoutClick.bind(this);
    }

    handleLogoutClick(){
        this.props.removeToken();
        this.props.history.push('/');
    }

    render(){
        let loginFunctionality = '';
        const homeProps = this.props.highlight && this.props.highlight==='/'?{className:"active"}:null;
        const friendsProps = this.props.highlight && this.props.highlight==='/friends'?{className:"active"}:null;
        const userProps = this.props.highlight && this.props.highlight==='/users'?{className:"dropdown active"}:{className:"dropdown"};
        if (this.props.token!=='') {
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
                                    <Link url="/users/me">
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