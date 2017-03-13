export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';

export function loginAction(token, sessionURL){
    return {
      type: LOGIN,
      token: token,
      sessionsURL: sessionURL
    };
}

export function logoutAction(){
    return {
        type: LOGOUT,
        token: '',
        sessionURL: ''
    };
}