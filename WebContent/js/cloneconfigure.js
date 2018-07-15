$(document).ready(function() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extactCloneConfigure.action",
		dataType : "json",
		data : {},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				$('#min_token').val(data.list[1]);
				$('#min_cloc').val(data.list[0]);
				$('#min_class').val(data.list[2]);
				$('#encoding').val(data.list[3]); 
			} else {
				alert("failed");
			}
		}
	});
});
function saveSetting() {
	var minToken = $('#min_token').val();
	var minCloc = $('#min_cloc').val();
	var minClass = $('#min_class').val();
	var enCoding = $('#encoding').val(); 
	
	$.ajax({
		type : "post",
		url : "../Zhonghui/cloneConfigure.action",
		dataType : "json",
		data : {
			mintoken : minToken,
			mincloc  : minCloc,
			minclass : minClass,
			encoding : enCoding
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			alert("success");
		}
	});
}