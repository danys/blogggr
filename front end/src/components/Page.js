import React from 'react'
import Navbar from '../components/Navbar'
import Container from '../components/Container'

export default class Page extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        return (
            <div>
                <Navbar loggedin={this.props.loggedin} highlight={this.props.highlight}/>
                <Container main={this.props.main}/>
            </div>
        );
    }
}