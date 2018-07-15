$(document).ready(function() {
	$(".form_datetime").datetimepicker({format: 'yyyy-mm-dd', minView: "month",autoclose : 1,todayHighlight : 1});
	setDate();
	getRestoreFile();
});
function getRestoreFile(){
	$.ajax({
		type : "post",
		url : "../Zhonghui/readBackupList.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var fileList = data.backupList;
				for(var i in fileList){
					$("#restoreFile").append("<option value =\""+fileList[i]+"\">"+fileList[i]+"</option>");
				}
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
function backup(){
	var date1 = new Date($('#date1').val());
	var date2 = new Date($('#date2').val());
	var clean = $("input[name='inlineRadioOptions']:checked").val();
	var path = $("#data_address").val(); 

	// if(isNull($('#date1').val())||isNull($('#date2').val())||(date2-date1 < 0)){
	// 	alert("请正确填写起止时间");
	// 	return;
	// }else{
	// 	backupDatabase(date1, date2, clean);
	// }
	backupDatabase(date1, date2, clean,path);
}
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}
function backupDatabase(d1, d2, c, p){
	console.log(d1);
	console.log(d2);
	console.log(c);
	console.log(p);
	$.ajax({
		type : "post",
		url : "../Zhonghui/backupDatabase.action",
		dataType : "json",
		data : {
			path: p
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				alert("success");
			} else {
				alert("failed");
			}
		}
	});
}
function restore(){
	var path = $("#restoreFile").children('option:selected').val();
	$.ajax({
		type : "post",
		url : "../Zhonghui/restoreDatabase.action",
		dataType : "json",
		data : {
			fileName: path
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				alert("success");
			} else {
				alert("failed");
			}
		}
	});
	console.log(path);
}