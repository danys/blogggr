import React from 'react'
import LoginForm from '../components/LoginForm'
import Page from '../components/Page'

export default class Login extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
            <Page loggedin="false" main={<LoginForm router={this.props.router} />} highlight="home" />
        );
    }
}