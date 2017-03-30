import React from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, browserHistory, IndexRoute } from 'react-router';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import {loadState, saveState} from './utils/localStorage';

import App from './modules/App';
import Login from './modules/Login';
import {Signup} from './modules/Signup';
import BlogHome from './modules/BlogHome';
import {loginDetails} from './reducers/reducer';

const persistedState = loadState();
const store = createStore(loginDetails, persistedState);
store.subscribe(()=>{
    saveState(store.getState())
});

ReactDOM.render(
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App}>
                <IndexRoute component={BlogHome} name="home"/>
                <Route path="/login" component={Login} name="login"/>
                <Route path="/signup" component={Signup} name="signup"/>
                /* TODO: "friends", "user" path names to specify */
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app')
);
