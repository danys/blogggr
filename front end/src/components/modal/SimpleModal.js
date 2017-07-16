import React from 'react'
import {Modal} from './Modal'

export class SimpleModal extends React.Component {

    constructor(props){
        super(props);
    }

    render(){
        return (
            <Modal {...this.props}>
                <div>{this.props.message}</div>
            </Modal>
        );
    }
}