var projectsList = [];
var success;
var tree = [];
var teamVar;
$(document).ready(function() {
	$('#tagFile').val("tag");
	$('#portNumber').val("80");
	// getSubsystem();
	var myDate = new Date();
	var committime = myDate.toLocaleString();
	committime = myDate.getFullYear() + '-' + addZero(myDate.getMonth() + 1) + '-'
			+ addZero(myDate.getDate()) + ' ' + addZero(myDate.getHours()) + ':'
			+ addZero(myDate.getMinutes()) + ':' + addZero(myDate.getSeconds());
	$('#dtp_input1').val(committime);
	$('#showDate').val(committime);
	$(".to_cc").css("display","none");
	setProjectTeam();
	$('#versionControl').change(function(){
		var value = $(this).children('option:selected').val();
		if(value == "svn"){
			$("#addProjectPartBtn").attr('class','btn btn-primary');
			$("#addProjectPartBtn").attr('onclick','getProjectFile()');
			$(".to_cc").css("display","none");
			$(".userP").css("display","table-row");
			$(".to_svn").css("display","table-row");
		}else if(value == "cc"){
			$("#addProjectPartBtn").attr('class','btn btn-primary disabled');
			$("#addProjectPartBtn").attr('onclick','');
			$(".to_cc").css("display","table-row");
			$(".userP").css("display","none");
			$(".to_svn").css("display","none");
		}else{
			$("#addProjectPartBtn").attr('class','btn btn-primary disabled');
			$("#addProjectPartBtn").attr('onclick','');
			$(".to_cc").css("display","none");
			$(".userP").css("display","table-row");
			$(".to_svn").css("display","table-row");
		}
	}); 
	// startSend();
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
				teamVar = data.teams;
				for(var i in teamVar){
					$("#projectTeam").append("<option value =\""+teamVar[i].teamId+"\">"+teamVar[i].teamName+"</option>");
				}
			} else {
			}
		}
	});
}
function getProjectFile(){
	var setting = {
		view: {
			selectedMulti: false
		},
		check: {
			enable: true,
			chkboxType : {"Y": "ps", "N": "ps"},
		},
		data: {
			simpleData: {
				enable: true
			}
		}
	};

	var address = $('#newProjectAddress').val();
	var port = $('#portNumber').val();
	var tag_file = $('#tagFile').val();
	var user_name = $('#username').val();
	var pass_word = $('#password').val();
	
	if (isNull(address) || isNull(user_name) || isNull(pass_word)) {
		alert("请完成相关信息的填写");
		return false;
	}
	if(!isStandard(address)){
		alert("项目地址中请不要出现'/空白/'或'\\空白\\'的形式");
		return false;
	}else{
		if(address.indexOf("/",address.length-1)!=-1 || address.indexOf("\\",address.length-1)!=-1)
			address = address.slice(0, address.length-1);
	}
	//$('#coverarea').css('display','block');
	$.ajax({
		type : "post",
		url : "../Zhonghui/getFileTree.action",
		dataType : "json",
		data : {
			url : address+"/"+tag_file,
			port : port,
			username : user_name,
			password : pass_word,
			tag : tag_file
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				$('#coverarea').css('display','none');
				tree = data.treeNode;
				$.fn.zTree.init($("#fileTree"), setting, tree);
				$('#ignoreFile').modal('show');
			} else {
				alert("连接出错！请检查地址和用户名密码.");
				$('#coverarea').css('display','none');
			}
		}
	});
	return true;
}
function importProject(){
	$('#ignoreFile').modal('hide');
	var treeObj = $.fn.zTree.getZTreeObj("fileTree");
	var trueNodes = treeObj.getCheckedNodes(true);
	console.log(trueNodes);
	// setting.check.checkType = "checkbox"
	// treeNode.check_Child_State	勾选状态说明
	// -1	不存在子节点 或 子节点全部设置为 nocheck = true/checkout
	// 0	无 子节点被勾选/不存在
	// 1	部分 子节点被勾选/empty
	// 2	全部 子节点被勾选/checkout
	var emptyPath = [];
	var checkoutPath = [];
	for(var i in trueNodes){
		if(trueNodes[i].check_Child_State==2){
			checkoutPath.push(trueNodes[i].relativePath);
			var children = trueNodes[i].children;
			for(var j in children){
				deleteChildren(children[j],trueNodes);
			}
		}else if(trueNodes[i].check_Child_State==1){
			emptyPath.push(trueNodes[i].relativePath);
		}else{//-1
			checkoutPath.push(trueNodes[i].relativePath);
		}
	}

	var res = addOneProject(emptyPath, checkoutPath);
	if(res)
		alert("added repository successfully");
}
function deleteChildren(parentRoot,tree){
	var index = tree.indexOf(parentRoot);
	if(index != -1){
		tree.splice(index,1);
		if(parentRoot.children.length>0){
			for(var i in parentRoot.children){
				deleteChildren(parentRoot.children[i],tree);
			}
		}
	}
}
function addZero(val){
	if(val<10)
		return '0'+val;
	return val;
}

