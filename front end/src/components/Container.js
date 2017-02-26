import React from 'react'
import Footer from '../components/Footer'

export default class Container extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
            <div className="container">
                {this.props.main}
                <Footer />
            </div>
        );
    }
}