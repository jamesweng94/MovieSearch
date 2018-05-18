/**
 * @param resultDataString jsonObject
 */

function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    if (resultDataJson["status"] === "success") {
    	window.location.replace("welcome-dashboard.html")
    	
    }
 
    else {
        alert(resultDataJson["message"]);
    }
}


/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
	
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/employee-login",
        jQuery("#employee-form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));

}

jQuery("#employee-form").submit((event) => submitLoginForm(event));