function addAllProjectFile(){
	var address = $('#newProjectAddress').val();
	var port = $('#portNumber').val();
	var tag_file = $('#tagFile').val();
	var project_name_ch = $('#projectNameCh').val();
	var project_name_en = $('#projectNameEn').val();
	var project_team = teamVar[$('#projectTeam').val()-1].teamName;
	var develop_company = $('#developCompany').val();
	var user_name = $('#username').val();
	var pass_word = $('#password').val();
	var processtime = $('#dtp_input1').val();
	var version = $('#oneVersion').val();

	var steam = $('#steam').val();
	var pvod = $('#pvod').val();
	var component = $('#component').val();
	var view ="";
	var viewLocalPath ="";
	var versionControl = $("#versionControl").children('option:selected').val();

	if(versionControl == "cc"){
		if (isNull(steam) || isNull(pvod) || isNull(component) || isNull(processtime)) {
			alert("请完成相关信息的填写");
			return false;
		}
	}else{
		if (isNull(address) || isNull(user_name) || isNull(pass_word) || isNull(processtime)) {
			alert("请完成相关信息的填写");
			return false;
		}
		if(!isStandard(address)){
			alert("项目地址中请不要出现'/空白/'或'\\空白\\'的形式");
			return false;
		}else{
			if(address.indexOf("/",address.length-1)!=-1 || address.indexOf("\\",address.length-1)!=-1)
				address = address.slice(0, address.length-1);
		}
	}
	if (isNull(project_name_ch) || isNull(project_name_en)) {
		alert("请完成项目中英文名称的填写");
		return false;
	}
	
	//$('#coverarea').css('display','block');
	var myDate = new Date();
	var committime = myDate.toLocaleString();
	committime = myDate.getFullYear() + '-' + addZero(myDate.getMonth() + 1) + '-'
			+ addZero(myDate.getDate()) + ' ' + addZero(myDate.getHours()) + ':'
			+ addZero(myDate.getMinutes()) + ':' + addZero(myDate.getSeconds());
	if(versionControl == "cc"){
		window.location.href = "projects.html";
	}
	$.ajax({
		type : "post",
		url : "../Zhonghui/projectConfigure.action",
		dataType : "json",
		data : $.param({
			url : address+"/"+tag_file,
			port : port,
			username : user_name,
			password : pass_word,
			processDate : processtime,
			commitDate : committime,
			projectTeam : project_team,
			projectNameCh : project_name_ch,
			projectNameEn : project_name_en,
			developCompany : develop_company,
			tag : tag_file,
			steam : steam,
			pvod : pvod,
			component : component,
			view : view,
			viewLocalPath : viewLocalPath,
			versionControl : versionControl,
			empty : [],
			tocheckout : [],
			version : version 
		},true),
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
			// alert("XMLHttpRequest:" + XMLHttpRequest.status);
			// alert("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				$('#coverarea').css('display','none');
				window.location.href = "projects.html";
			} else {
				alert("The username and password doesnt match, please change");
				$('#coverarea').css('display','none');
			}
		}
	});
	return true;
}
function addOneProject(empty, tocheckout) {
	var address = $('#newProjectAddress').val();
	var port = $('#portNumber').val();
	var tag_file = $('#tagFile').val();
	var project_name_ch = $('#projectNameCh').val();
	var project_name_en = $('#projectNameEn').val();
	var project_team = teamVar[$('#projectTeam').val()-1].teamName;
	var develop_company = $('#developCompany').val();
	var user_name = $('#username').val();
	var pass_word = $('#password').val();
	var processtime = $('#dtp_input1').val();
	var versionControl = $("#versionControl").children('option:selected').val();
	
	if (isNull(address) || isNull(user_name) || isNull(pass_word) || isNull(processtime)) {
		alert("请完成相关信息的填写");
		return false;
	}
	if (isNull(project_name_ch) || isNull(project_name_en)) {
		alert("请完成项目中英文名称的填写");
		return false;
	}
	if(!isStandard(address)){
		alert("项目地址中请不要出现'/空白/'或'\\空白\\'的形式");
		return false;
	}else{
		if(address.indexOf("/",address.length-1)!=-1 || address.indexOf("\\",address.length-1)!=-1)
			address = address.slice(0, address.length-1);
	}
	console.log(address+"/"+tag_file);
	var myDate = new Date();
	var committime = myDate.toLocaleString();
	committime = myDate.getFullYear() + '-' + addZero(myDate.getMonth() + 1) + '-'
			+ addZero(myDate.getDate()) + ' ' + addZero(myDate.getHours()) + ':'
			+ addZero(myDate.getMinutes()) + ':' + addZero(myDate.getSeconds());

	//$('#coverarea').css('display','block');
	$.ajax({
		type : "post",
		url : "../Zhonghui/projectConfigure.action",
		dataType : "json",
		data : $.param({
			url : address+"/"+tag_file,
			port : port,
			username : user_name,
			password : pass_word,
			processDate : processtime,
			commitDate : committime,
			projectTeam : project_team,
			projectNameCh : project_name_ch,
			projectNameEn : project_name_en,
			developCompany : develop_company,
			tag : tag_file,
			steam : "",
			pvod : "",
			component : "",
			view : "",
			viewLocalPath : "",
			versionControl : versionControl,
			empty : empty,
			tocheckout : tocheckout,
			version : version
		},true),
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			return false;
			// alert("XMLHttpRequest:" + XMLHttpRequest.status);
			// alert("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				$('#coverarea').css('display','none');
			} else {
				$('#coverarea').css('display','none');
				alert("The username and password doesnt match, please change");
			}
		}
	});
	return true;
}

function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}

function isStandard(words){
	var temp = words;
	if(words.indexOf("http://")==0 || words.indexOf("http:\\\\")==0)
		temp = words.substr(7);
	if(words.indexOf("https://")==0 || words.indexOf("https:\\\\")==0)
		temp = words.substr(8);
	if(words.indexOf("svn://")==0 || words.indexOf("svn:\\\\")==0)
		temp = words.substr(6);
	temp = Trim(temp, "g");
	var index1 = temp.indexOf("\\\\");
	var index2 = temp.indexOf("//");
	if(index1 == -1 && index2 == -1)
		return true;
	else
		return false;
}
function Trim(str,is_global)
{
	var result; 
	result = str.replace(/(^\s+)|(\s+$)/g,"");
	if(is_global.toLowerCase()=="g")
	    result = result.replace(/\s/g,"");
	return result;
}