import React from 'react'
import {Modal} from './Modal'

export class PostFormModal extends React.Component {

    constructor(props){
        super(props);
    }

    handleChange(key, event){
        this.props.onChange(key, event.target.value);
    }

    render(){
        return (
            <Modal {...this.props}>
                {this.props.data ?
                <form>
                    <div className="form-group">
                        <label htmlFor="createPostTitle">Post's title</label>
                        <input type="text" className="form-control" id="createPostTitle" placeholder="Title" onChange={this.handleChange.bind(this, 'title')} value={this.props.data.title?this.props.data.title:''} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="createPostText">Post's text</label>
                        <textarea rows="20" className="form-control" id="createPostText" placeholder="Text" onChange={this.handleChange.bind(this, 'textBody')} value={this.props.data.textBody?this.props.data.textBody:''}>
                    </textarea>
                    </div>
                    <div className="form-group">
                        <label htmlFor="createPostVisibility">Post visibility</label>
                        <select className="form-control" id="createPostVisibility" onChange={this.handleChange.bind(this, 'isGlobal')} value={this.props.data.isGlobal?this.props.data.isGlobal:'Global'}>
                            <option>Global</option>
                            <option>Restricted</option>
                        </select>
                    </div>
                </form>
                :
                this.props.text ?
                <div>{this.props.text}</div>
                :
                ''}
            </Modal>
        );
    }
}