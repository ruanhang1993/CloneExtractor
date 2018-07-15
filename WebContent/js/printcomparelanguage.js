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
					for(var i in comparesInfo){
						$.ajax({
							type : "post",
							url : "../Zhonghui/getCompareLanguage.action",
							dataType : "json",
							data : {
								compareId : comparesInfo[i].compareId
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								console.log("XMLHttpRequest:" + XMLHttpRequest.status);
								console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
							},
							success : function(data) {
								if (data.successful == "true" || data.successful == true) {
									var compareLanguageList = data.list;
									for(var j in compareLanguageList){
										var rate = ((compareLanguageList[j].cloc+compareLanguageList[j].dcloc)/(compareLanguageList[j].aloc+compareLanguageList[j].dloc) * 100).toFixed(2);
										var eRate = ((compareLanguageList[j].ecloc+compareLanguageList[j].edcloc)/(compareLanguageList[j].edloc+compareLanguageList[j].ealoc) * 100).toFixed(2);
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
										content += compareLanguageList[j].language;
										content += "</td>";
										content += "<td>";
										content = content + compareLanguageList[j].preRevisionId+" vs "+compareLanguageList[j].revisionId;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].chloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].cmchloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].bchloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].echloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].ecloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].edcloc;
										content += "</td>";
										content += "<td>";
										content +=  rate + "%";
										content += "</td>";
										content += "<td>";
										content +=  eRate+ "%";
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].ccloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].eccloc;
										content += "</td>";

										content += "<td>";
										content += compareLanguageList[j].changeFileNum;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].cloneChangeFileNum;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].cloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].cmcloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].bcloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].dcloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].cmdcloc;
										content += "</td>";
										content += "<td>";
										content += compareLanguageList[j].bdcloc;
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