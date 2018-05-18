
function handleInsertStarResult(resultData) {

    if (resultData["status"] === "success") {
    	alert("New star inserted successfully!")
    }
    
    else {
        alert(resultData["message"]);
    }
}



function submitInsertForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/insert-star",
        jQuery("#insert-form").serialize(),
        (resultData) => handleInsertStarResult(resultData) 
    );
}


jQuery("#insert-form").submit((event) => submitInsertForm(event));