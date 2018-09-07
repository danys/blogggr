import React from 'react';
import { Cell } from 'fixed-data-table-2';

export function getCellData(data, index, field, itemsPerPage){
    const pageNum = (Math.floor(index/itemsPerPage)).toString();
    const pageIndex = index%itemsPerPage;
    if (data.hasOwnProperty(pageNum)){
        return data[pageNum]['pageItems'][pageIndex][field];
    } else {
        this.props.loadUsers(pageNum);
        return null;
    }
}

export class TextCell extends React.Component {

    constructor(props){
        super(props);
        this.getCellData = this.getCellData.bind(this);
    }

    getCellData(data, index, field, itemsPerPage){
        const pageNum = (Math.floor(index/itemsPerPage)).toString();
        const pageIndex = index%itemsPerPage;
        if (data.hasOwnProperty(pageNum)){
            return data[pageNum]['pageItems'][pageIndex][field];
        } else {
            this.props.loadUsers(pageNum);
            return null;
        }
    }

    render() {
        const {rowIndex, field, data, itemsPerPage, loadUsers, ...props} = this.props;
        return (
            <Cell {...props}>
                {this.getCellData(data, rowIndex, field, itemsPerPage)}
            </Cell>
        );
    }
}

export class InputHeaderCell extends React.Component {

    onInputChange(event){
        event.stopPropagation();
        this.props.onChange(event.target.value);
    }

    render() {
        const {field, data, ...props} = this.props;
        return (
            <Cell {...props}>
                <input type="text"
                       className="form-control"
                       placeholder={field}
                       onChange={this.onInputChange.bind(this)}
                       value={data}
                />
            </Cell>
        );
    }
}

export class FixedHeaderCell extends React.Component {

  render() {
    const {field, ...props} = this.props;
    return (
        <Cell {...props}>
          {field}
        </Cell>
    );
  }
}