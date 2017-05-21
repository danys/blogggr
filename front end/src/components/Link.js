import React from 'react';
import PropTypes from 'prop-types';
import { browserHistory } from 'react-router';

class Link extends React.Component{

    constructor(props){
        super(props);
    }

    handleClick(event){
        event.preventDefault();
        if (this.props.onClick) this.props.onClick(event);
        if (this.props.url!=='#') browserHistory.push(this.props.url);
    }

    render(){
        const text = this.props.children?this.props.children:this.props.text?this.props.text:null;
        const cssClass = this.props.cssClass?this.props.cssClass:null;
        let props = jQuery.extend( true, {}, this.props);
        if (this.props.text) delete props["text"];
        if (this.props.url) delete props["url"];
        if (this.props.onClick) delete props["onClick"];
        if (this.props.cssClass) delete props["cssClass"];
        return (
            cssClass?
                <a {...props} className={cssClass} onClick={this.handleClick.bind(this)} href={this.props.url}>{text}</a>
                :
                <a {...props} onClick={this.handleClick.bind(this)} href={this.props.url}>{text}</a>
        )
    }
}

export default Link;

Link.propTypes = {
    onClick: PropTypes.func,
    text: PropTypes.string,
    url: PropTypes.string,
    cssClass: PropTypes.string
};
