import React from 'react'

import SearchSidebarBody from './sidebar/SearchSidebarBody'
import {Sidebar} from './sidebar/Sidebar'
import {get,post} from '../utils/ajax'
import { connect } from 'react-redux'
import {red, green, blue}  from '../consts/Constants'
import {HomePost} from './HomePost'
import { updateUserData } from '../actions/UserDataActions'
import { setTitle, setPoster, setVisibility } from '../actions/BlogSearchFilterActions'
import {PostFormModal} from './modal/PostFormModal'
import PrevNext from './navigation/PrevNext'
import {getErrorMessage} from '../utils/errorExtractor';

export class HomePosts extends React.Component {

    constructor(props){
        super(props);
        this.searchPosts = this.searchPosts.bind(this);
        this.searchTitleUpdate = this.searchTitleUpdate.bind(this);
        this.searchBlogPosterUpdate = this.searchBlogPosterUpdate.bind(this);
        this.searchVisibilityUpdate = this.searchVisibilityUpdate.bind(this);
        this.loadUserDetails = this.loadUserDetails.bind(this);
        this.postsURL = "/api/v1.0/posts";
        this.userMeURL = "/api/v1.0/users/me";
        this.state = {
            title: this.props.title,
            poster: {}, //gets label and value keys
            visibility: this.props.visibility,
            limit: 10,
            newPost: {global: true}
        }
    }

    loadUserDetails(){
        get(this.userMeURL,
            {},
            (data)=>{this.setState({userData: data.data},
                ()=>{this.props.storeUserData(this.state.userData.firstName, this.state.userData.lastName);});
            },
            (jqXHR)=>{
                this.props.showOverlayMsg('Error retrieving details of the current user!', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    componentWillMount(){
        this.loadUserDetails();
        this.searchPosts();
    }

    searchTitleUpdate(newTitle){
        this.setState({title: newTitle});
        this.props.setSearchTitle(newTitle);
    }

    searchBlogPosterUpdate(selectedValue){
        if (selectedValue==null) selectedValue = {};
        this.setState({poster: selectedValue});
        this.props.setSearchPoster(selectedValue);
    }

    searchVisibilityUpdate(visibility){
        this.setState({visibility: visibility});
        this.props.setSearchVisibility(visibility);
    }

    searchPosts(){
        let requestData = {};
        if (this.state.title!=undefined && this.state.title!=='') requestData['title']=this.state.title;
        if (this.state.poster!=undefined && 'value' in this.state.poster) requestData['posteruserId']=this.state.poster.value;
        if (this.state.visibility!=undefined && this.state.visibility!='') requestData['visibility']=this.state.visibility;
        if (this.state.limit!=undefined && this.state.limit!='') requestData['limit']=this.state.limit;
        get(this.postsURL,
            requestData,
            (data)=>{this.setState({postsData: data.data});},
            (jqXHR)=>{
                this.props.showOverlayMsg('Posts search error', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    showCreatePost(){
        $('#newPostModal').modal('show');
    }

    createPost(){
        const data = this.state.newPost;
        let requestData = {};
        requestData.title=data.title;
        requestData.textBody=data.textBody;
        requestData.global = data.global;
        post(this.postsURL, requestData,
            (data)=>{
                this.props.showOverlayMsg('Success', 'Successfully created post!', green);
                const initVisibility = {global: 'Global'};
                this.setState({newPost: initVisibility});
                this.searchPosts();
            },
            (jqXHR)=>{
                this.props.showOverlayMsg('Error creating post', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    updateNewPost(key, value){
        const newPost = this.state.newPost;
        if (key==='isGlobal') newPost.global = (value==='Global')?true:false;
        else newPost[key] = value;
        this.setState({newPost: newPost});
    }

    handlePrevClick(){
        get(this.state.postsData.pageData.previous,
            {},
            (data)=>{this.setState({postsData: data.data});},
            (jqXHR)=>{
                this.props.showOverlayMsg('Posts search error', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    handleNextClick(){
        get(this.state.postsData.pageData.next,
            {},
            (data)=>{this.setState({postsData: data.data});},
            (jqXHR)=>{
                this.props.showOverlayMsg('Posts search error', getErrorMessage(jqXHR.responseText), red);
            },{'Authorization': this.props.token});
    }

    render() {
        const homePosts = (this.state.postsData==null || this.state.postsData.pageItems.length==0)?'No matching posts found!':this.state.postsData.pageItems.map(
            (post,index) => {return <HomePost key={index} title={post.title}
                                 author={post.user.firstName+' '+post.user.lastName}
                                 authorProfileURL={'/users/'+post.user.userId}
                                 timestamp={post.timestamp}
                                 textBody={post.textBody}
                                 postURL={'/users/'+post.user.userId+'/posts/'+post.shortTitle}
                                 hr={(index==this.state.postsData.pageItems.length-1)?'':<hr/>}/>}
        );
        const userFirst = (this.state.userData==null || this.state.userData.firstName==null)?null:this.state.userData.firstName;
        const userLast = (this.state.userData==null || this.state.userData.lastName==null)?null:this.state.userData.lastName;
        return (
            <div>
                <div className="row">
                    <div className="col-md-8">
                        <div className="userHomeName">Hello, {userFirst} {userLast}</div>
                        <h1 className="page-header">Blog posts</h1>
                        {homePosts}
                        <PrevNext
                            prev={(this.state.postsData && this.state.postsData.pageData && this.state.postsData.pageData.previous)?this.handlePrevClick.bind(this):null}
                            next={(this.state.postsData && this.state.postsData.pageData && this.state.postsData.pageData.next)?this.handleNextClick.bind(this):null}
                            prevUrl="/"
                            nextUrl="/"
                            rev="true"
                        />
                    </div>
                    <div className="col-md-4">
                        <Sidebar title="Search for blog posts">
                            <SearchSidebarBody
                                handleSearch={this.searchPosts}
                                updateTitle={this.searchTitleUpdate}
                                updatePoster={this.searchBlogPosterUpdate}
                                updateVisibility={this.searchVisibilityUpdate}
                                title={this.state.title}
                                poster={this.state.poster}
                                visibility={this.state.visibility}
                                showNewPostModal={this.showCreatePost}/>
                        </Sidebar>
                    </div>
                </div>
                <PostFormModal data={this.state.newPost} title={'Create a new post'} onChange={this.updateNewPost.bind(this)} footerAction={this.createPost.bind(this)} modalId='newPostModal' footerButtonCaption='Save' color={blue} hasFooter={true} />
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token,
    posteruserId: state.blogSearchFilter.postuserId,
    posterUserName: state.blogSearchFilter.postUserName,
    title: state.blogSearchFilter.title,
    visibility: state.blogSearchFilter.visibility,
});

const mapDispatchToProps = (dispatch) => {
    return {
        storeUserData: (firstName, lastName) => {
            dispatch(updateUserData(firstName, lastName))
        },
        setSearchTitle: (title) => {
            dispatch(setTitle(title))
        },
        setSearchPoster: (poster) => {
            dispatch(setPoster(poster))
        },
        setSearchVisibility: (visibility) => {
            dispatch(setVisibility(visibility))
        },
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(HomePosts);