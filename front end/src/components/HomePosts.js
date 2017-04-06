import React from 'react'

import Sidebar from './SearchSidebar'
import {get} from '../utils/ajax'
import { connect } from 'react-redux'
import {red}  from '../consts/Constants'
import {HomePost} from './HomePost'

export class HomePosts extends React.Component {

    constructor(props){
        super(props);
        this.searchPosts = this.searchPosts.bind(this);
        this.postsURL = "/api/v1.0/posts";
        this.state = {
        }
    }

    componentWillMount(){
        this.searchPosts();
    }

    searchPosts(title, postAuthor, visibility){
        let requestData = {};
        requestData['title']=title;
        //request['posterUserID']=postAuthor;
        requestData['visibility']=visibility;
        requestData['limit']=10;
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
            (post,index) => {return <HomePost title={post.title}
                                 author={post.user.firstName+' '+post.user.lastName}
                                 authorProfileURL={'/users/'+post.user.userID}
                                 timestamp={post.timestamp}
                                 textBody={post.textBody}
                                 postURL={'/posts/'+post.shortTitle}
                                 hr={(index==this.state.postsData.pageItems.length-1)?'':<hr/>}/>}
        );
        return (
            <div className="row">
                <div className="col-md-8">
                    <h1 className="page-header">Your blog posts' wall</h1>
                    {homePosts}
                </div>
                <div className="col-md-4">
                    <Sidebar handleSearch={this.searchPosts}/>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.token
});

/*const mapDispatchToProps = (dispatch) => {
    return {
        abc: () => {
            dispatch(...action)
        }
    }
};*/

export default connect(
    mapStateToProps,
    null
)(HomePosts);