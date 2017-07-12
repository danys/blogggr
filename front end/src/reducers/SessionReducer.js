import { LOGIN, LOGOUT, RENEW_SESSION } from '../actions/SessionActions'

const initialState = {
    token: '',
    validUntil: '',
    email: ''
}


export function SessionReducer(state = initialState, action) {
    switch (action.type) {
        case LOGIN:
            return Object.assign({}, state, {token: action.token, validUntil: action.validUntil, email: action.email});
        case LOGOUT:
            return Object.assign({}, state, {token: action.token, validUntil: action.validUntil, email: action.email});
        case RENEW_SESSION:
            return Object.assign({}, state, {validUntil: action.validUntil});
        default:
            return state;
    }
}