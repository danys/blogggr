import React, {PropTypes} from 'react';
import Link from './Link';

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
    title: React.PropTypes.string.isRequired,
    author: React.PropTypes.string.isRequired,
    authorProfileURL: React.PropTypes.string.isRequired,
    timestamp: React.PropTypes.string.isRequired,
    textBody: React.PropTypes.string.isRequired,
    postURL: React.PropTypes.string.isRequired
}