import React from 'react';
import PropTypes from 'prop-types';
import {withRouter} from 'react-router-dom';

class Link extends React.Component{

    constructor(props){
        super(props);
    }

    handleClick(event){
        event.preventDefault();
        if (this.props.onClick) this.props.onClick(event);
        if (this.props.url!=='#') this.props.history.push(this.props.url);
        window.scrollTo(0, 0);
    }

    render(){
        const text = this.props.children?this.props.children:this.props.text?this.props.text:null;
        const cssClass = this.props.cssClass?this.props.cssClass:null;
        let props = jQuery.extend( true, {}, this.props);
        if (this.props.hasOwnProperty('text')) delete props["text"];
        if (this.props.hasOwnProperty('url')) delete props["url"];
        if (this.props.hasOwnProperty('onClick')) delete props["onClick"];
        if (this.props.hasOwnProperty('cssClass')) delete props["cssClass"];
        if (this.props.hasOwnProperty('match')) delete props["match"];
        if (this.props.hasOwnProperty('location')) delete props["location"];
        if (this.props.hasOwnProperty('history')) delete props["history"];
        return (
            cssClass?
                <a {...props} className={cssClass} onClick={this.handleClick.bind(this)} href={this.props.url}>{text}</a>
                :
                <a {...props} onClick={this.handleClick.bind(this)} href={this.props.url}>{text}</a>
        )
    }
}

export default withRouter(Link);

Link.propTypes = {
    onClick: PropTypes.func,
    text: PropTypes.string,
    url: PropTypes.string,
    cssClass: PropTypes.string
};
