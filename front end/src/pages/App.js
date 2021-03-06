import React from 'react';
import Navbar from '../components/navigation/Navbar';
import Footer from '../components/Footer';
import {SimpleModal} from '../components/modal/SimpleModal';
import {red}  from '../consts/Constants';
import {Switch, Route} from 'react-router-dom';
import Login from './Login';
import {Signup} from './Signup';
import Friends from './Friends';
import Post from './Post';
import BlogHome from './BlogHome';
import User from './User';
import Settings from './Settings';
import {AuthRoute} from '../routes/AuthRoute';
import { connect } from 'react-redux';
import { logoutAction } from '../actions/SessionActions'

class App extends React.Component{

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
        $('#modal').on('hidden.bs.modal', () => {
            this.setState({modalTitle:'',modalMsg: '', color: red});
            //Prevent padding-right:10px from being added to body
            $(document.body).removeAttr('style');
            $(document.body).removeAttr('class');
        });
    }

    showOverlayMsg(title, msg, color){
        this.setState({modalMsg: msg, modalTitle: title, color: color});
        $('#modal').modal('show');
    }

    //Check if auth token is still valid
    authIsOK(){
        let validTillDateTime = moment(this.props.validUntil,"YYYY-MM-DD HH:mm:ss");
        if ((this.props.token!=='') && (moment().isAfter(validTillDateTime))) {
            this.props.removeToken();
            return false;
        }
        else if (this.props.token!=='') return true;
        else return false;
    }

    render(){
        const appRoutes = (
            <Switch>
                <Route exact path="/" render={()=><BlogHome loggedin={this.authIsOK.bind(this)} showOverlayMsg={this.showOverlayMsg}/>} />
                <Route path="/login" render={()=><Login showOverlayMsg={this.showOverlayMsg}/>} />
                <Route path="/signup" render={()=><Signup showOverlayMsg={this.showOverlayMsg}/>} />
                <AuthRoute loggedin={this.authIsOK.bind(this)} path="/users/:userId/posts/:postName" render={()=><Post showOverlayMsg={this.showOverlayMsg}/>} />
                <AuthRoute loggedin={this.authIsOK.bind(this)} path="/users/:userId" render={()=><User showOverlayMsg={this.showOverlayMsg}/>} />
                <AuthRoute loggedin={this.authIsOK.bind(this)} path="/settings" render={()=><Settings showOverlayMsg={this.showOverlayMsg}/>} />
                <AuthRoute loggedin={this.authIsOK.bind(this)} path="/friends" render={()=><Friends showOverlayMsg={this.showOverlayMsg}/>} />
            </Switch>
        );
        return (
        <div>
            <Navbar highlight={this.props.location.pathname} showOverlayMsg={this.showOverlayMsg} router={this.props.router}/>
            <div className="container">
                {appRoutes}
                <Footer />
            </div>
            <SimpleModal message={this.state.modalMsg} title={this.state.modalTitle} modalId='modal' color={this.state.color} />
        </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.session.token,
        validUntil: state.session.validUntil
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        removeToken: () => {
            dispatch(logoutAction())
        }
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(App);