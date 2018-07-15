$(document).ready(function() {
	setDate();
	getSubsystem();
});
var projectsInfo;
var commitsInfo;
var commitLs;
function getSubsystem() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractProjects.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				projectsInfo = data.projects;
				loadCommits();
			} else {
				alert("failed");
			}
		}
	});

}
function setDate(){
	var today = new Date();
	var older = new Date();
	older.setDate(older.getDate()-30);
	var temp1 = older.getFullYear()+"-"+(older.getMonth()+1)+"-"+older.getDate();
	var temp2 = today.getFullYear()+"-"+(today.getMonth()+1)+"-"+today.getDate();
	$('#date1').val(temp1);
	$('#date2').val(temp2);
}

function deleteOneProject(element){
	var index = $("#printProjectsTable tr").index($(element).closest("tr"));
	$("#printProjectsTable tr:eq(" + index + ")").remove();
}

function loadCommits() {
	len = projectsInfo.length;
	var projectIdList = [];
	for (var i = 0; i < len; i++) {
		projectIdList.push(projectsInfo[i].projectId);
	}
	$.ajax({
			type : "post",
			url : "../Zhonghui/extractLastCommit.action",
			dataType : "json",
			data : $.param({'projects':projectIdList},true),
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("commit XMLHttpRequest:" + XMLHttpRequest.status+"/XMLHttpRequest:" + XMLHttpRequest.readyState);
			},
			success : function(data) {
				if (data.successful == "true" || data.successful == true){
					commitsInfo = data.lastCommits;
					// loadPrintProjects();
					refresh();
				} else {
					alert("failed");
				}
			}
	});
}
function printTableLanguage(){
	var ignore = [0];
	$("#printProjectsTable thead tr").find($("th")).each(function(index){
		if(!$(this).find("input[type='checkbox']").is(':checked')){
			ignore.push(index);
		}
	});
	$('#printProjectsTable').tableExport({type:'excel',escape:'true',ignoreColumn:ignore,fileName:'代码克隆月度扫描报告(细分语言)'});
}
function printTableAll(){
	var ignore = [0];
	$("#printProjectsTable thead tr").find($("th")).each(function(index){
		if(!$(this).find("input[type='checkbox']").is(':checked')){
			ignore.push(index);
		}
	});
	$('#printProjectsTable').tableExport({type:'excel',escape:'true',ignoreColumn:ignore,fileName:'代码克隆月度扫描报告(汇总)'});
}
function getPrintProjects(){
	$("#printProjectsTable tbody").html('');
	// loadPrintProjects();
	refresh();
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

function refresh(){
	var date1 = new Date($('#date1').val());
	var date2 = new Date($('#date2').val());
	var diff = diffDays($('#date1').val(), $('#date2').val());

	if(isNull($('#date1').val())||isNull($('#date2').val())||diff < 0){
		alert("请正确填写起止时间");
		return;
	}else{
		refreshPrintProjects();
	}
}
function diffDays(date1, date2){
	var temp1 = new Date(date1);
	var temp2 = new Date(date2);

	var days = temp2.getTime() - temp1.getTime();
	return parseInt(days / (1000 * 60 * 60 * 24));
}
function refreshOnePrintProject(i) {
	var date1 = new Date($('#date1').val());
	var date2 = new Date($('#date2').val());
	var diff = diffDays($('#date1').val(), $('#date2').val());

	var project = projectsInfo[i];
	var commit = commitsInfo[i];
	var iden = 'project' + i;
	var commitLs;
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractOneVersion.action",
		dataType : "json",
		data : {
			commitId : commit.commitId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				commit = data.commit;
				commitLs = data.commitLs;

				var diffDay = diffDays($('#date1').val(), commit.commitDate);
				if(diffDay < diff && diffDay > 0){
					for(var i in commitLs){
						var content = "<tr id = " + iden + ">";
						content += "<td>";
						content += "<button type=\"button\" class=\"close\" onclick=\"deleteOneProject(this)\"><span>&times;</span></button>";
						content += "</td>";
						content += "<td>";
						content += project.projectTeam;
						content += "</td>";
						content += "<td>";
						content += project.projectNameCh;
						content += "</td>";
						content += "<td>";
						content += project.developCompany;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].language;
						content += "</td>";
						content += "<td>";
						content += commit.revisionId;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].loc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].cmloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].bloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].eloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].cloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].cmcloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].bcloc;
						content += "</td>";
						content += "<td>";
						content += commitLs[i].ecloc;
						content += "</td>";
						content += "<td>";
						content += commit.ccloc;
						content += "</td>";
						content += "<td>";
						content += commit.eccloc;
						content += "</td>";
						content += "<td>";
						content += commit.fileNum;
						content += "</td>";
						content += "<td>";
						content += commit.cloneFileNum;
						content += "</td>";
						content += "</tr>";
						$("#printProjectsTable").append(content);
					}
				}
			} else {
				alert("failed");
			}
		}
	});
}
function refreshPrintProjects(){
	$("#printProjectsTable tbody").html("");
	len = projectsInfo.length;
	for (var i = 0; i < len; i++) {
		refreshOnePrintProject(i);
	}
}
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}