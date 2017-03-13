import { LOGIN, LOGOUT } from '../actions/action'

const initialState = {
    loggedin: false,
    token: ""
}


export function loginDetails(state = initialState, action) {
    switch (action.type) {
        case 'LOGIN':
            return Object.assign({}, state, {loggedin: true, token: action.token});
        case 'LOGOUT':
            return Object.assign({}, state, {loggedin: false, token: action.token});
        default:
            return state;
    }
}