$(document).ready(function() {
	project_id = GetRequest();
	getCommits();
});
var project_id;
var projectName;
var commitsInfo;
function getCommits() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractVersions.action",
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
				commitsInfo = data.commits;
				loadCommits();
				$("#projectName").text(data.projectName);
			} else {
				alert("failed");
			}
		}
	});

}
function loadOneCommit(i) {
	var commit = commitsInfo[i];
	var iden = 'commit' + i;
	var content = "<tr id = " + iden + ">";
	content += "<td>";
	content += commit.revisionId;
	content += "</td>";
	content += "<td>";
	content += "<input class=\"projectinput\" data-id=\""+commit.commitId+"\" data-index=\""+i+"\" value=\""+commit.commitCode+"\"/>";
	content += "</td>";
	content += "</tr>";
	$("#version").append(content);
}
function loadCommits() {
	len = commitsInfo.length;
	for (i = 0; i < len; i++) {
		loadOneCommit(i);
	}
}
function updateVersionNumber(){
	var updateId = [];
	var updateVal = [];
	$("#version tbody").find($("input")).each(function(){
		var val = $(this).val();
		var i = $(this).attr("data-index");
		if(val != commitsInfo[i].commitCode && val != (""+commitsInfo[i].commitCode)){
			updateId.push($(this).attr("data-id"));
			updateVal.push(val);
		}
	});

	$.ajax({
		type : "post",
		url : "../Zhonghui/updateVersion.action",
		dataType : "json",
		data : $.param({
			id : updateId,
			val : updateVal
		},true),
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				alert("finished");
			} else {
				alert("error");
			}
		}
	});
}