$("#backToMain").click(function(){
    window.location.replace("index.html");
})

var titles = [];
var qty = [];
function handleResult(resultData) {
    let tableBodyElement = jQuery("#shopping_cart_table");
    if(!resultData[0]["movie_title"]){
        $("#message").text(resultData[0]["message"]);
        return;
    }

    for (let i = 0; i  < resultData.length; i++) {
        titles.push(resultData[i]["movie_title"]);
        qty.push(resultData[i]["movie_qty"]);
        
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_qty"] + "</th>";
        rowHTML += "</tr>";
        tableBodyElement.append(rowHTML);      
    
    }
}


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

var salesId = [];
function resultHandler(result){
    console.log("size: "+ result.length);
   // let tableBodyElement = jQuery("#movieID_body");
    for (let i = 0; i  < result.length; i++) {
        $.ajax({
            url:"api/sales",
            dataType:"json",
            method:"POST",
            data:{
                movieID: result[i]["movieID"],
                firstName: urlParams["firstName"],
                lastName: urlParams["lastName"],
                creditID: urlParams["creditid"]
            }
        }).done(
            $.ajax({
            url:"api/sales",
            dataType:"json",
            method:"POST",
            data:{
                todo: "findId",
                movieID: result[i]["movieID"]
            },
            success: function(salesResult){
                    salesId.push(salesResult["sale_id"]);
                    let tableBodyElement = jQuery("#sales_id_table");
                    let rowHTML = "";
                    rowHTML += "<tr>";
                    rowHTML += "<th>" + salesResult["sale_id"] + "</th>";
                    rowHTML += "</tr>";
                    tableBodyElement.after(rowHTML);  
                    
            }
        })
        )
        
    } 
 }


$.ajax({
    url:"api/cart",
    dataType: "json",
    method: "POST", 
    data: {
        todo: "view"
    },
    success: (resultData) => handleResult(resultData)
})

$.ajax({
    url:"api/cart",
    dataType: "json",
    method: "POST", 
    data: {
        todo: "getId"
    },
    success: (resultData) => resultHandler(resultData)
})
