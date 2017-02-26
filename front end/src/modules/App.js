import React from 'react'
import Welcome from '../components/Welcome'
import Page from '../components/Page'
import BlogHome from '../components/BlogHome'
import { connect } from 'react-redux'

export class App extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        const isLoggedIn = this.props.loggedin === 'true' ? true : false;
        let content;
        if (isLoggedIn) content = (
            <Page loggedin="true" main={<BlogHome/>} highlight="home"/>
        );
        else content = (
            <Page loggedin="false" main={<Welcome/>} highlight="home"/>
        );
        return content;
    }
}

const mapStateToProps = (state) => ({
    loggedin: state.loggedin
})

const mapDispatchToProps = (dispatch) => ({
    //
})

const LoginState = connect(
    mapStateToProps,
    mapDispatchToProps
)(App)

export default LoginState