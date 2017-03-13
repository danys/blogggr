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