function handleInsertMovieResult(resultData) {

    if (resultData["status"] === "success") {
    	alert(resultData["message"]);
    }
    
    else {
        alert(resultData["message"]);
    }
}



function submitInsertForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/insert-movie",
        jQuery("#insert-form").serialize(),
        (resultData) => handleInsertMovieResult(resultData) 
    );
}


jQuery("#insert-form").submit((event) => submitInsertForm(event));