import React from 'react';
import { Cell } from 'fixed-data-table-2';

export function getCellData(data, index, field, itemsPerPage, loadData){
    const pageNum = (Math.floor(index/itemsPerPage)).toString();
    const pageIndex = index%itemsPerPage;
    if (data.hasOwnProperty(pageNum)){
        return data[pageNum]['pageItems'][pageIndex][field];
    } else {
        loadData(pageNum);
        return null;
    }
}

export class TextCell extends React.Component {

    render() {
        const {rowIndex, field, data, itemsPerPage, loadUsers, ...props} = this.props;
        return (
            <Cell {...props}>
                {getCellData(data, rowIndex, field, itemsPerPage, loadUsers)}
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

export class ImageCell extends React.Component {

  render() {
    const {rowIndex, field, data, itemsPerPage, loadUsers, baseUrl, width, height, ...props} = this.props;
    const imageData = getCellData(data, rowIndex, field, itemsPerPage, loadUsers);
    const imageURL = (imageData!=null && imageData.hasOwnProperty('name'))? baseUrl+'/'+getCellData(data, rowIndex, field, itemsPerPage, loadUsers)['name']:'';
    return (
        <Cell {...props}>
          <img src={imageURL} width={width} height={height} />
        </Cell>
    );
  }
}