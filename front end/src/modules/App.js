import React from 'react'
import Navbar from '../components/Navbar'
import { connect } from 'react-redux'
import Footer from '../components/Footer'

export class App extends React.Component{

    constructor(props){
        super(props);
        const isLoggedIn = this.props.loggedin === 'true' ? true : false;
        this.state = {
            loggedin: isLoggedIn,
            navHighlight: "home"
        }
    }

    componentWillReceiveProps(nextProps){
        const isLoggedIn = nextProps.loggedin === 'true' ? true : false;
        this.setState({
            loggedin: isLoggedIn,
            navHighlight: nextProps.navHighlight
        });
    }

    render(){
        return (
        <div>
            <Navbar loggedin={this.state.loggedin} highlight={this.state.navHighlight}/>
            <div className="container">
                {this.props.children}
                <Footer />
            </div>
        </div>
        );
    }
}

const mapStateToProps = (state) => ({
    loggedin: state.loggedin
})

const mapDispatchToProps = (dispatch) => ({
    //
})

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(App)