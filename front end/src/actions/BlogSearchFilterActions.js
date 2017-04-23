export const SET_TITLE = 'SET_TITLE';
export const SET_POSTER = 'SET_POSTER';
export const SET_VISIBILITY = 'SET_VISIBILITY';

export function setTitle(title){
    return {
        type: SET_TITLE,
        title: title
    };
};

export function setPoster(poster){
    return {
        type: SET_POSTER,
        poster: poster
    };
};

export function setVisibility(visibility){
    return {
        type: SET_VISIBILITY,
        visibility: visibility
    };
};