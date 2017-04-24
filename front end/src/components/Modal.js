import React, {PropTypes} from 'react'

export class Modal extends React.Component {

    constructor(props){
        super(props);
    }

    render(){
        const idAttr = {id: this.props.modalId};
        const headerColorStyle = {backgroundColor: this.props.color};
        const footerCode = (
            <div className="modal-footer">
                <button type="button" className="btn btn-default" data-dismiss="modal">{this.props.footerButtonCaption}</button>
            </div>
        );
        const footer = (this.props.hasFooter)?footerCode:null;
        return (
        <div {...idAttr} className="modal fade" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header" style={headerColorStyle}>
                        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 className="modal-title">{this.props.title}</h4>
                    </div>
                    <div className="modal-body">
                        {this.props.body}
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
    //body: PropTypes.node
}

Modal.defaultProps = {
    title: 'Title',
    //body: 'Text body',
    hasFooter: false,
    footerButtonCaption: 'Save'
}