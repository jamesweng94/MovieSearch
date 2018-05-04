function handleCheckoutResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("printed?");
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
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
