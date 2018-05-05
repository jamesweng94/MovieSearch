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
        rowHTML += "<th class = 'movie_info'>" + resultData[i]["movie_id"] + "</th>"; 
        rowHTML +=
        "<th class = 'movie_info'>" +
        '<a href="single-movie.html?name=' + resultData[i]['movie_title'] + '">'
        + resultData[i]["movie_title"] +   
        '</a>' +
        "</th>";   

        rowHTML += "<th class = 'movie_info'>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th class = 'movie_info'>" + resultData[i]["movie_dir"] + "</th>";   

        var j = 0;
        var generes_row = "";
        while(resultData[i]["movie_genres"][j] !== undefined){
            generes_row += (resultData[i]["movie_genres"][j] + " ") + ", ";
            j++;
        }
     
        rowHTML += "<th class = 'movie_info'>" + generes_row + "</th>";   
	
        var star_row = "";
        resultData[i]["movie_star"].forEach(function(item, index){
           let temp = '<a href="single-star.html?name=' + item + '">' + item +'</a>';
           star_row += temp + ", ";
        })
        rowHTML += "<th class = 'movie_info'>" + star_row + "</th>";   

        rowHTML += "<th class = 'movie_info'>" + resultData[i]["movie_rating"] + "</th>";

        rowHTML += "<th class = 'movie_info'>" +
        "<div id = 'single_movie_input'>" + 
        "<form id = 'add-to-cart' action = 'shopping-cart.html' method = 'GET'>" +
        "<input type = 'hidden' value = '"+ resultData[i]["movie_id"] + "' name = 'movieID'>" + 
        "<input type = 'hidden' value = '"+ resultData[i]["movie_title"] + "' name = 'title'>" + 
        "<input type = 'hidden' value = 'add' name = 'todo'>"+
        "<input type = 'submit' value = 'Add to cart' class='btn'></form></div>"
            + "</th>";

        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        tableBodyElement.append(rowHTML);
    }
}

function sortTable(f, n) {
    var rows = $('#movie_table tbody  tr').get();
    rows.sort(function(a, b) {
        var A = getVal(a);
        var B = getVal(b);

        if(A < B) {
            return -1 * f;
        }
        if(A > B) {
            return 1 * f;
        }
        return 0;
    })

    function getVal(elm){
        var v = $(elm).children('.movie_info').eq(n).text().toUpperCase();
        if($.isNumeric(v)){
            v = parseInt(v,10);
        }
        return v;
    }

    $.each(rows, function(index, row) {
        $('#movie_table').children('tbody').append(row);
    });
}

var f_sl = 1; // flag to toggle the sorting order
var f_nm = 1; // flag to toggle the sorting order
$("#sort_title").click(function(){
    f_sl *= -1; // toggle the sorting order
    var n = $(this).prevAll().length;
    sortTable(f_sl,n);
});
$("#sort_rating").click(function(){
    f_nm *= -1; // toggle the sorting order
    var n = $(this).prevAll().length;
    sortTable(f_nm,n);
});


var urlParams;
(window.onpopstate = function () {
    var match,
        pl     = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query  = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
       urlParams[decode(match[1])] = decode(match[2]);
})();

let action = getParameterByName('action');

if (!action) {
	
	jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/list",
        data: {action: "search",
        	   search: urlParams["search"],
        	   title: urlParams["title"],
        	   year: urlParams["year"],
        	   director: urlParams["director"],
               star: urlParams["star"],
               page: "1",
               limit: "10"},
        success: (resultData) => handleListResult(resultData)      
    });
}
else {
    jQuery.ajax({
        dataType: "json", 
        method: "GET", 
        url: "api/list",
        data: { action: "browse",
        		by: getParameterByName("by"),
                value: getParameterByName("value"),
                page: "1",
                limit: "10" },
        success: (resultData) => handleListResult(resultData) 
    });  
}

$("#goCheckout").click(function(){
	window.location.replace("shopping-cart.html");
});

$(document).ajaxComplete(function(){
    var currentLimit = 10;
    var rowCount = $("#movie_table tr").length;
    var totalPage;
    //show limit number of content in a page
    //$("#movie_table tr:gt(10)").hide();
    $('#page-limit').on('change', function(){
        pageLimit = this.value;
        $("#movie_table tr").show();
        $("#movie_table tr:gt(" + parseInt(pageLimit) + ")").hide();
        currentLimit = parseInt(pageLimit);

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
