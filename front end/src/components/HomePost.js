import React from 'react';
import PropTypes from 'prop-types';
import Link from './navigation/Link';

export class HomePost extends React.Component {

    constructor(props){
        super(props);
    }

    render(){
        return (
        <div>
            <h2>
                <Link url={this.props.postURL} text={this.props.title} />
            </h2>
            <p className="lead">
                by <Link url={this.props.authorProfileURL} text={this.props.author} />
            </p>
            <p><span className="glyphicon glyphicon-time"></span>Posted on {this.props.timestamp}</p>
            <hr/>
            <img className="img-responsive" src="/blogBgImage.png" alt=""/>
            <hr/>
            <p>{this.props.textBody}</p>
            <Link cssClass="btn btn-primary" url={this.props.postURL}>
                Read More <span
                className="glyphicon glyphicon-chevron-right"></span>
            </Link>
            {this.props.hr}
        </div>
        );
    }
}

HomePost.propTypes = {
    title: PropTypes.string.isRequired,
    author: PropTypes.string.isRequired,
    authorProfileURL: PropTypes.string.isRequired,
    timestamp: PropTypes.string.isRequired,
    textBody: PropTypes.string.isRequired,
    postURL: PropTypes.string.isRequired
}