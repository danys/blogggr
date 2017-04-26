import React from 'react'

export default class CreatePostForm extends React.Component {

    constructor(props){
        super(props);
    }

    render(){

        return (
            <form>
                <div className="form-group">
                    <label htmlFor="createPostTitle">Post's title</label>
                    <input type="text" className="form-control" id="createPostTitle" placeholder="Title" />
                </div>
                <div className="form-group">
                    <label htmlFor="createPostText">Post's text</label>
                    <textarea rows="20" className="form-control" id="createPostText" placeholder="Text">
                    </textarea>
                </div>
            </form>
        );
    }
}