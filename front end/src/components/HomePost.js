import React from 'react'

export class HomePost extends React.Component {

    constructor(props){
        super(props);
    }

    render(){
        return (
            <div>
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
                </div>
        );
    }
}