import React,{PropTypes} from 'react'

export default class SearchSidebar extends React.Component{

    constructor(props){
        super(props);
        this.handleSearch = this.handleSearch.bind(this);
    }

    handleSearch(){
        const title = jQuery("#titleSearchKey").val();
        const postAuthor = jQuery("#posterSearchKey").val();
        let visibility;
        if (jQuery("#searchVisibilityAll").is(":checked")) visibility = 'all';
        else if (jQuery("#searchVisibilityOnlyGlobal").is(":checked")) visibility = 'onlyGlobal';
        else if (jQuery("#searchVisibilityOnlyFriends").is(":checked")) visibility = 'onlyFriends';
        else if (jQuery("#onlyMe").is(":checked")) visibility = 'onlyCurrentUser';
        this.props.handleSearch(title, postAuthor, visibility);
    }

    render(){
        return (
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
            <button type="button" className="btn btn-sm btn-primary btn-block" onClick={this.handleSearch}>
                <span className="glyphicon glyphicon-search"> Search</span>
            </button>
        </div>
        );
    }
}

SearchSidebar.propTypes = {
    handleSearch: React.PropTypes.func
}