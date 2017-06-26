import React from 'react'

import HomePosts from '../components/HomePosts'
import Welcome from '../components/Welcome'

const BlogHome = (props) => {
    let content =  (props.loggedin()===true)?<HomePosts showOverlayMsg={props.showOverlayMsg}/>:<Welcome/>;
    return (
        content
    );
}

export default BlogHome;