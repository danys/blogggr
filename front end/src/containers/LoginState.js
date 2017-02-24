import { connect } from 'react-redux'
import { App } from '../modules/App'

const mapStateToProps = (state) => ({
    loggedin: state.loggedin
})

const mapDispatchToProps = (dispatch) => ({
    //
})

const LoginState = connect(
    mapStateToProps,
    mapDispatchToProps
)(App)

export default LoginState