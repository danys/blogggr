import React from 'react'

export default class HomePosts extends React.Component {
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
                    <div className="well">
                        <h4>Blog Search</h4>
                        <div className="form-group">
                            <label htmlFor="titleSearchKey">Blog title</label>
                            <input type="text" className="form-control" placeholder="Title" id="titleSearchKey"/>
                        </div>
                        <div className="form-group">
                            <label htmlFor="posterSearchKey">Blog poster</label>
                            <input type="text" className="form-control" placeholder="User name" id="posterSearchKey"/>
                        </div>
                        <div className="form-group">
                            <label htmlFor="posterSearchKey">Post visibility</label>
                            <div className="radio">
                                <label>
                                    <input type="radio" name="searchVisibility" id="searchVisibilityAll" value="all"
                                           checked/>
                                    All posts
                                </label>
                            </div>
                            <div className="radio">
                                <label>
                                    <input type="radio" name="searchVisibility" id="searchVisibilityOnlyGlobal"
                                           value="onlyGlobal"/>
                                    Only global
                                </label>
                            </div>
                            <div className="radio">
                                <label>
                                    <input type="radio" name="searchVisibility" id="searchVisibilityOnlyFriends"
                                           value="onlyFriends"/>
                                    Only friends
                                </label>
                            </div>
                            <div className="radio">
                                <label>
                                    <input type="radio" name="searchVisibility" id="searchVisibilityOnlyMe"
                                           value="onlyMe"/>
                                    Only your posts
                                </label>
                            </div>
                        </div>
                        <button type="button" className="btn btn-sm btn-primary btn-block">
                            <span className="glyphicon glyphicon-search"> Search</span>
                        </button>
                    </div>
                </div>
            </div>
        );
    }
}