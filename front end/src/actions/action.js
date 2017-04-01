export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';

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