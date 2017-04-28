import React, {PropTypes} from 'react'

export class Modal extends React.Component {

    constructor(props){
        super(props);
        this.state={
        };
    }

    handleButton(){
        this.props.footerAction(this.state);
    }

    getBodyState(key, value){
        this.setState({[key]:value});
    }

    render(){
        const idAttr = {id: this.props.modalId};
        const headerColorStyle = {backgroundColor: this.props.color};
        const footerCode = (
            <div className="modal-footer">
                <button type="button" className="btn btn-default" data-dismiss="modal" onClick={this.handleButton.bind(this)}>{this.props.footerButtonCaption}</button>
            </div>
        );
        const footer = (this.props.hasFooter)?footerCode:null;
        const body = (typeof this.props.body==='undefined' || typeof this.props.body==='string')?this.props.body:React.cloneElement(this.props.body,{onChange: this.getBodyState.bind(this)});
        return (
        <div {...idAttr} className="modal fade" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header" style={headerColorStyle}>
                        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 className="modal-title">{this.props.title}</h4>
                    </div>
                    <div className="modal-body">
                        {body}
                    </div>
                    {footer}
                </div>
            </div>
        </div>
        );
    }
}

Modal.propTypes = {
    title: PropTypes.string.isRequired,
    footerAction: PropTypes.func,
}

Modal.defaultProps = {
    title: 'Title',
    hasFooter: false,
    footerButtonCaption: 'Save'
}