import React from 'react'

import Sidebar from './SearchSidebar'
import {get} from '../utils/ajax'
import { connect } from 'react-redux'
import {red}  from '../consts/Constants'
import {HomePost} from './HomePost'
import { updateUserData } from '../actions/UserDataActions'
import { setTitle, setPoster, setVisibility } from '../actions/BlogSearchFilterActions'

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
            postUserID: this.props.posterUserID,
            postUserName: '', //TODO
            visibility: this.props.visibility,
            limit: 10
        }
    }

    loadUserDetails(){
        get(this.userMeURL,
            {},
            (data)=>{this.setState({userData: data.data},
                ()=>{this.props.storeUserData(this.state.userData.firstName, this.state.userData.lastName, this.state.userData.email);});
            },
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Error retrieving details of the current user!', errorMsg, red);
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

    searchBlogPosterUpdate(blogPoster){
        //Perform a search for blogPoster and retrieve names and IDS
        this.setState({posterUserID: blogPoster});
        //this.props.setSearchPoster(blogPoster);
        //TODO set postername AND ID
    }

    searchVisibilityUpdate(visibility){
        this.setState({visibility: visibility});
        this.props.setSearchVisibility(visibility);
    }

    searchPosts(){
        let requestData = {};
        if (this.state.title!=undefined && this.state.title!=='') requestData['title']=this.state.title;
        if (this.state.postUserID!=undefined && this.state.postUserID!='') requestData['posterUserID']=this.state.postUserID;
        if (this.state.visibility!=undefined && this.state.visibility!='') requestData['visibility']=this.state.visibility;
        if (this.state.limit!=undefined && this.state.limit!='') requestData['limit']=this.state.limit;
        get(this.postsURL,
            requestData,
            (data)=>{this.setState({postsData: data.data});},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Posts search error', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    render() {
        const homePosts = (this.state.postsData==null)?null:this.state.postsData.pageItems.map(
            (post,index) => {return <HomePost key={index} title={post.title}
                                 author={post.user.firstName+' '+post.user.lastName}
                                 authorProfileURL={'/users/'+post.user.userID}
                                 timestamp={post.timestamp}
                                 textBody={post.textBody}
                                 postURL={'/posts/'+post.shortTitle}
                                 hr={(index==this.state.postsData.pageItems.length-1)?'':<hr/>}/>}
        );
        const userFirst = (this.state.userData==null || this.state.userData.firstName==null)?null:this.state.userData.firstName;
        const userLast = (this.state.userData==null || this.state.userData.lastName==null)?null:this.state.userData.lastName;
        return (
            <div className="row">
                <div className="col-md-8">
                    <h2>Hi, {userFirst} {userLast}</h2>
                    <h1 className="page-header">Blog posts</h1>
                    {homePosts}
                </div>
                <div className="col-md-4">
                    <Sidebar handleSearch={this.searchPosts}
                             updateTitle={this.searchTitleUpdate}
                             updatePoster={this.searchBlogPosterUpdate}
                             updateVisibility={this.searchVisibilityUpdate}
                             title={this.state.title}
                             postUserID={this.state.postUserID}
                             postUserName={this.state.postUserName}
                             visibility={this.state.visibility}
                    />
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token,
    posterUserID: state.blogSearchFilter.posterUserID,
    title: state.blogSearchFilter.title,
    visibility: state.blogSearchFilter.visibility,
});

const mapDispatchToProps = (dispatch) => {
    return {
        storeUserData: (firstName, lastName, email) => {
            dispatch(updateUserData(firstName, lastName, email))
        },
        setSearchTitle: (title) => {
            dispatch(setTitle(title))
        },
        setSearchPoster: (posterID, posterName) => {
            dispatch(setPoster(posterID, posterName))
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