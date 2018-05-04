function handleResult(resultData) {
    let tableBodyElement = jQuery("#shopping_cart_table");
    if(!resultData[0]["movie_title"]){
        $("#message").text(resultData[0]["message"]);
        return;
    }

    for (let i = 0; i  < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th class = 'movie_info'>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th class = 'movie_info'>" + 
                    "<form class = 'update-item' method = 'GET'>" +
                    "<input id = 'qty_update' type = 'text' value = '"+ resultData[i]["movie_qty"] + "' name = 'qty'>" +
                    "<input type = 'hidden' value = 'update' name = 'todo'>"+
                    "<input type = 'hidden' value = '"+  resultData[i]["movie_title"]+ "' name = 'title'>"+
                    "<input type = 'submit' value = 'Update' class='btn' style = 'text-align:center; margin-left: 20px; font-size:14px'>"+
                  "</form></th>";

        rowHTML += "<th>" +
        "<div id = 'remove'>" + 
        "<form class = 'remove-item' method = 'GET'>" +
        "<input type = 'hidden' value = '"+ resultData[i]["movie_title"] + "' name = 'title'>" + 
        "<input type = 'hidden' value = 'remove' name = 'todo'>"+
        "<input type = 'submit' value = 'Delete Item' class='btn'></form></div>"
            + "</th>";

        rowHTML += "</tr>";
        tableBodyElement.append(rowHTML);       
    }
}

$("#backToMain").click(function(){
    window.location.replace("index.html");
})

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
/*
$(".update-item").submit(function(){
    $.ajax({
        url:"api/cart",
        dataType: "json",
        method: "POST", 
        data: {
            qty: urlParams["qty"],
            todo: urlParams["todo"],
            title: urlParams["title"]
        },
        success: (resultData) => handleResult(resultData)
    }); 
}); 

$(".update-item").submit(function(e){
    e.preventDefault();
    jQuery.post(
        "api/cart",
        jQuery("#login_form").serialize(),
        (resultData) => handleResult(resultData));
});

$(".remove-item").submit(function(){
    $.ajax({
        url:"api/cart",
        dataType: "json",
        method: "POST", 
        data: {
            qty: urlParams["qty"],
            todo: urlParams["todo"],
            title: urlParams["title"]
        },
        success: (resultData) => handleResult(resultData)
    }); 
}); 
*/
$.ajax({
    beforeSend: function(){
        var checkQty = urlParams["qty"];
        if(checkQty !== undefined){
        var getInt = parseInt(checkQty);
        if(getInt < 0){
            alert("Please enter a valid number");
            }
        }
    },
    url:"api/cart",
    dataType: "json",
    method: "POST", 
    data: {
        qty: urlParams["qty"],
        todo: urlParams["todo"],
        title: urlParams["title"]
    },
    success: (resultData) => handleResult(resultData)
});  
