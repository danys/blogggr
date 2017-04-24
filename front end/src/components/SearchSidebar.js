import React from 'react';
import Select from 'react-select'
import {get} from '../utils/ajax'
import { connect } from 'react-redux'
import debounce from 'lodash/debounce'

export class SearchSidebar extends React.Component{

    constructor(props){
        super(props);
        this.handleSearch = this.handleSearch.bind(this);
        this.updateTitle = this.updateTitle.bind(this);
        this.updatePoster = this.updatePoster.bind(this);
        this.handleRadio = this.handleRadio.bind(this);
        this.getOptions = this.getOptions.bind(this);
        this.updatePoster = this.updatePoster.bind(this);
        this.debouncedOptions = debounce(this.getOptions,500);
        this.usersURL = "/api/v1.0/users?search=";
    }

    handleSearch(){
        this.props.handleSearch();
    }

    updateTitle(event){
        this.props.updateTitle(event.target.value);
    }

    updatePoster(value){
        this.props.updatePoster(value); //value has label and value keys
    }

    handleRadio(event){
        this.props.updateVisibility(event.target.value);
    }

    getOptions(input, callback) {
        if (input.length<3) return;
        get(this.usersURL+input,
            {},
            (data)=>{
                const selectOptions = data.data.pageItems.map((obj)=>{let val = {};val['label']=obj.firstName+' '+obj.lastName;val['value']=obj.userID;return val;});
                callback(null, {
                    options: selectOptions,
                    complete: true
                })
            },
            (jqXHR)=>{
                console.log("Error getting matching users");
            },{'Authorization': this.props.token});
    };

    render(){
        const posterLabel = ('label' in this.props.poster)?this.props.poster.label:'';
        return (
        <div className="well">
            <h4>Search for blog posts</h4>
            <div className="form-group">
                <label htmlFor="titleSearchKey">Blog title</label>
                <input type="text" className="form-control" placeholder="Title" id="titleSearchKey" onChange={this.updateTitle} value={this.props.title}/>
            </div>
            <div className="form-group">
                <label htmlFor="posterSearchKey">Blog poster</label>
                <Select.Async
                    name="user-select"
                    multi={false}
                    value={posterLabel}
                    loadOptions={this.debouncedOptions}
                    onChange={this.updatePoster}
                    valueKey="label"
                    autoload={false}
                />
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
            <button type="button" className="btn btn-md btn-primary btn-block" onClick={this.handleSearch}>
                <span className="glyphicon glyphicon-search"> Search</span>
            </button>
            <br/>
            <h4>Create a new blog post</h4>
            <button type="button" className="btn btn-md btn-primary btn-block" onClick={this.props.showNewPostModal}>
                New post
            </button>
        </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token
});

export default connect(
    mapStateToProps,
    null
)(SearchSidebar);