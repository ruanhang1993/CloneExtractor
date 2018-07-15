$(document).ready(function() {
	$('#compareHistory').attr('href',window.localStorage.getItem('comparehis'));
	$('#oneCompare').attr('href',window.localStorage.getItem('comparedetail'));
	var temp = GetRequestTwo();
	compare_id = temp['first'];
	projectName = decodeURI(temp['second']);
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractOneCompare.action",
		dataType : "json",
		data : {
			compareId : compare_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				compare = data.compare;
				$('#oneCompare').html(compare.preRevisionId+" & "+compare.revisionId);
				$('#projectName').text(projectName+' '+compare.preRevisionId+" & "+compare.revisionId);
			} else {
				alert("failed");
			}
		}
	});
	$('#projectName').text(projectName);
	getChangeFiles();
	// // rh
	// addLanguage("codeOne");
	// SyntaxHighlighter.all();
});
var compare_id;
var changeFiles;
var projectName;
var compare;
var relativePath;
function getChangeFiles() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractChangeFiles.action",
		dataType : "json",
		data : {
			compareId : compare_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				changeFiles = data.files;
				if(changeFiles.length > 0){
					$.ajax({
						type : "post",
						url : "../Zhonghui/extractFiles.action",
						dataType : "json",
						data : {
							commitId : changeFiles[0].commitId
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("XMLHttpRequest:" + XMLHttpRequest.status);
							console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
						},
						success : function(data) {
							if (data.successful == "true" || data.successful == true) {
								relativePath = data.relativePath;
								loadChangeFiles();
								$('.datatable').dataTable();
							}else{

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
function loadOneChangeFile(i) {
	var changeFile = changeFiles[i];
	var iden = 'changefile' + i;
	var content = "<tr id = " + iden + " onclick=\"changeFile(this)\">";
	content += "<td>";
	content += getFileName(changeFile.fileName);
	content += "</td>";
	content += "<td>";
	content += getRelativePath(changeFile.fileName);
	content += "</td>";
	content += "<td>";
	content += changeFile.chloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.cmchloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.bchloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.echloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.cloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.cmcloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.bcloc;
	content += "</td>";
	content += "<td>";
	content += changeFile.ecloc;
	content += "</td>";

	content += "<td>";
	content += '0';
	content += "</td>";

	content += "<td>";
	content += 'changeFile.xxx';
	content += "</td>";

	content += "</tr>";
	$("#changefiledetail").append(content);
}
function byte2string(byteArr) {
	var re = "";
	for ( var i in byteArr) {
		re += String.fromCharCode(byteArr[i]);
	}
	return re;
}
function loadChangeFiles() {
	len = changeFiles.length;
	for (i = 0; i < len; i++) {
		loadOneChangeFile(i);
	}
}

function changeFile(element){
	// var elementId = $(element).attr('id');
	// var index = elementId.substring(10);
	// var sa = byte2string(changeFiles[parseInt(index)].content);	
	// $("#codeOneText").text(changeFiles[parseInt(index)].fileName);
	// $("#codeOneBlock").html("<pre id=\"codeOne\" class=\"brush:java; highlight:[0]; cloned:[0];\">"+sa+"</pre>");
	// addLanguage("codeOne");
	// highLight("codeOne", 1, 1);
	// highLight("codeOne", 10, 18);
	// highLight("codeOne", 10, 19);
	// clone("codeOne", 20, 20);
	// clone("codeOne", 21, 22);
	// $("#codeTwoText").text(changeFiles[parseInt(index)].fileName);
	// $("#codeTwoBlock").html("<pre id=\"codeTwo\" class=\"brush:java; highlight:[0]; cloned:[0];\">"+sa+"</pre>");
	// addLanguage("codeTwo");
	// highLight("codeTwo", 1, 1);
	// highLight("codeTwo", 10, 18);
	// highLight("codeTwo", 10, 19);
	// clone("codeTwo", 20, 20);
	// clone("codeTwo", 21, 22);
	// SyntaxHighlighter.highlight();
}
function getFileName(s){
	var i = s.lastIndexOf("\\");
	if(i != -1){
		return s.slice(i+1);
	}
}
function getRelativePath(s){
	var i = s.indexOf(relativePath);
	if(i != -1){
		return s.slice(i+relativePath.length);
	}
}