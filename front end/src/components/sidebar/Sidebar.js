import React from 'react';

export class Sidebar extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
            <div className="well">
                <h4>{this.props.title}</h4>
                {this.props.children}
            </div>
        );
    }
}