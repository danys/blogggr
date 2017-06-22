import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post} from '../utils/ajax'
import Link from '../components/navigation/Link'

class User extends React.Component{

    constructor(props){
        super(props);
        /*this.postsURL = "/api/v1.0/users/";
        this.commentsURL = "/api/v1.0/comments";
        this.state = {
            commentText: ''
        };
        this.fetchPost = this.fetchPost.bind(this);
        this.postComment = this.postComment.bind(this);
        this.handlePostCommentChange = this.handlePostCommentChange.bind(this);*/
    }

    /*fetchPost(){
        get(this.postsURL+this.props.match.params.userID+'/posts/'+this.props.match.params.postName,
            {},
            (data)=>{this.setState({postData: data.data})},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error retrieving details of the post!', errorMsg, red);
            },{'Authorization': this.props.token});
    }*/

    /*componentDidMount(){
        this.fetchPost();
    }

    postComment(){
        let requestData={};
        requestData["postID"]=(this.state.postData)?this.state.postData.postID:'';
        requestData["text"]=(this.state.postData)?this.state.commentText:'';
        post(this.commentsURL,
            requestData,
            ()=>{this.setState({commentText: ''},this.fetchPost())},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error posting comment!', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    handlePostCommentChange(event){
        this.setState({commentText: event.target.value});
    }*/

    render() {
        return (
            <div className="row">
                <div className="col-lg-6">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h3 className="panel-title">User details</h3>
                        </div>
                        <div className="panel-body">
                            Panel content
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token
});


export default withRouter(connect(
    mapStateToProps,
    null
)(User));
