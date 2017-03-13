import React from 'react'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'

export default class App extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
        <div>
            <Navbar highlight={this.props.children.props.route.name} router={this.props.router}/>
            <div className="container">
                {this.props.children}
                <Footer />
            </div>
        </div>
        );
    }
}