import React, {PropTypes} from 'react'
import { browserHistory } from 'react-router';

export class HomePost extends React.Component {

    constructor(props){
        super(props);
    }

    render(){
        return (
        <div>
            <h2>
                <a href={this.props.postURL} onClick={(e)=>{e.preventDefault();browserHistory.push(this.props.postURL)}}>{this.props.title}</a>
            </h2>
            <p className="lead">
                by <a href={this.props.authorProfileURL}>{this.props.author}</a>
            </p>
            <p><span className="glyphicon glyphicon-time"></span>Posted on {this.props.timestamp}</p>
            <hr/>
            <img className="img-responsive" src="/blogBgImage.png" alt=""/>
            <hr/>
            <p>{this.props.textBody}</p>
            <a className="btn btn-primary" href={this.props.postURL}>Read More <span
            className="glyphicon glyphicon-chevron-right"></span></a>
            {this.props.hr}
        </div>
        );
    }
}

HomePost.propTypes = {
    title: React.PropTypes.string.isRequired,
    author: React.PropTypes.string.isRequired,
    authorProfileURL: React.PropTypes.string.isRequired,
    timestamp: React.PropTypes.string.isRequired,
    textBody: React.PropTypes.string.isRequired,
    postURL: React.PropTypes.string.isRequired
}