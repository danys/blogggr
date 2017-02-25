import React from 'react'
import SignupForm from '../components/SignupForm'
import Page from '../components/Page'

export default class Signup extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
            <Page loggedin="false" main={<SignupForm/>} highlight="home" />
        );
    }
}