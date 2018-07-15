$(document).ready(function() {
	project_id = GetRequest();
	getSubsystem();
});
var project_id;
var commitsInfo;
var projectName;
function getSubsystem() {
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
				projectName = data.projectName;
				$("#projectTitle").html(projectName);
				loadCommits();
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
	content += "<button type=\"button\" class=\"close\" onclick=\"deleteOneProject(this)\"><span>&times;</span></button>";
	content += "</td>";
	content += "<td>";
	content += commit.revisionId;
	content += "</td>";
	content += "<td>";
	content += deleteT(commit.commitDate);
	content += "</td>";
	content += "<td>";
	content += commit.fileNum;
	content += "</td>";
	content += "<td>";
	content += commit.cloneFileNum;
	content += "</td>";
	content += "<td>";
	content += commit.loc;
	content += "</td>";
	content += "<td>";
	content += commit.cmloc;
	content += "</td>";
	content += "<td>";
	content += commit.bloc;
	content += "</td>";
	content += "<td>";
	content += commit.eloc;
	content += "</td>";
	content += "<td>";
	content += commit.cloc;
	content += "</td>";
	content += "<td>";
	content += commit.cmcloc;
	content += "</td>";
	content += "<td>";
	content += commit.bcloc;
	content += "</td>";
	content += "<td>";
	content += commit.ecloc;
	content += "</td>";
	content += "<td>";
	content += commit.ccloc;
	content += "</td>";
	content += "<td>";
	content += commit.eccloc;
	content += "</td>";
	content += "</tr>";
	$("#printProjectsTable").append(content);
}
function deleteOneProject(element){
	var index = $("#printProjectsTable tr").index($(element).closest("tr"));
	$("#printProjectsTable tr:eq(" + index + ")").remove();
}

function loadCommits() {
	len = commitsInfo.length;
	for (i = 0; i < len; i++) {
		loadOneCommit(i);
	}
}
function printOneProject(){
	var ignore = [0];
	$("#printProjectsTable thead tr").find($("th")).each(function(index){
		if(!$(this).find("input[type='checkbox']").is(':checked')){
			ignore.push(index);
		}
	});
	$('#printProjectsTable').tableExport({type:'excel',escape:'true',ignoreColumn:ignore,fileName:'代码克隆-'+projectName+'-报告'});
}
function getPrintCommits(){
	$("#printProjectsTable tbody").html('');
	loadCommits();
}

function showPreArea(){
	var ignore = [0];
	$("#printProjectsTable thead tr").find($("th")).each(function(index){
		if(!$(this).find("input[type='checkbox']").is(':checked')){
			ignore.push(index);
		}
	});
	$("#preAreaTable").html($("#printProjectsTable").html());
	$("#preAreaTable th input").css("display","none");
	for(var i in ignore){
		$("#preAreaTable").find($("tr")).each(function(){
	        $("td:eq("+ignore[i]+")",this).hide();
	        $("th:eq("+ignore[i]+")",this).hide();
	    });
	}
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}