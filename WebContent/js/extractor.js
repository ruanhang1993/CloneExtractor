$(document).ready(function() {
	getSubsystem();
});
var projectsInfo;
var repoInfo;
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
				loadRepo();
			} else {
				alert("failed");
			}
		}
	});

}

function freshOneProject(element) {
	var projectId = $(element).attr('data-id');
	$.ajax({
		type : "post",
		url : "../Zhonghui/freshProject.action",
		dataType : "json",
		data : {
			projectId : projectId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("XMLHttpRequest:" + XMLHttpRequest.status);
			// alert("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
			} else {
				alert("failed");
			}
		}
	});
}

function loadOneProject(i) {
	var project = projectsInfo[i];
	var repo = repoInfo[i];
	var iden = 'project' + i;
	var content = "<tr id = " + iden + ">";
	content += "<td><button type=\"button\" class=\"close\" onclick=\"deleteOneProject(this)\" data-id=\""+project.projectId+"\"><span>&times;</span></button></td>";
	content += "<td>";
	content += project.projectTeam;
	content += "</td>";
	content += "<td>";
	content += project.projectNameCh;
	content += "</td>";
	content += "<td>";
	content += deleteT(repo.processDate);
	content += "</td>";
	content += "<td><a href='versionhis.html?projectId="
			+ project.projectId
			+ "'><span class=\"glyphicon glyphicon-share-alt\"></span></a></td><td><a href='comparehis.html?projectId="
			+ project.projectId + "'><span class=\"glyphicon glyphicon-share-alt\"></span></a></td>";
	content += "<td><button style=\"padding:1px 10px;\" type=\"button table-button\" class=\"btn btn-default\" onclick=\"freshOneProject(this)\" data-id=\""+project.projectId+"\">扫描</button>"
	+ "<a style=\"padding:1px 10px;margin-left:3px;\" type=\"button table-button\" class=\"btn btn-default\" href=\"changeproject.html?projectId="+project.projectId+"\">修改</a>"
	+"<a style=\"padding:1px 10px;margin-left:3px;\" type=\"button table-button\" class=\"btn btn-default\" href=\"addversionnumber.html?projectId="+project.projectId+"\">添加项目编号</a></td>";
	content += "</tr>";
	$("#projectsTable").append(content);
}
function loadProjects(){
	len = projectsInfo.length;
	for (var i = 0; i < len; i++) {
		loadOneProject(i);
	}
}

function loadRepo() {
	len = projectsInfo.length;
	var projectIdList = [];
	for (var i = 0; i < len; i++) {
		projectIdList.push(projectsInfo[i].projectId);
	}
	if(projectIdList.length > 0)
		$.ajax({
				type : "post",
				url : "../Zhonghui/extractRepo.action",
				dataType : "json",
				data : $.param({'projects':projectIdList},true),
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("commit XMLHttpRequest:" + XMLHttpRequest.status+"/XMLHttpRequest:" + XMLHttpRequest.readyState);
				},
				success : function(data) {
					if (data.successful == "true" || data.successful == true){
						repoInfo = data.repos;
						loadProjects();
						var table = $('.datatable').DataTable();
						table.columns().every( function () {
							var that = this;

							$( 'input', this.footer() ).on( 'keyup change', function () {
								if ( that.search() !== this.value ) {
									that
										.search( this.value )
										.draw();
								}
							} );
						} );
					} else {
						alert("failed");
					}
				}
		});
}
function deleteOneProject(element) {
	var index = $("#projectsTable tr").index($(element).closest("tr"));
	var projectId = $(element).attr('data-id');
	$.ajax({
		type : "post",
		url : "../Zhonghui/deleteProject.action",
		dataType : "json",
		data : {
			projectId : projectId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("XMLHttpRequest:" + XMLHttpRequest.status);
			// alert("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
			} else {
				alert("failed");
			}
		}
	});
	$("#projectsTable tr:eq(" + index + ")").remove();
	
	var start = $("#projectsTable").dataTable().fnSettings()._iDisplayStart;  
    var total = $("#projectsTable").dataTable().fnSettings().fnRecordsDisplay();  
    window.location.reload();  
    if((total-start)==1){  
        if (start > 0) {  
            $("#projectsTable").dataTable().fnPageChange( 'previous', true );  
        }  
    }  
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}