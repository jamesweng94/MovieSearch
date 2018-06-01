
function handleLookup(title, doneCallback) {
    
    console.log("autocomplete initiated")
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
        var consoleOut = [];
        var sessionJsonData = JSON.parse(sessionStorage.getItem(title));

        sessionJsonData.forEach(function(item){
            consoleOut.push(item["value"]);
        })
        console.log("Movie List in the cache")
        console.log(consoleOut);
        doneCallback({suggestions: sessionJsonData})
    }
}

function handleSelectSuggestion(suggestion) {
	console.log("auto-complete query: " + suggestion["value"])
    window.location.href = "single-movie.html?name=" + suggestion["value"];
}

function handleLookupAjaxSuccess(data, title, doneCallback) {
    var consoleOutput = [];    
    var jsonData = JSON.parse(data);
    
    jsonData.forEach(function(item){
        consoleOutput.push(item["value"]);
    })
    
    console.log(consoleOutput)
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
