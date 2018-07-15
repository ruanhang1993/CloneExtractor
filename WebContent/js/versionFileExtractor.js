$(document).ready(function() {
	$('#versionHistory').attr('href',window.localStorage.getItem('versionhis'));
	$('#oneVersion').attr('href',window.localStorage.getItem('versiondetail'));
	commit_id = GetRequestTwo()['first'];
	projectName = decodeURI(GetRequestTwo()['second']);
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractOneVersion.action",
		dataType : "json",
		data : {
			commitId : commit_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				commit = data.commit;
				$('#oneVersion').html(commit.revisionId);
			} else {
				alert("failed");
			}
		}
	});
	$('#projectName').text(projectName);
	getFiles();
	// addLanguage("codeOne");
	SyntaxHighlighter.all();
});
var commit_id;
var files;
var projectName;
var table;
var relativePath;

function getFiles() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractFiles.action",
		dataType : "json",
		data : {
			commitId : commit_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				files = data.files;
				relativePath = data.relativePath;
				if(files.length>0){
					$.ajax({
						type : "post",
						url : "../Zhonghui/extractCloneByFile.action",
						dataType : "json",
						data : {
							fileId : files[0].fileId
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("XMLHttpRequest:" + XMLHttpRequest.status);
							console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
						},
						success : function(data) {
							if (data.successful == "true" || data.successful == true) {
								var clones = data.clones;
								$("#filename").text(files[0].fileName);
								var tempLanguage = suffixToLanguage[files[0].fileType];
								var tempContent = byte2string(files[0].content);
								tempContent = tempContent.replaceAll("<","&lt");
								$("#codeOneBlock").html("<pre id=\"codeOne\" class=\"brush:"+tempLanguage+";\">"+tempContent+"</pre>");
								// addLanguage("codeOne");
								SyntaxHighlighter.highlight();
								if(clones.length>0){
									// {'index':[clone,clone]}
									var popoverInfo = {};
									for(var i in clones){
										var begin = clones[i].beginLine;
										var end = clones[i].endLine;
										for(begin; begin <= end; begin++){
											var numIndex = "" + begin;
											if(popoverInfo[numIndex] == null){
												popoverInfo[numIndex] = [clones[i]];
											}else{
												popoverInfo[numIndex].push(clones[i]);
											}
										}
									}
									highlightByObj(popoverInfo);
									// for(var i in clones){
									// 	highlightByColor(1, clones[i]);
									// }
								}
							} else {
								alert("failed");
							}
						}
					});
				}
				loadFiles();
				table = $('#versionfiledetial').DataTable();
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
function loadOneFile(i) {
	var file = files[i];
	var iden = 'file' + i;
	var content = "<tr id = " + iden+ " onclick=\"changeFile(this)\" data-fileId="+file.fileId+">";
	content += "<td>";
	content += getFileName(file.fileName);
	content += "</td>";
	content += "<td>";
	content += getRelativePath(file.fileName);
	content += "</td>";
	content += "<td>";
	content += file.loc;
	content += "</td>";
	content += "<td>";
	content += file.eloc;
	content += "</td>";
	content += "<td>";
	content += file.cmloc;
	content += "</td>";
	content += "<td>";
	content += file.bloc;
	content += "</td>";
	content += "<td>";
	content += file.cloc;
	content += "</td>";
	content += "<td>";
	content += file.ecloc;
	content += "</td>";
	content += "<td>";
	content += file.cmcloc;
	content += "</td>";
	content += "<td>";
	content += file.bcloc;
	content += "</td>";
	content += "<td>";
	content += file.cloneClassNum;
	content += "</td>";
	content += "<td>";
	content += file.cloneNum;
	content += "</td>";
	content += "<td><select id=\"select"+iden+"\">";
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractCloneByFile.action",
		dataType : "json",
		async: false,
		data : {
			fileId : file.fileId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var clones = data.clones;
				if(clones.length>0){
					for(var i in clones){
						content += "<option value =\""+clones[i].cloneId+"\">"+clones[i].beginLine+"~"+clones[i].endLine+"</option>";
					}
				}
			} else {
				alert("failed");
			}
		}
	});
	content += "</select></td>";
	content += "<td><button style=\"padding:1px 10px;\" type=\"button table-button\" class=\"btn btn-default\" onclick=\"wrongcheck(this)\" data-id=\"select"+iden+"\">误报</button></td>";
	content += "</tr>";
	$("#versionfiledetial").append(content);
}
function wrongcheck(element){
	var selectId = $(element).attr("data-id");
	var selectCloneId = $("#"+selectId).val();
	$.ajax({
		type : "post",
		url : "../Zhonghui/deleteClone.action",
		dataType : "json",
		async: false,
		data : {
			cloneId : selectCloneId
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
function byte2string(byteArr) {
	var re = "";
	for ( var i in byteArr) {
		re += String.fromCharCode(byteArr[i]);
	}
	return re;
}
function loadFiles() {
	len = files.length;
	for (i = 0; i < len; i++) {
		loadOneFile(i);
	}
}

String.prototype.replaceAll = function(oldstr,newstr){
　　return this.replace(new RegExp(oldstr,"gm"),newstr);
}
function changeFile(element) {
	var elementId = $(element).attr('id');
	var index = elementId.substring(4);
	var sa = byte2string(files[parseInt(index)].content);
	sa = sa.replaceAll("<","&lt");
	var file_id = $(element).attr('data-fileId');
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractCloneByFile.action",
		dataType : "json",
		data : {
			fileId : file_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var clones = data.clones;
				$("#filename").text(files[parseInt(index)].fileName);
				var tempLanguage = suffixToLanguage[files[parseInt(index)].fileType];
				$("#codeOneBlock").html("<pre id=\"codeOne\" class=\"brush:"+tempLanguage+";\">"+sa+"</pre>");
				// addLanguage("codeOne");
				SyntaxHighlighter.highlight();
				if(clones.length>0){
					// {'index':[clone,clone]}
					var popoverInfo = {};
					for(var i in clones){
						var begin = clones[i].beginLine;
						var end = clones[i].endLine;
						for(begin; begin <= end; begin++){
							var numIndex = "" + begin;
							if(popoverInfo[numIndex] == null){
								popoverInfo[numIndex] = [clones[i]];
							}else{
								popoverInfo[numIndex].push(clones[i]);
							}
						}
					}
					highlightByObj(popoverInfo);
					// for(var i in clones){
					// 	highlightByColor(1, clones[i]);
					// }
				}
			} else {
				alert("failed");
			}
		}
	});
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

//过滤
function isNull(words) {
	if (words == null)
		return true;
	if (words == '')
		return true;
	return false;
}