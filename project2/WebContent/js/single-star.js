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
        
        var movies_row = "";
        resultData[i]["star_movies"].forEach(function(item, index){
           let temp = '<a href="single-movie.html?name=' + item + '">' + item +'</a>';
           movies_row += temp + ", ";
        })
        rowHTML += "<th class = 'movie_info'>" + movies_row + "</th>";  
        rowHTML += "</tr>";

        SingleStarTableBodyElement.append(rowHTML);
    }
}

$("#goCheckout").click(function(){
	window.location.replace("shopping-cart.html");
});


jQuery.ajax({
    dataType: "json", 
    method: "GET", 
    url: "api/single-star", 
    data: {name: getParameterByName("name")},
    success: (resultData) => handleSingleStarResult(resultData) 
});