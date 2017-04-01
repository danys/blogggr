import React from 'react'
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'
import {Modal} from '../components/Modal'
import {red}  from '../consts/Constants'

export default class App extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            modalTitle: 'Error',
            modalMsg: 'Error',
            color: red
        };
        this.showOverlayMsg = this.showOverlayMsg.bind(this);
    }

    componentDidMount(){
        $('#modal').on('hidden.bs.modal', () => {this.setState({modalTitle:'',modalMsg: '', color: red})});
    }

    showOverlayMsg(title, msg, color){
        this.setState({modalMsg: msg, modalTitle: title, color: color});
        $('#modal').modal('show');
    }

    render(){
        //Modify children props
        let childrenWithProps = React.Children.map(this.props.children, (child) => {
            return React.cloneElement(child, {
                showOverlayMsg: this.showOverlayMsg
            })
        });
        return (
        <div>
            <Navbar highlight={this.props.children.props.route.name} router={this.props.router}/>
            <div className="container">
                {childrenWithProps}
                <Footer />
            </div>
            <Modal title={this.state.modalTitle} body={this.state.modalMsg} modalId='modal' color={this.state.color}/>
        </div>
        );
    }
}