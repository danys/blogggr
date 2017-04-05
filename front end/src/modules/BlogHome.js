import React from 'react'
import { connect } from 'react-redux'

import HomePosts from '../components/HomePosts'
import Welcome from '../components/Welcome'
import { logoutAction } from '../actions/action'

export class BlogHome extends React.Component{

    constructor(props){
        super(props);
        let isLoggedIn = this.props.loggedin;
        let validTillDateTime = moment(this.props.validUntil,"YYYY-MM-DD HH:mm:ss");
        if ((isLoggedIn===true) && (moment().isAfter(validTillDateTime))) {
            isLoggedIn = false;
            this.props.removeToken();
        }
        this.state = {
            loggedin: isLoggedIn
        }
    }

    componentWillReceiveProps(nextProps){
        const isLoggedIn = nextProps.loggedin;
        this.setState({
            loggedin: isLoggedIn
        });
    }

    render(){
        let content =  (this.state.loggedin)?<HomePosts showOverlayMsg={this.props.showOverlayMsg}/>:<Welcome/>;
        return (
            content
        );
    }
}

const mapStateToProps = (state) => {
    return {
        loggedin: state.loggedin,
        validUntil: state.validUntil
    }
}

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
)(BlogHome)