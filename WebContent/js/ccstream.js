$(document).ready(function() {
	getSubsystem();
});
var streamsInfo;
function getSubsystem() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/getCCConfig.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				streamsInfo = data.list;
				loadStreams();
			} else {
				alert("failed");
			}
		}
	});
}

function loadStreams() {
	len = streamsInfo.length;
	for (var i = 0; i < len; i++) {
		loadOneStream(i);
	}
}
function loadOneStream(i){
	var stream = streamsInfo[i];
	var content = "<tr id = " + streamsInfo[i].stream + " class=\"oldStream\">";
	content += "<td>";
	content += "<input type='checkbox' data-id=\""+streamsInfo[i].stream+"\"/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' value = '"+ streamsInfo[i].stream + "' class=\"stream disabled\" readonly/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' value = '"+ streamsInfo[i].blstream + "' class='blstream'/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' value = '"+ streamsInfo[i].view + "' class='view'/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' value = '"+ streamsInfo[i].viewLocalPath + "' class='viewlocalpath'/>";
	content += "</td>";
	content += "</tr>";
	$("#streamTable").append(content);
}
function addOneStream(){
	var content = "<tr class=\"newStream\">";
	content += "<td><button type=\"button\" class=\"close\" onclick=\"deleteOneAdd(this)\"><span>&times;</span></button></td>";
	content += "<td>";
	content += "<input type='text' class='stream'/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' class='blstream'/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' class='view'/>";
	content += "</td>";
	content += "<td>";
	content += "<input type='text' class='viewlocalpath'/>";
	content += "</td>";
	content += "</tr>";
	$("#streamTable").append(content);
}
function storeAddAll(){
	var addStream = [];
	var cando = true;
	$("#streamTable tbody").find(".newStream").each(function(index){
		var stream = $(this).find(".stream").val();
		var blstream = $(this).find(".blstream").val();
		var view = $(this).find(".view").val();
		var viewlocalpath = $(this).find(".viewlocalpath").val();
		if(isNull(stream)||isNull(blstream)||isNull(view)||isNull(viewlocalpath)){
			cando = cando&&false;
		}else{
			addStream.push({"stream":stream,"blstream":blstream,"view":view,"viewLocalPath":viewlocalpath});
		}
	});
	if(cando){
		$.ajax({
			type : "post",
			url : "../Zhonghui/addCCConfig.action",
			dataType : "json",
			data : {'streamList':JSON.stringify(addStream)},
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
	}else{
		alert("请填补空缺的字段");
	}
	console.log(addStream);
	refresh();
}
function storeEditAll(){
	var editStream = [];
	var cando = true;
	$("#streamTable tbody").find(".oldStream").each(function(index){
		var stream = $(this).find(".stream").val();
		var blstream = $(this).find(".blstream").val();
		var view = $(this).find(".view").val();
		var viewlocalpath = $(this).find(".viewlocalpath").val();
		if(isNull(stream)||isNull(blstream)||isNull(view)||isNull(viewlocalpath)){
			cando = cando&&false;
		}else{
			editStream.push({"stream":stream,"blstream":blstream,"view":view,"viewLocalPath":viewlocalpath});
		}
	});
	if(cando){
		$.ajax({
			type : "post",
			url : "../Zhonghui/updateCCConfig.action",
			dataType : "json",
			data : {'streamList':JSON.stringify(editStream)},
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
	}else{
		alert("请填补空缺的字段");
	}
	console.log(editStream);
	refresh();
}
function deleteSelected(){
	var deleteStream = [];
	$("#streamTable tbody tr").each(function(index){
		if($(this).find("input[type='checkbox']").is(':checked')){
			deleteStream.push($(this).find("input[type='checkbox']").attr("data-id"));
		}
	});
	$.ajax({
		type : "post",
		url : "../Zhonghui/delCCConfig.action",
		dataType : "json",
		data : $.param({'streamList':deleteStream},true),
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
	console.log(deleteStream);
	refresh();
}

function refresh(){
	$("#streamTable tbody").html("");
	getSubsystem();
}
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}
function deleteOneAdd(element){
	var index = $("#streamTable tr").index($(element).closest("tr"));
	$("#streamTable tr:eq(" + index + ")").remove();
}