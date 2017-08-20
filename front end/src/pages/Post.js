import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post} from '../utils/ajax'
import {red}  from '../consts/Constants'
import Link from '../components/navigation/Link'
import {Sidebar} from '../components/sidebar/Sidebar'
import PostOptionsSidebarBody from '../components/sidebar/PostOptionsSidebarBody'
import {PostFormModal} from '../components/modal/PostFormModal'
import {CommentFormModal} from '../components/modal/CommentFormModal'
import {blue, green} from '../consts/Constants'
import {put, del} from '../utils/ajax'

class Post extends React.Component{

    constructor(props){
        super(props);
        this.userPostsURL = "/api/v1.0/users/";
        this.postsURL = "/api/v1.0/posts/";
        this.commentsURL = "/api/v1.0/comments";
        this.state = {
            commentText: '',
            modalTitle: '',
            modalButtonCaption: '',
            updatePostData: null,
            updateCommentData: null,
            action: '',
            modalText: '',
            commentAction: 'Edit'
        };
        this.fetchPost = this.fetchPost.bind(this);
        this.postComment = this.postComment.bind(this);
        this.handlePostCommentChange = this.handlePostCommentChange.bind(this);
    }

    fetchPost(props){
        get(this.userPostsURL+props.match.params.userId+'/posts/'+props.match.params.postName,
            {},
            (data)=>{this.setState({postData: data.data})},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                props.showOverlayMsg('Error retrieving details of the post!', errorMsg, red);
            },{'Authorization': props.token});
    }

    componentWillReceiveProps(nextProps){
        if (JSON.stringify(nextProps)===JSON.stringify(this.props)) return;
        this.fetchPost(nextProps);
    }

    componentDidMount(){
        this.fetchPost(this.props);
    }

    postComment(){
        let requestData={};
        requestData["postId"]=(this.state.postData)?this.state.postData.postId:'';
        requestData["text"]=(this.state.postData)?this.state.commentText:'';
        post(this.commentsURL,
            requestData,
            ()=>{this.setState({commentText: ''},this.fetchPost(this.props))},
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
        if (this.state.postData==null) return;
        const updatePostData = jQuery.extend(true, {}, this.state.postData);
        this.setState({modalTitle: 'Edit post', modalButtonCaption: 'Update', updatePostData: updatePostData, action: 'Edit', modalText: ''});
        $('#postModal').modal('show');
    }

    showDeletePostModal(){
        this.setState({modalTitle: 'Delete post', modalButtonCaption: 'Delete', updatePostData: null, action: 'Delete', modalText: 'Do you really want to delete this post?'});
        $('#postModal').modal('show');
    }

    modalAction(){
        if (!this.state.postData) return; //Error
        if (this.state.action==='Edit'){
            //PUT post
            let modalTitle = 'Edit post';
            let requestData = {};
            requestData.title=this.state.updatePostData.title;
            requestData.textBody=this.state.updatePostData.textBody;
            requestData.global = this.state.updatePostData.global;
            put(this.postsURL+this.state.postData.postId, requestData,
                (data, status, request)=>{
                    this.props.showOverlayMsg(modalTitle, 'Successfully updated post!', green);
                    let url = this.props.match.url;
                    let slashIndex = url.lastIndexOf('/');
                    url = url.substring(0,slashIndex);
                    url += '/';
                    url += data.data.shortTitle;
                    this.props.history.push(url);
                }, (jqXHR)=>{
                    let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                    errorMsg = errorMsg.substring(1,errorMsg.length-1);
                    this.props.showOverlayMsg(modalTitle, errorMsg, red);
                },
                {'Authorization': this.props.token}
            );
        } else if (this.state.action==='Delete'){
            //DELETE post
            let modalTitle = 'Delete post';
            del(this.postsURL+this.state.postData.postId,
                (data, status, request)=>{
                    this.props.showOverlayMsg(modalTitle, 'Successfully deleted post!', green);
                    this.props.history.push('/');
                }, (jqXHR)=>{
                    let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                    errorMsg = errorMsg.substring(1,errorMsg.length-1);
                    this.props.showOverlayMsg(modalTitle, errorMsg, red);
                },
                {'Authorization': this.props.token}
            );
        }
    }

    commentModalAction(){
        if (this.state.updateCommentData==null) return; //Error
        if (this.state.commentAction==='Edit'){
            //Update comment
            let modalTitle = 'Edit comment';
            let requestData = {};
            requestData.commentID=this.state.updateCommentData.commentID;
            requestData.text=this.state.updateCommentData.text;
            put(this.commentsURL+'/'+this.state.updateCommentData.commentID, requestData,
                (data, status, request)=>{
                    this.props.showOverlayMsg(modalTitle, 'Successfully updated comment!', green);
                    this.fetchPost(this.props);
                }, (jqXHR)=>{
                    let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                    errorMsg = errorMsg.substring(1,errorMsg.length-1);
                    this.props.showOverlayMsg(modalTitle, errorMsg, red);
                },
                {'Authorization': this.props.token}
            );
        } else if (this.state.commentAction==='Delete'){
            //DELETE post
            let modalTitle = 'Delete comment';
            del(this.commentsURL+'/'+this.state.updateCommentData.commentID,
                (data, status, request)=>{
                    this.props.showOverlayMsg(modalTitle, 'Successfully deleted comment!', green);
                    this.fetchPost(this.props);
                }, (jqXHR)=>{
                    let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                    errorMsg = errorMsg.substring(1,errorMsg.length-1);
                    this.props.showOverlayMsg(modalTitle, errorMsg, red);
                },
                {'Authorization': this.props.token}
            );
        }
    }

    modalFormChange(field, value){
        let post = this.state.updatePostData;
        if (field==='isGlobal') post.global = (value==='Global')?true:false;
        else post[field] = value;
        this.setState({updatePostData: post});
    }

    commentModalFormChange(field, value){
        let comment = this.state.updateCommentData;
        comment[field] = value;
        this.setState({updateCommentData: comment});
    }

    showCommentModal(actionType, index){
        if (this.state.postData==null) return;
        const updateCommentData = jQuery.extend(true, {}, this.state.postData.comments[index]);
        this.setState({commentAction: actionType, updateCommentData: updateCommentData});
        $('#commentModal').modal('show');
    }

    render() {
        const posterURL = (this.state.postData?'/users/'+this.state.postData.user.userId:'');
        let comments = (this.state.postData?this.state.postData.comments.map((comment, index)=>{
            return (
                <div key={index} className="media">
                    <Link cssClass="pull-left" url="#">
                        <img className="media-object" src="/blogCommentImage.png" alt="" />
                    </Link>
                    <div className="media-body">
                        <h4 className="media-heading">{comment.user.firstName+' '+comment.user.lastName}
                            <small className="marginLeftRight">{comment.timestamp}</small>
                            {(comment.user.email===this.props.email)?
                                <span>
                                    <button type="button" className="btn btn-md btn-info marginLeftRight" onClick={this.showCommentModal.bind(this,'Edit',index)}>
                                        <span className="glyphicon glyphicon-pencil"></span>
                                    </button>
                                    <button type="button" className="btn btn-md btn-danger marginLeftRight" onClick={this.showCommentModal.bind(this, 'Delete',index)}>
                                        <span className="glyphicon glyphicon-remove"></span>
                                    </button>
                                </span>
                                :
                                ''}
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
                <PostFormModal data={this.state.updatePostData} onChange={this.modalFormChange.bind(this)} title={this.state.modalTitle} text={this.state.modalText} footerAction={this.modalAction.bind(this)} modalId='postModal' footerButtonCaption={this.state.modalButtonCaption} hasFooter={true} />
                <CommentFormModal data={this.state.updateCommentData} actionType={this.state.commentAction} onChange={this.commentModalFormChange.bind(this)} footerAction={this.commentModalAction.bind(this)} modalId='commentModal' hasFooter={true} />
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
