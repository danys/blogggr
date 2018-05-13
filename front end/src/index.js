import React from 'react';
import ReactDOM from 'react-dom';
import { Route } from 'react-router';
import {BrowserRouter} from 'react-router-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import {loadState, saveState} from './utils/localStorage';

import App from './pages/App';
import reducer from './reducers/index';

// Vendor imports

//jQuery
import jQuery from 'jquery';

import moment from 'moment';

//Bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.min.js'

//Font awesome
import 'font-awesome/css/font-awesome.min.css'

//Fixed data table
import 'fixed-data-table-2/dist/fixed-data-table.min.css'

//App styles
import './styling/dist/css/style.css'
import 'react-select/dist/react-select.css'

//Img
import './styling/img/blogBgImage.png'
import './styling/img/blogCommentImage.png'

//Croppie
import 'croppie/croppie.js'
import 'croppie/croppie.css'
// End of vendor imports

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
