import React from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, browserHistory, IndexRoute } from 'react-router';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import {loadState, saveState} from './utils/localStorage';

import App from './modules/App';
import Login from './modules/Login';
import {Signup} from './modules/Signup';
import Post from './modules/Post';
import BlogHome from './modules/BlogHome';
import reducer from './reducers/index';

const persistedState = loadState();
const store = createStore(reducer, persistedState);
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
                <Route path="/users/:userID/posts/:postName" component={Post} name="post"/>
                /* TODO: "friends", "user" path names to specify */
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app')
);
