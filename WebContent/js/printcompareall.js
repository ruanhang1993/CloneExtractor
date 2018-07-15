$(document).ready(function() {
	getSubsystem();
});
var projectsInfo;
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
				loadPrintProjects();
			} else {
				alert("failed");
			}
		}
	});

}

function loadOnePrintProject(i) {
	var project = projectsInfo[i];
	var iden = 'project' + i;
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractCompares.action",
		dataType : "json",
		data : {
			projectId : project.projectId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var comparesInfo = data.compares;
				if(comparesInfo.length>0){
					$.ajax({
						type : "post",
						url : "../Zhonghui/extractOneVersion.action",
						dataType : "json",
						data : {
							commitId : comparesInfo[0].commitId
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("XMLHttpRequest:" + XMLHttpRequest.status);
							console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
						},
						success : function(data) {
							if (data.successful == "true" || data.successful == true) {
								var commitLs = data.commitLs;
								var languageAll = "";
								for(var i in commitLs){
									languageAll = languageAll+"/"+commitLs[i].language;
								}
								for(var i in comparesInfo){
									var rate = ((comparesInfo[i].cloc+comparesInfo[i].dcloc)/(comparesInfo[i].aloc+comparesInfo[i].dloc) * 100).toFixed(2);
									var eRate = ((comparesInfo[i].ecloc+comparesInfo[i].edcloc)/(comparesInfo[i].edloc+comparesInfo[i].ealoc) * 100).toFixed(2);
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
									content += languageAll;
									content += "</td>";
									content += "<td>";
									content = content + comparesInfo[i].preRevisionId+" vs "+comparesInfo[i].revisionId;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].chloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].cmchloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].bchloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].echloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].ecloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].edcloc;
									content += "</td>";
									content += "<td>";
									content +=  rate + "%";
									content += "</td>";
									content += "<td>";
									content +=  eRate+ "%";
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].ccloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].eccloc;
									content += "</td>";

									content += "<td>";
									content += comparesInfo[i].changeFileNum;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].cloneChangeFileNum;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].cloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].cmcloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].bcloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].dcloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].cmdcloc;
									content += "</td>";
									content += "<td>";
									content += comparesInfo[i].bdcloc;
									content += "</td>";
									content += "</tr>";
									$("#printProjectsTable").append(content);
								}
							} else {
								alert("failed");
							}
						}
					});
				}
			} else {
				alert("failed");
			}
		}
	});
}
function loadPrintProjects(){
	len = projectsInfo.length;
	for (var i = 0; i < len; i++) {
		loadOnePrintProject(i);
	}
}
function deleteOneProject(element){
	var index = $("#printProjectsTable tr").index($(element).closest("tr"));
	$("#printProjectsTable tr:eq(" + index + ")").remove();
}
function printTable(){
	var ignore = [0];
	$("#printProjectsTable thead tr").find($("th")).each(function(index){
		if(!$(this).find("input[type='checkbox']").is(':checked')){
			ignore.push(index);
		}
	});
	$('#printProjectsTable').tableExport({type:'excel',escape:'true',ignoreColumn:ignore,fileName:'项目概览'});
}
function getPrintProjects(){
	$("#printProjectsTable tbody").html('');
	loadPrintProjects();
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