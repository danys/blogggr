import React from 'react'
import ReactDOM from 'react-dom'
import { Router, Route, browserHistory } from 'react-router'
import App from './modules/App'
import { Provider } from 'react-redux'
import { createStore } from 'redux'
import {loginDetails} from './reducers/reducer'

const store = createStore(loginDetails);

ReactDOM.render(
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App}/>
        </Router>
    </Provider>,
    document.getElementById('app')
);
