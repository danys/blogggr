export function post(url, requestData, successHandler, errorHandler, headers={}) {
    jQuery.ajax({
        url: url,
        method: 'POST',
        data: JSON.stringify(requestData),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        headers: headers,
        success: successHandler,
        error: errorHandler
    });
}

function toURLString(obj){
    let queryString = '';
    for (let key in obj) {
        if (obj[key]!=undefined && obj[key]!=null && obj[key]!=''){
            queryString += key;
            queryString += '=';
            queryString += obj[key];
            queryString += '&';
        }
    }
    return queryString;
}

export function get(url, requestData, successHandler, errorHandler, headers={}) {
    jQuery.ajax({
        url: url,
        method: 'GET',
        data: toURLString(requestData),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        headers: headers,
        success: successHandler,
        error: errorHandler
    });
}

export function del(url, successHandler, errorHandler, headers={}) {
    jQuery.ajax({
        url: url,
        method: 'DELETE',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        headers: headers,
        success: successHandler,
        error: errorHandler
    });
}