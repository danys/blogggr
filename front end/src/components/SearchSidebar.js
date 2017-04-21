import React from 'react'

export default class SearchSidebar extends React.Component{

    constructor(props){
        super(props);
        this.handleSearch = this.handleSearch.bind(this);
        this.updateTitle = this.updateTitle.bind(this);
        this.updatePoster = this.updatePoster.bind(this);
        this.handleRadio = this.handleRadio.bind(this);
    }

    handleSearch(){
        this.props.handleSearch();
    }

    updateTitle(event){
        this.props.updateTitle(event.target.value);
    }

    updatePoster(event){
        this.props.updatePoster(event.target.value);
    }

    handleRadio(event){
        this.props.updateVisibility(event.target.value);
    }

    render(){
        return (
        <div className="well">
            <h4>Blog Search</h4>
            <div className="form-group">
                <label htmlFor="titleSearchKey">Blog title</label>
                <input type="text" className="form-control" placeholder="Title" id="titleSearchKey" onChange={this.updateTitle} value={this.props.title}/>
            </div>
            <div className="form-group">
                <label htmlFor="posterSearchKey">Blog poster</label>
                <input type="text" className="form-control" placeholder="User name" id="posterSearchKey" onChange={this.updatePoster} value={this.props.postUserID}/>
            </div>
            <div className="form-group">
                <label htmlFor="posterSearchKey">Post visibility</label>
                <div className="radio">
                    <label>
                        <input type="radio" name="searchVisibility" id="searchVisibilityAll" value="all"
                               checked={this.props.visibility==='all'} onChange={this.handleRadio}/>
                        All posts
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" name="searchVisibility" id="searchVisibilityOnlyGlobal"
                               value="onlyGlobal" checked={this.props.visibility==='onlyGlobal'} onChange={this.handleRadio}/>
                        Only global
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" name="searchVisibility" id="searchVisibilityOnlyFriends"
                               value="onlyFriends" checked={this.props.visibility==='onlyFriends'} onChange={this.handleRadio}/>
                        Only friends
                    </label>
                </div>
                <div className="radio">
                    <label>
                        <input type="radio" name="searchVisibility" id="searchVisibilityOnlyMe"
                               value="onlyMe" checked={this.props.visibility==='onlyMe'} onChange={this.handleRadio}/>
                        Only your posts
                    </label>
                </div>
            </div>
            <button type="button" className="btn btn-sm btn-primary btn-block" onClick={this.handleSearch}>
                <span className="glyphicon glyphicon-search"> Search</span>
            </button>
        </div>
        );
    }
}