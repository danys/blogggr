import React from 'react';
import { connect } from 'react-redux';

class PostOptionsSidebarBody extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return (
            <div>
                <button type="button" className="btn btn-md btn-info btn-block" onClick={this.props.editModal.bind(this)}>Edit post</button>
                <button type="button" className="btn btn-md btn-danger btn-block" onClick={this.props.deleteModal.bind(this)}>Delete post</button>
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
)(PostOptionsSidebarBody);