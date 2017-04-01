import React from 'react'
import { connect } from 'react-redux'

import HomePosts from '../components/HomePosts'
import Welcome from '../components/Welcome'

export class BlogHome extends React.Component{

    constructor(props){
        super(props);
        let isLoggedIn = this.props.loggedin;
        let validTillDateTime = moment(this.props.validUntil,"YYYY-MM-DD HH:mm:ss");
        if (moment().isAfter(validTillDateTime)){
            //TODO
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
        let content =  (this.state.loggedin)?<HomePosts/>:<Welcome/>;
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

const mapDispatchToProps = (dispatch) => ({
    //
})

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(BlogHome)