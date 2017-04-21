export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';
export const RENEW_SESSION = 'RENEW_SESSION';

export function loginAction(token, sessionURL, validUntil){
    return {
      type: LOGIN,
      token: token,
      sessionURL: sessionURL,
      validUntil: validUntil
    };
}

export function logoutAction(){
    return {
        type: LOGOUT,
        token: '',
        sessionURL: '',
        validUntil: ''
    };
}

export function renewSessionAction(validUntil){
    return {
        type: RENEW_SESSION,
        validUntil: validUntil
    }
}