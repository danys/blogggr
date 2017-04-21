import { UPDATE } from '../actions/UserDataActions';

const initialState = {
    firstName: '',
    lastName: '',
    email: '',
};


export function UserDataReducer(state = initialState, action) {
    switch (action.type) {
        case UPDATE:
            return Object.assign({}, state, {firstName: action.firstName, lastName: action.lastName, email: action.lastName});
        default:
            return state;
    }
}