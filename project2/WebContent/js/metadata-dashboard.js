function handleListResult(resultData) {
	let tableBodyElement = jQuery("#metadata_table_body");
	
	for (let i = 0; i < resultData.length; i++) {
		let tableHTML = "<h2> " + resultData[i]["table_name"].toUpperCase() + " </h2>" +
						"<table id=metadata-table class=\"table table-striped\">" + 
						"<thead> <tr>" +
						"<th> Attributes </th>" + 
						"<th> Types </th>" + 
						"</tr> </thead>" +
						"<tbody>";
		
		var column_names = resultData[i]["column_name"].split(", ");
		var column_types = resultData[i]["column_type"].split(", ");
		
		for (var j = 0; j < column_names.length; j++) {
			let rowHTML = " ";
			rowHTML += " <tr> ";
			rowHTML += "<th>" + column_names[j] + "</th>"; 
			rowHTML += "<th>" + column_types[j] + "</th>";
			rowHTML += " </tr> ";
			tableHTML +=  rowHTML;
		}
		
		tableHTML += "</tbody> </table> <p> <br> </p>";
		tableBodyElement.append(tableHTML);
	}
	
}



jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleListResult(resultData)      
});