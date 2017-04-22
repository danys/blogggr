import React from 'react';
import Select from 'react-select'
import {get} from '../utils/ajax'
import { connect } from 'react-redux'

export class SearchSidebar extends React.Component{

    constructor(props){
        super(props);
        this.handleSearch = this.handleSearch.bind(this);
        this.updateTitle = this.updateTitle.bind(this);
        this.updatePoster = this.updatePoster.bind(this);
        this.handleRadio = this.handleRadio.bind(this);
        this.getOptions = this.getOptions.bind(this);
        this.usersURL = "/api/v1.0/users?search=";
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

    getOptions(input, callback) {
        setTimeout(() => {
            get(this.usersURL+input,
                {},
                (data)=>{
                    const selectOptions = data.data.pageItems.map((obj)=>{let val = {};val['label']=obj.firstName+' '+obj.lastName;val['value']=obj.userID;return val;});
                    callback(null, {
                        options: selectOptions
                    })
                },
                (jqXHR)=>{
                    console.log("Error getting matching users");
                },{'Authorization': this.props.token});
        }, 500);
    };

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
                <Select.Async
                    name="user-select"
                    value={this.props.postUserName}
                    loadOptions={this.getOptions}
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
            <button type="button" className="btn btn-sm btn-primary btn-block" onClick={this.handleSearch}>
                <span className="glyphicon glyphicon-search"> Search</span>
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