export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';

export function loginAction(token){
    return {
      type: LOGIN,
      token: token
    };
}

export function logoutAction(){
    return {
        type: LOGOUT,
        token: ''
    };
}