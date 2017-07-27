import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import {get, post} from '../utils/ajax'
import {red}  from '../consts/Constants'
import Link from '../components/navigation/Link'
import {Sidebar} from '../components/sidebar/Sidebar'
import PostOptionsSidebarBody from '../components/sidebar/PostOptionsSidebarBody'
import {PostFormModal} from '../components/modal/PostFormModal'
import {CommentFormModal} from '../components/modal/CommentFormModal'
import {blue, green} from '../consts/Constants'
import {put, del} from '../utils/ajax'

import { Table, Column, Cell } from 'fixed-data-table-2';

class Friends extends React.Component{

    constructor(props){
        super(props);
        this.friendsURL = "/api/v1.0/friends/";
        this.state = {
            myTableData: [
                {name: 'Rylan'},
                {name: 'Amelia'},
                {name: 'Estevan'},
                {name: 'Florence'},
                {name: 'Tressa'}
            ]
        };
    }

    render() {
        return (
            <div className="row">
                <div className="col-lg-6">
                    <Table
                        rowsCount={this.state.myTableData.length}
                        rowHeight={50}
                        width={1000}
                        height={500}
                        headerHeight={50}>
                        <Column
                            header={<Cell>Name</Cell>}
                            cell={props => (
                                <Cell {...props}>
                                    {this.state.myTableData[props.rowIndex].name}
                                </Cell>
                            )}
                            width={200}
                        />
                    </Table>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    token: state.session.token,
    email: state.session.email
});


export default withRouter(connect(
    mapStateToProps,
    null
)(Friends));
