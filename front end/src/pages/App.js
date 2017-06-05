import React from 'react'
import Navbar from '../components/navigation/Navbar'
import Footer from '../components/Footer'
import {Modal} from '../components/Modal'
import {red}  from '../consts/Constants'
import {Switch, Route} from 'react-router-dom'
import Login from './Login';
import {Signup} from './Signup';
import Post from './Post';
import BlogHome from './BlogHome';

export class App extends React.Component{

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
        const appRoutes = (
            <Switch>
                <Route exact path="/" render={()=><BlogHome showOverlayMsg={this.showOverlayMsg}/>} />
                <Route path="/login" render={()=><Login showOverlayMsg={this.showOverlayMsg}/>} />
                <Route path="/signup" render={()=><Signup showOverlayMsg={this.showOverlayMsg}/>} />
                <Route path="/users/:userID/posts/:postName" render={()=><Post showOverlayMsg={this.showOverlayMsg}/>} />
            </Switch>
        );
        return (
        <div>
            <Navbar highlight={this.props.location.pathname} showOverlayMsg={this.showOverlayMsg} router={this.props.router}/>
            <div className="container">
                {appRoutes}
                <Footer />
            </div>
            <Modal title={this.state.modalTitle} body={this.state.modalMsg} modalId='modal' color={this.state.color}/>
        </div>
        );
    }
}