import { UPDATE } from '../actions/UserDataActions';

const initialState = {
    firstName: '',
    lastName: ''
};


export function UserDataReducer(state = initialState, action) {
    switch (action.type) {
        case UPDATE:
            return Object.assign({}, state, {firstName: action.firstName, lastName: action.lastName});
        default:
            return state;
    }
}