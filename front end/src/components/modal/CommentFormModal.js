import React from 'react'
import {Modal} from './Modal'

export class CommentFormModal extends React.Component {

    constructor(props){
        super(props);
    }

    handleChange(key, event){
        this.props.onChange(key, event.target.value);
    }

    render(){
        const title = (this.props.actionType==='Edit')?'Edit comment':'Delete comment';
        const footerButtonCaption = (this.props.actionType==='Edit')?'Update':'Delete';
        const data = (this.props.actionType==='Edit')?this.props.data:null;
        const text = (this.props.actionType==='Edit')?null:'Do you really want to delete this comment?';
        return (
            <Modal title={title} footerButtonCaption={footerButtonCaption} >
                {data ?
                    <form>
                        <div className="form-group">
                            <label htmlFor="createPostTitle">Edit comment</label>
                            <input type="text" className="form-control" id="commentText" placeholder="Comment" onChange={this.handleChange.bind(this, 'text')} value={data.text?data.text:''} />
                        </div>
                    </form>
                    :
                    text ?
                        <div>{text}</div>
                        :
                        ''}
            </Modal>
        );
    }
}