$(document).ready(function() {
	setProjectTeam();
});
function setProjectTeam(){
	$.ajax({
		type : "post",
		url : "../Zhonghui/getTeams.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var teamVar = data.teams;
				for(var i in teamVar){
					$("#projectTeamTable").append("<tr><td><button type=\"button\" class=\"close\" onclick=\"deleteOneTeam(this)\" data-id=\""+teamVar[i].teamId+"\"><span>&times;</span></button></td><td>"+teamVar[i].teamName+"</td></tr>");
				}
			} else {
			}
		}
	});
}
function deleteOneTeam(e){
	var index = $(e).attr("data-id");
	$.ajax({
		type : "post",
		url : "../Zhonghui/deleteTeam.action",
		dataType : "json",
		data : {
			teamId : index
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
			} else {
			}
		}
	});
	$("#projectTeamTable tbody").html("");
	setProjectTeam();
}
function addOneTeam(){
	if(isNull($("#projectTeam").val())){
		alert("请填写项目组名称");
		return;
	}
	$.ajax({
		type : "post",
		url : "../Zhonghui/addTeam.action",
		dataType : "json",
		data : {
			teamName : $("#projectTeam").val()
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				$("#projectTeam").val("");
				alert("添加完成");
			} else {
				alert("添加失败");
			}
		}
	});
	$("#projectTeamTable tbody").html("");
	setProjectTeam();
}
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}