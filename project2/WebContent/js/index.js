
function handleLookup(title, doneCallback) {

    if(sessionStorage.getItem(title) == null){
        console.log("New query(not in cache): " + title)
        $.ajax({
            "method": "GET",
            "url": "api/suggestion?title=" + escape(title),
            "success": function(data) {
                handleLookupAjaxSuccess(data, title, doneCallback) 
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
    else{
        doneCallback({suggestions: JSON.parse(sessionStorage.getItem(title))})
    }
}

function handleSelectSuggestion(suggestion) {
	console.log("auto-complete query: " + suggestion["value"])
    window.location.href = "single-movie.html?name=" + suggestion["value"];
}

function handleLookupAjaxSuccess(data, title, doneCallback) {
    console.log("lookup ajax successful")
    
	var jsonData = JSON.parse(data);
    console.log(jsonData)
    sessionStorage.setItem(title, JSON.stringify(jsonData));
	doneCallback( { suggestions: jsonData} );
}


$("#autocomplete").autocomplete({
    lookup: function (title, doneCallback) {
        handleLookup(title, doneCallback);
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    minChars: 3,
    deferRequestBy: 300,
    groupBy: "category",
})
