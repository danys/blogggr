import React from 'react';
import ReactDOM from 'react-dom';
import { Route, IndexRoute } from 'react-router';
import {BrowserRouter} from 'react-router-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import {loadState, saveState} from './utils/localStorage';

import {App} from './pages/App';
import reducer from './reducers/index';

const persistedState = loadState();
const store = createStore(reducer, persistedState);
store.subscribe(()=>{
    saveState(store.getState())
});

ReactDOM.render(
    <Provider store={store}>
        <BrowserRouter>
            <Route path="/" component={App} />
        </BrowserRouter>
    </Provider>,
    document.getElementById('app')
);
