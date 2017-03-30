import React from 'react'
import { connect } from 'react-redux'

import HomePosts from '../components/HomePosts'
import Welcome from '../components/Welcome'

export class BlogHome extends React.Component{

    constructor(props){
        super(props);
        const isLoggedIn = this.props.loggedin;
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

const mapStateToProps = (state) => ({
    loggedin: state.loggedin
})

const mapDispatchToProps = (dispatch) => ({
    //
})

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(BlogHome)