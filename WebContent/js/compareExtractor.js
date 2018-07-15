$(document).ready(function() {
	project_id = GetRequest();
	getcompares();
});
var comparesInfo;
function getcompares() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractCompares.action",
		dataType : "json",
		data : {
			projectId : project_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				comparesInfo = data.compares;
				loadcompares();
				$('.datatable').dataTable({
					"oLanguage" : {
						"sUrl" : "./zh_CN.json"
					}
				});

			} else {
				alert("failed");
			}
		}
	});

}
function loadOnecompare(i) {
	var compare = comparesInfo[i];
	var iden = 'compare' + i;
	var content = "<tr id = " + iden + ">";
	content += "<td>";
	content += "<input name='' type='checkbox' />";
	content += "</td>";
	content += "<td>";
	content += compare.revisionId;
	content += "</td>";
	content += "<td>";
	content += compare.preRevisionId;
	content += "</td>";
	content += "<td>";
	content += commit.timeSpan;
	content += "</td>";
	content += "<td>";
	content += commit.chloc;
	content += "</td>";
	content += "<td>";
	content += commit.cloc;
	content += "</td>";
	content += "<td>";
	content += deleteT(compare.compareDate);
	content += "</td><td><a href='comparedetail.html?compareId=" + compare.compareId + "'>-></a></td>";
	content += "</tr>";
	$("#comparehis").append(content);
}
function loadCompares() {
	len = comparesInfo.length;
	for (i = 0; i < len; i++) {
		loadOneCompare(i);
	}

}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}
