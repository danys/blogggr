export const UPDATE = 'UPDATE';

export function updateUserData(firstName, lastName){
    return {
        type: UPDATE,
        firstName: firstName,
        lastName: lastName
    };
}