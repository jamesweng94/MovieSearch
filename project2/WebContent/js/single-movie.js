function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>"; 
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_dir"] + "</th>";

        var j = 0;
        var generes_row = "";
        while(resultData[i]["movie_genres"][j] !== undefined){
            var temp = '<a href="movie-list.html?action=browse&by=genre&value=' + resultData[i]["movie_genres"][j] + '">' + resultData[i]["movie_genres"][j] +'</a>';
            generes_row += temp + " ";
            j++;
        }
        rowHTML += "<th>" + generes_row + "</th>";  

        let star_row = "";
        resultData[i]["movie_star"].forEach(function(item){
            let temp = '<a href="single-star.html?name=' + item + '">' + item +'</a>';
           star_row += temp + ", ";
        })
        rowHTML += "<th>" + star_row + "</th>"; 

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        rowHTML += "<th>" +
        "<div id = 'single_movie_input'>" + 
        "<form class = 'add-to-cart' action = 'shopping-cart.html' method = 'GET'>" +
        "<input type = 'hidden' value = '"+ resultData[i]["movie_id"] + "' name = 'movieID'>" + 
        "<input type = 'hidden' value = '"+ resultData[i]["movie_title"] + "' name = 'title'>" + 
        "<input type = 'hidden' value = 'add' name = 'todo'>"+
        "<input type = 'submit' value = 'Add to cart' class='btn'></form></div>"
            + "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

$("#goCheckout").click(function(){
	window.location.replace("shopping-cart.html");
});

let movieID = getParameterByName('name');
jQuery.ajax({
    dataType: "json",  
    method: "GET",
    url: "api/single-movie?name=" + movieID,
    success: (resultData) => handleResult(resultData)
});