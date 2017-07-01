import { LOGIN, LOGOUT, RENEW_SESSION } from '../actions/SessionActions'

const initialState = {
    loggedin: false,
    token: '',
    validUntil: ''
}


export function SessionReducer(state = initialState, action) {
    switch (action.type) {
        case LOGIN:
            return Object.assign({}, state, {loggedin: true, token: action.token, validUntil: action.validUntil});
        case LOGOUT:
            return Object.assign({}, state, {loggedin: false, token: action.token, validUntil: action.validUntil});
        case RENEW_SESSION:
            return Object.assign({}, state, {validUntil: action.validUntil});
        default:
            return state;
    }
}