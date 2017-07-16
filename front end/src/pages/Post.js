import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post} from '../utils/ajax'
import {red}  from '../consts/Constants'
import Link from '../components/navigation/Link'
import {Sidebar} from '../components/sidebar/Sidebar'
import PostOptionsSidebarBody from '../components/sidebar/PostOptionsSidebarBody'
import {PostForm} from '../components/modal/PostFormModal'
import {Modal} from '../components/modal/Modal'

class Post extends React.Component{

    constructor(props){
        super(props);
        this.userPostsURL = "/api/v1.0/users/";
        this.postsURL = "/api/v1.0/posts/";
        this.commentsURL = "/api/v1.0/comments";
        this.state = {
            commentText: ''
        };
        this.fetchPost = this.fetchPost.bind(this);
        this.postComment = this.postComment.bind(this);
        this.handlePostCommentChange = this.handlePostCommentChange.bind(this);
    }

    fetchPost(){
        get(this.userPostsURL+this.props.match.params.userID+'/posts/'+this.props.match.params.postName,
            {},
            (data)=>{this.setState({postData: data.data})},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error retrieving details of the post!', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    componentDidMount(){
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
    }

    showEditPostModal(){
        //
    }

    showDeletePostModal(){
        //
    }

    render() {
        const posterURL = (this.state.postData?'/users/'+this.state.postData.user.userID:'');
        let comments = (this.state.postData?this.state.postData.comments.map((comment, index)=>{
            return (
                <div key={index} className="media">
                    <Link cssClass="pull-left" url="#">
                        <img className="media-object" src="/blogCommentImage.png" alt="" />
                    </Link>
                    <div className="media-body">
                        <h4 className="media-heading">{comment.user.firstName+' '+comment.user.lastName}
                            <small>{comment.timestamp}</small>
                        </h4>
                        {comment.text}
                    </div>
                </div>
            )
            }):null);
        //If the current user is the author of the post: show edit and delete options
        let sidebar = '';
        if (this.state.postData && this.props.email===this.state.postData.user.email){
            sidebar = (
                <div className="col-md-4">
                    <Sidebar title="Post options">
                        <PostOptionsSidebarBody editModal={this.showEditPostModal.bind(this)} deleteModal={this.showDeletePostModal.bind(this)} />
                    </Sidebar>
                </div>
            );
        }
        return (
            <div className="row">
                <div className="col-lg-8">
                    <h1>{this.state.postData?this.state.postData.title:null}</h1>
                    <p className="lead">
                        by <Link text={this.state.postData?this.state.postData.user.firstName+' '+this.state.postData.user.lastName:null} url={posterURL} />
                    </p>
                    <hr />
                    <p><span className="glyphicon glyphicon-time"></span> Posted on {this.state.postData?this.state.postData.timestamp:null}</p>
                    <hr />
                    <img className="img-responsive" src="/blogBgImage.png" alt="" />
                    <hr />
                    <p className="lead">{this.state.postData?this.state.postData.textBody:null}</p>
                    <hr />
                    <div className="well">
                        <h4>Leave a Comment:</h4>
                        <form role="form">
                            <div className="form-group">
                                <textarea className="form-control" rows="3" value={this.state.commentText} onChange={this.handlePostCommentChange}></textarea>
                            </div>
                            <button type="button" className="btn btn-primary" onClick={this.postComment}>Submit</button>
                        </form>
                    </div>
                    <hr />
                    {comments}
                </div>
                {sidebar}
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token,
    email: state.session.email
});


export default withRouter(connect(
    mapStateToProps,
    null
)(Post));
