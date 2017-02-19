import React from 'react'
import Welcome from '../components/Welcome'
import Page from '../components/Page'

export default class App extends React.Component{
    render(){
        return (
        <Page loggedin="false" main={<Welcome/>}/>
        );
    }
}