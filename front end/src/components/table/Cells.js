import React from 'react';
import { Cell } from 'fixed-data-table-2';

export class TextCell extends React.Component {
    render() {
        const {rowIndex, field, data, ...props} = this.props;
        return (
            <Cell {...props}>
                {data[rowIndex][field]}
            </Cell>
        );
    }
}

export class InputHeaderCell extends React.Component {

    onChange(event){
        this.props.onChange(event.target.value);
    }

    render() {
        const {field, data, ...props} = this.props;
        return (
            <Cell {...props}>
                <input type="text"
                       className="form-control"
                       placeholder={field}
                       onChange={this.onChange.bind(this)}
                       value={data}
                />
            </Cell>
        );
    }
}