$(document).ready(function() {
	project_id = GetRequest();
	setProjectTeam();
});
var teamVar;
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
				teamVar = data.teams;
				for(var i in teamVar){
					$("#projectTeam").append("<option value =\""+teamVar[i].teamId+"\">"+teamVar[i].teamName+"</option>");
				}
				getProject();
			} else {
			}
		}
	});
}
var project_id;
var thisProject;
var isSvn;
var repoInfo;// 是个数组，只有一个元素
function getProject() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/getOneProject.action",
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
				thisProject = data.project;
				loadProject();
			} else {
				alert("failed");
			}
		}
	});

}
function loadProject() {
	if(isNull(thisProject.stream)){
		isSvn = true;
		$(".to_cc").css("display","none");
		$(".userP").css("display","table-row");
		$(".to_svn").css("display","table-row");
		var projectIdList = [];
		projectIdList.push(thisProject.projectId);
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
					$('#newProjectAddress').val(repoInfo[0].url);
					$('#username').val(repoInfo[0].username);
					$('#password').val(repoInfo[0].password);
				} else {
					alert("failed");
				}
			}
		});
	}else{
		isSvn = false;
		$(".to_cc").css("display","table-row");
		$(".userP").css("display","none");
		$(".to_svn").css("display","none");
		$('#component').val(thisProject.component);
		$('#pvob').val(thisProject.pvob);
		$('#stream').val(thisProject.stream);
	}
	$("#projectName").text(thisProject.projectName);
	$('#developCompany').val(thisProject.developCompany);
	$('#projectNameEn').val(thisProject.projectNameEn);
	$('#projectNameCh').val(thisProject.projectNameCh);
	$("#projectTeam").find($("option")).each(function(index, el) {
		if($(this).text()==thisProject.projectTeam)
			$(this).attr("selected", true);
	});
}
function updateProject(){
	var project_team = teamVar[$('#projectTeam').val()-1].teamName;
	var develop_company = $('#developCompany').val();
	var project_name_en = $('#projectNameEn').val();
	var project_name_ch = $('#projectNameCh').val();

	var address = $('#newProjectAddress').val();
	var user_name = $('#username').val();
	var pass_word = $('#password').val();

	var stream = $('#stream').val();
	var pvob = $('#pvob').val();
	var component = $('#component').val();;

	if(isNull(develop_company)||isNull(project_name_ch)||isNull(project_name_en)){
		alert("请补全项目信息");
		return;
	}
	if(isSvn){
		if(isNull(address)||isNull(user_name)||isNull(pass_word)){
			alert("请补全SVN信息");
			return;
		}
	}else{
		if(isNull(stream)||isNull(pvob)||isNull(component)){
			alert("请补全CC信息");
			return;
		}
	}
	$.ajax({
		type : "post",
		url : "../Zhonghui/updateProject.action",
		dataType : "json",
		data : {
			projectId : project_id,
			projectTeam : project_team,
			developCompany : develop_company,
			projectNameCh : project_name_ch,
			projectNameEn : project_name_en,

			address: address,
			username: user_name,
			password: pass_word,

			stream: stream,
			pvob: pvob,
			component: component
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				alert("finished");
			} else {
				alert("failed");
			}
		}
	});
}
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}