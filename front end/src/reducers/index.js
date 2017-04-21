import { combineReducers } from 'redux'
import {SessionReducer as session} from './SessionReducer';
import {UserDataReducer as userData} from './UserDataReducer';
import {BlogSearchFilterReducer as blogSearchFilter} from './BlogSearchFilterReducer';

export default combineReducers({
    session: session,
    userData: userData,
    blogSearchFilter: blogSearchFilter
});