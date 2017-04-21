export const UPDATE = 'UPDATE';

export function updateUserData(firstName, lastName, email){
    return {
        type: UPDATE,
        firstName: firstName,
        lastName: lastName,
        email: email
    };
}