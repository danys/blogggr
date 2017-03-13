import { LOGIN, LOGOUT } from '../actions/action'

const initialState = {
    loggedin: false,
    token: "",
    sessionURL: ""
}


export function loginDetails(state = initialState, action) {
    switch (action.type) {
        case 'LOGIN':
            return Object.assign({}, state, {loggedin: true, token: action.token, sessionURL: action.sessionURL});
        case 'LOGOUT':
            return Object.assign({}, state, {loggedin: false, token: action.token, sessionURL: action.sessionURL});
        default:
            return state;
    }
}