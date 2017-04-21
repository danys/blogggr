import { SET_TITLE, SET_POSTER, SET_VISIBILITY } from '../actions/BlogSearchFilterActions';

const initialState = {
    title: '',
    postUserID: '',
    postUserName: '',
    visibility: 'all',
};

export function BlogSearchFilterReducer(state = initialState, action) {
    switch (action.type) {
        case SET_TITLE:
            return Object.assign({}, state, {title: action.title});
        case SET_POSTER:
            return Object.assign({}, state, {postUserID: action.postUserID, postUserName: action.postUserName});
        case SET_VISIBILITY:
            return Object.assign({}, state, {visibility: action.visibility});
        default:
            return state;
    }
}