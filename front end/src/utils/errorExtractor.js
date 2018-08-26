export function getErrorMessage(responseText) {
  let jsonObject = JSON.parse(responseText);
  if (jsonObject.hasOwnProperty('errors')){
    let jsonErrors = jsonObject.errors;
    if ((jsonErrors instanceof Array) && (jsonErrors.length >= 1)){
      let firstError = jsonErrors[0];
      if (firstError instanceof String){
        let firstErrorString = JSON.stringify(firstError);
        return firstErrorString.substring(1,firstErrorString.length-1);
      } else {
        let errorString = JSON.stringify(firstError.message);
        return errorString.substring(1,errorString.length-1);
      }
    } else {
      return "Error message unparsable!";
    }
  } else {
    return "Error message unparsable!";
  }
}