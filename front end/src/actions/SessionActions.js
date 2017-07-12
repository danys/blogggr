export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';
export const RENEW_SESSION = 'RENEW_SESSION';

export function loginAction(token, validUntil, email){
    return {
      type: LOGIN,
      token: token,
      validUntil: validUntil,
      email: email
    };
}

export function logoutAction(){
    return {
        type: LOGOUT,
        token: '',
        validUntil: '',
        email: ''
    };
}

export function renewSessionAction(validUntil){
    return {
        type: RENEW_SESSION,
        validUntil: validUntil
    }
}