import React from 'react'

import Sidebar from './SearchSidebar'
import {get} from '../utils/ajax'
import { connect } from 'react-redux'
import {red}  from '../consts/Constants'

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
            (data)=>{this.setState({postsData: data});},
            (jqXHR)=>{
                let errorMsg = JSON.stringify(JSON.parse(jqXHR.responseText).error);
                errorMsg = errorMsg.substring(1,errorMsg.length-1);
                this.props.showOverlayMsg('Posts search error', errorMsg, red);
            },{'Authorization': this.props.token});
    }

    render() {
        return (
            <div className="row">
                <div className="col-md-8">
                    <h1 className="page-header">Your blog posts' wall</h1>

                    <h2>
                        <a href="blogpost.html">Blog Post Title</a>
                    </h2>
                    <p className="lead">
                        by <a href="index.php">Start Bootstrap</a>
                    </p>
                    <p><span className="glyphicon glyphicon-time"></span> Posted on August 28, 2013 at 10:00 PM</p>
                    <hr/>
                    <img className="img-responsive" src="/dist/blogBgImage.png" alt=""/>
                    <hr/>
                    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolore, veritatis, tempora,
                        necessitatibus inventore nisi quam quia repellat ut tempore laborum possimus eum dicta id animi
                        corrupti debitis ipsum officiis rerum.</p>
                    <a className="btn btn-primary" href="#">Read More <span
                        className="glyphicon glyphicon-chevron-right"></span></a>


                    <hr/>
                    <h2>
                        <a href="#">Blog Post Title</a>
                    </h2>
                    <p className="lead">
                        by <a href="index.php">Start Bootstrap</a>
                    </p>
                    <p><span className="glyphicon glyphicon-time"></span> Posted on August 28, 2013 at 10:45 PM</p>
                    <hr/>
                    <img className="img-responsive" src="/dist/blogBgImage.png" alt=""/>
                    <hr/>
                    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quibusdam, quasi, fugiat, asperiores
                        harum voluptatum tenetur a possimus nesciunt quod accusamus saepe tempora ipsam distinctio
                        minima dolorum perferendis labore impedit voluptates!</p>
                    <a className="btn btn-primary" href="#">Read More <span
                        className="glyphicon glyphicon-chevron-right"></span></a>
                    <hr/>
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