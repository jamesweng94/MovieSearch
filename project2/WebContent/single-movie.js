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
    console.log("handleResult: populating movie table from resultData");

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
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";  

        var j = 0;
        var generes_row = "";
        while(resultData[i]["movie_genres"][j] !== undefined){
            var temp = '<a href="movie-list.html?genres=' + resultData[i]["movie_genres"][j] + '">' + resultData[i]["movie_genres"][j] +'</a>';
            generes_row += temp + " ";
            j++;
        }
        rowHTML += "<th>" + generes_row + "</th>";  

        let star_row = "";
        resultData[i]["movie_star"].forEach(function(item){
            let temp = '<a href="single-star.html?id=' + item + '">' + item +'</a>';
           star_row += temp + ", ";
        })
        rowHTML += "<th>" + star_row + "</th>"; 

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


let movieID = getParameterByName('id');

jQuery.ajax({
    dataType: "json",  
    method: "GET",
    url: "api/single-movie?id=" + movieID,
    success: (resultData) => handleResult(resultData)
});