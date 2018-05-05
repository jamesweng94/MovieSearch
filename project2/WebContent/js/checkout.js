function handleCheckoutResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html?firstName=" + resultDataJson["first_name"]+"&lastName=" + resultDataJson["last_name"] + "&creditid="+resultDataJson["credit_id"]);
    }
    else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert(resultDataJson["message"]);
        
    }
}

function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        jQuery("#customer_info").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

jQuery("#customer_info").submit((event) => submitCheckoutForm(event));
