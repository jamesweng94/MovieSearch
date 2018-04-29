
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

function handleListResult(resultData){
    let tableBodyElement = jQuery("#movie_table_body");
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i  < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>"; 

        rowHTML +=
        "<th>" +
        '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
        + resultData[i]["movie_title"] +   
        '</a>' +
        "</th>";   

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_dir"] + "</th>";   

        var j = 0;
        var generes_row = "";
        while(resultData[i]["movie_genres"][j] !== undefined){
            generes_row += (resultData[i]["movie_genres"][j] + " ");
            j++;
        }
        rowHTML += "<th>" + generes_row + "</th>";   
	
        /*
        let generes_row = "";
        resultData[i]["movie_genres"].forEach(function(item){
           genres_row += item + " ";
        })
        rowHTML+="<th>" + generes_row + "</th>";
        */

        let star_row = "";
        resultData[i]["movie_star"].forEach(function(item, index){
            let temp = '<a href="single-star.html?id=' + resultData[i]["movie_starID"] + '">' + item +'</a>';
           star_row += temp + ", ";
        })
        rowHTML += "<th>" + star_row + "</th>";   

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        tableBodyElement.append(rowHTML);
    }
}

let target = getParameterByName('search');
jQuery.ajax({
    dataType: "json", 
    url: "api/list?search=" + target,
    type: "GET",
    success: (resultData) => handleListResult(resultData),
    error: function(){
        alert("error");
    }           
});

$(document).ajaxComplete(function(){
    var currentLimit = 10;
    var rowCount = $("#movie_table tr").length;
    var totalPage;
    var limitChanged = false;
    //show limit number of content in a page
    $("#movie_table tr:gt(10)").hide();
    $('#page-limit').on('change', function(){
        pageLimit = this.value;
        $("#movie_table tr").show();
        $("#movie_table tr:gt(" + parseInt(pageLimit) + ")").hide();
        currentLimit = parseInt(pageLimit);
        limitChanged = true;

        totalPage = Math.ceil(rowCount/ currentLimit);
        $(".pagination li").remove(); 
        $(".pagination").append("<li id='previous-page'><a href='javascript:void(0)' aria-label=Previous><span aria-hidden=true>&laquo;</span></a></li>");
        $(".pagination").append("<li class='current-page active'><a href='javascript:void(0)'>" + 1 + "</a></li>");
        for (var i = 2; i <= totalPage; i++) {
            $(".pagination").append("<li class='current-page'><a href='javascript:void(0)'>" + i + "</a></li>");
        }
        $(".pagination").append("<li id='next-page'><a href='javascript:void(0)' aria-label=Next><span aria-hidden=true>&raquo;</span></a></li>");
        // pagination
        $(".pagination li.current-page").on("click", function() {
            if ($(this).hasClass('active')) {
                return false;
            } else {
                var currentPage = $(this).index(); 
                $(".pagination li").removeClass('active'); 
                $(this).addClass('active');
                $("#movie_table tr").hide();
                var grandTotal = currentLimit * currentPage;

                for (var i = grandTotal - currentLimit; i < grandTotal; i++) {
                $("#movie_table tr:eq(" + i + ")").show();
                }
            }
        })

        $("#next-page").on("click", function(){
            var current_page = $(".pagination li.active").index();
            if(current_page === totalPage){
                return false;
            }
            else{
                current_page++;
                $(".pagination li").removeClass('active');
                $("#movie_table tr").hide();
                var grandTotal = currentLimit * current_page;
                for (var i = grandTotal - currentLimit; i < grandTotal; i++) {
                $("#movie_table tr:eq(" + i + ")").show();
                }
                $(".pagination li.current-page:eq(" + (current_page - 1) + ")").addClass('active');
            }
        })

        $("#previous-page").on("click", function(){
            var current_page = $(".pagination li.active").index();
            if(current_page === 1){
                return false;
            }
            else{
                current_page--;
                $(".pagination li").removeClass('active');
                $("#movie_table tr").hide();
                var grandTotal = currentLimit * current_page;
                for (var i = grandTotal - currentLimit; i < grandTotal; i++) {
                $("#movie_table tr:eq(" + i + ")").show();
                }
                $(".pagination li.current-page:eq(" + (current_page - 1) + ")").addClass('active');
            }
        })
    })

    $('#page-limit')
    .val('10')
    .trigger('change');
})