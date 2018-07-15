$(document).ready(function() {
	getSubsystem();
});
var projectsInfo;
function getSubsystem() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/getProcessBar.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				projectsInfo = data.list;
				$("#processTable tbody").html("");
				if(projectsInfo!=null&&projectsInfo.length>0){
					loadProjects();
					var table = $('.datatable').DataTable();
						table.columns().every( function () {
							var that = this;
							$( 'input', this.footer() ).on( 'keyup change', function () {
								if ( that.search() !== this.value ) {
									that.search( this.value ).draw();
								}
							} );
						} );
				}else{
					alert("没有数据");
				}
				startSend();
			} else {
				alert("failed");
			}
		}
	});
}
function loadOneProject(i) {
	var project = projectsInfo[i];
	var content = "<tr id=\"tr"+project.commitId+"\">";
	content += "<td>";
	content += projectsInfo[i].projectname;
	content += "</td>";
	content += "<td>";
	content += projectsInfo[i].projectnameen;
	content += "</td>";
	content += "<td>";
	content += projectsInfo[i].versionId;
	content += "</td>";
	content += "<td>";
	content += deleteT(projectsInfo[i].scanDate);
	content += "</td>";
	content += "<td>";
	if(project.percent < 0){
		content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+project.commitId+"\" class=\"progress-bar progress-bar-danger\" role=\"progressbar\" style=\"width: 100%\">"+project.stage+"</div></div>";
	}else if(project.percent == 0){
		content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+project.commitId+"\" class=\"progress-bar progress-bar-warning\" role=\"progressbar\" style=\"width: 100%\">"+project.stage+"</div></div>";
	}else{
		content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+project.commitId+"\" class=\"progress-bar progress-bar-striped active\" role=\"progressbar\" style=\"width: "+project.percent+"%\">"+project.stage+"</div></div>";
	}
	content += "</td>";
	content += "</tr>";
	$("#processTable").append(content);
}
function loadProjects(){
	len = projectsInfo.length;
	for (var i = 0; i < len; i++) {
		loadOneProject(i);
	}
}

var timer;
//进度条
function changeBar(index, words, percent){
	$("#progressBar"+index).html(words);
	if(percent<0){
		$("#progressBar"+index).attr("class","progress-bar progress-bar-danger");
		$("#progressBar"+index).attr('style', 'width: 100%;');
	}else{
		$("#progressBar"+index).attr('style', 'width:'+percent+'%;');
	}
	
	if(percent >= 100){
		$("#processTable tbody").find("#tr"+index).each(function(i){
			$("#processTable tbody tr:eq(" + i + ")").remove();
		});
	}
}

function sendProgressMessage(){
	$.ajax({
		type : "post",
		url : "../Zhonghui/getProcessBar.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				console.log("send message");
				projectsInfo = data.list;
				len = projectsInfo.length;
				for (var i = 0; i < len; i++) {
					if(inTable(projectsInfo[i].commitId)){
						changeBar(projectsInfo[i].commitId, projectsInfo[i].stage, projectsInfo[i].percent);
					}else{
						addNewProject(projectsInfo[i].commitId, projectsInfo[i].projectname, projectsInfo[i].versionId, projectsInfo[i].percent, projectsInfo[i].stage, deleteT(projectsInfo[i].scanDate));
					}
				}
			} else {
				alert("failed");
			}
		}
	});
	
	// progress++;
	// var stop = false;
	// if(progress == 101)
	// 	stop = true;
	// if(stop){
	// 	window.clearInterval(timer); 
	// }
}
function startSend(){
	timer = window.setInterval("getSubsystem()",60000); 
}
function addNewProject(index,name,version,width,stage,date) {
	var content = "<tr id=\"tr"+index+"\">";
	content += "<td>";
	content += name;
	content += "</td>";
	content += "<td>";
	content += version;
	content += "</td>";
	content += "<td>";
	content += date;
	content += "</td>";
	content += "<td>";
	if(width<0){
		content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+index+"\" class=\"progress-bar progress-bar-danger\" role=\"progressbar\" style=\"width: 100%\">"+stage+"</div></div>";
	}else{
		content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+index+"\" class=\"progress-bar progress-bar-striped active\" role=\"progressbar\" style=\"width: "+width+"%\">"+stage+"</div></div>";
	}
	// content += "<div class=\"progress\" style=\"margin-bottom: 0;\"><div id=\"progressBar"+index+"\" class=\"progress-bar progress-bar-striped active\" role=\"progressbar\" style=\"width: "+width+"%\">"+stage+"</div></div>";
	content += "</td>";
	content += "</tr>";
	$("#processTable").append(content);
}
function inTable(i){
	if($("#processTable tbody").find("#tr"+i).length==0)
		return false;
	return true;
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}