import React from 'react'
import ReactDOM from 'react-dom'
import { Router, Route, browserHistory } from 'react-router'
import { Provider } from 'react-redux'
import { createStore } from 'redux'

import App from './modules/App'
import Login from './modules/Login'
import Signup from './modules/Signup'
import {loginDetails} from './reducers/reducer'

const store = createStore(loginDetails);

ReactDOM.render(
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App}/>
            <Route path="/login" component={Login}/>
            <Route path="/signup" component={Signup}/>
        </Router>
    </Provider>,
    document.getElementById('app')
);
