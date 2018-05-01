function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleSingleStarResult(resultData) {
	
	let SingleStarTableBodyElement = jQuery("#single_star_table_body");

    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        
        rowHTML += "<th>" + resultData[i]["star_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["star_birthyear"] + "</th>";
        rowHTML += "<th>" + resultData[i]["star_movies"] + "</th>";
        
        rowHTML += "</tr>";

        SingleStarTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", 
    method: "GET", 
    url: "api/single-star", 
    data: {id: getParameterByName("id")},
    success: (resultData) => handleSingleStarResult(resultData) 
});