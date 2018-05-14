/**
 * @param resultDataString jsonObject
 */

function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        alert(resultDataJson["message"]);
        
    }
}


/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
	
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/login",
        // Serialize the login form to the data sent by POST request
        jQuery("#login_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));
        /*        
         *  function a(resultDataString) {
        	return handleLoginResult(resultDataString));
        },
        */

}
// Bind the submit action of the form to a handler function
jQuery("#login_form").submit((event) => submitLoginForm(event));
