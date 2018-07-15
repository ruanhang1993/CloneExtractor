var language ={
"bash":"shBrushBash.js",
"coldfusion":"shBrushColdFusion.js",
"csharp":"shBrushCSharp.js",
"cpp":"shBrushCpp.js",
"css":"shBrushCss.js",
"delphi":"shBrushDelphi.js",
"diff":"shBrushDiff.js",
"erlang":"shBrushErlang.js",
"groovy":"shBrushGroovy.js",
"javascript":"shBrushJScript.js",
"java":"shBrushJava.js",
"javafx":"shBrushJavaFX.js",
"perl":"shBrushPerl.js",
"php":"shBrushPhp.js",
"text":"shBrushPlain.js",
"powershell":"shBrushPowerShell.js",
"python":"shBrushPython.js",
"ruby":"shBrushRuby.js",
"scala":"shBrushScala.js",
"sql":"shBrushSql.js",
"vb":"shBrushVb.js",
"xml":"shBrushXml.js"
};
var suffixToLanguage = {
	"java": "java",
	"c":"cpp",
	"cpp":"cpp",
	"cs":"csharp",
	"vb":"vb",
	"js":"javascript",
	"py":"python"
}

var highlightColor = ['rgba(227, 46, 46, 0.46)','rgba(221, 227, 46, 0.46)','rgba(46, 73, 227, 0.46)'];

var codeJs = "syntaxhighlighter_3.0.83/scripts/";

function loadJs(file) {
    var dynamicLoading = {
	    css: function(path){
			if(!path || path.length === 0){
				throw new Error('argument "path" is required !');
			}
			var head = document.getElementsByTagName('head')[0];
	        var link = document.createElement('link');
	        link.href = path;
	        link.rel = 'stylesheet';
	        link.type = 'text/css';
	        head.appendChild(link);
	    },
	    js: function(path){
			if(!path || path.length === 0){
				throw new Error('argument "path" is required !');
			}
			var head = document.getElementsByTagName('head')[0];
	        var script = document.createElement('script');
	        script.src = path;
	        script.type = 'text/javascript';
	        head.appendChild(script);
	    }
	};
	dynamicLoading.js(file);
}

function addLanguage(targetId){
	var start = $("#"+targetId).attr('class').indexOf('brush:');
	var end = $("#"+targetId).attr('class').indexOf(';');
	var l = $("#"+targetId).attr('class').substr(start+6, end-start-6);
	loadJs(codeJs + language[l]);
}

function highLight(targetId, lineA, lineB){
	var start = $("#"+targetId).attr('class').indexOf('highlight:[');
	var end = $("#"+targetId).attr('class').length;
	var latter = $("#"+targetId).attr('class').substr(start+11, end);
	var former = $("#"+targetId).attr('class').substr(0, start+11);

	if(lineA > lineB){
		var t = lineA;
		lineA = lineB;
		lineB = t;
	}
	var i = lineA;
	var newLine = '';
	while(i <= lineB){
		newLine = newLine + i + ',';
		i++;
	}
	$("#"+targetId).attr('class',former+newLine+latter);
}

function clone(targetId, lineA, lineB){
	var start = $("#"+targetId).attr('class').indexOf('cloned:[');
	var end = $("#"+targetId).attr('class').length;
	var latter = $("#"+targetId).attr('class').substr(start+8, end);
	var former = $("#"+targetId).attr('class').substr(0, start+8);

	if(lineA > lineB){
		var t = lineA;
		lineA = lineB;
		lineB = t;
	}
	var i = lineA;
	var newLine = '';
	while(i <= lineB){
		newLine = newLine + i + ',';
		i++;
	}
	$("#"+targetId).attr('class',former+newLine+latter);
}

function clone(targetId, lineA, lineB){
	var start = $("#"+targetId).attr('class').indexOf('cloned:[');
	var end = $("#"+targetId).attr('class').length;
	var latter = $("#"+targetId).attr('class').substr(start+8, end);
	var former = $("#"+targetId).attr('class').substr(0, start+8);

	if(lineA > lineB){
		var t = lineA;
		lineA = lineB;
		lineB = t;
	}
	var i = lineA;
	var newLine = '';
	while(i <= lineB){
		newLine = newLine + i + ',';
		i++;
	}
	$("#"+targetId).attr('class',former+newLine+latter);
}

function highlightByColor(index, clone){
	var lineA = clone.beginLine;
	var lineB = clone.endLine;
	if(lineA > lineB){
		var t = lineA;
		lineA = lineB;
		lineB = t;
	}
	$.ajax({
		type : "post",
		url : "../Zhonghui/getClones.action",
		dataType : "json",
		data : {
			cloneId : clone.cloneId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				var clones = data.clones;
				var files = data.files;
				var versions = data.versions;
				var content ="<div class=\"clone_title_div\">相关克隆实例</div>";
				content += "<table class=\"table\"><thead><tr><th>版本号</th><th>文件名</th><th>起始行</th><th>结束行</th></tr></thead><tbody>";
				for(var iter in clones){
					content += "<tr>";
					content += "<td>";
					content += versions[iter];
					content += "</td>";
					content += "<td>";
					content += files[iter];
					content += "</td>";
					content += "<td>";
					content += clones[iter].beginLine;
					content += "</td>";
					content += "<td>";
					content += clones[iter].endLine;
					content += "</td>";
					content += "</tr>";
				}
				content += "</tbody></table>";
				var i = lineA;
				while(i <= lineB){
					$('.number'+i).css('background', highlightColor[index]);
					$('.number'+i).popover({
						content: content,
						title: '克隆类型: '+ clone.cloneType +"<button type=\"button\" class=\"close\" onclick=\"closePop("+i+")\"><span aria-hidden=\"true\">&times;</span></button>",
						placement: 'top',
						container: 'body',
						html: true,
						trigger: 'hover'
					});
					i++;
				}
			} else {}
		}
	});	
}
function closePop(i){
	$(".number"+i).popover('hide');
}
function highlightByObj(popover){
	for(var line in popover){
		var content ="";
		var colorIndex = 0;
		var tempClones = popover[line];
		for(var tempClone in tempClones){
			content += "<div>克隆类型: "+tempClones[tempClone].cloneType+"</div>";
			colorIndex = tempClones[tempClone].cloneType;
			console.log(tempClones[tempClone].cloneId+"/"+tempClones[tempClone].cloneType);
			$('.number'+line).css('background', highlightColor[colorIndex]);
			$.ajax({
				type : "post",
				url : "../Zhonghui/getClones.action",
				dataType : "json",
				async: false,
				data : {
					cloneId : tempClones[tempClone].cloneId
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("XMLHttpRequest:" + XMLHttpRequest.status);
					console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
				},
				success : function(data) {
					if (data.successful == "true" || data.successful == true) {
						var clones = data.clones;
						var files = data.files;
						var versions = data.versions;
						content += "<table class=\"table table-bordered\"><thead><tr><th>版本号</th><th>文件名</th><th>起始行</th><th>结束行</th></tr></thead><tbody>";
						for(var iter in clones){
							content += "<tr>";
							content += "<td>";
							content += versions[iter];
							content += "</td>";
							content += "<td>";
							content += files[iter];
							content += "</td>";
							content += "<td>";
							content += clones[iter].beginLine;
							content += "</td>";
							content += "<td>";
							content += clones[iter].endLine;
							content += "</td>";
							content += "</tr>";
						}
						content += "</tbody></table>";
					} else {}
				}
			});	
		}
		$('.number'+line).popover({
			content: content,
			title: '相关克隆'+"<button type=\"button\" class=\"close\" onclick=\"closePop("+line+")\"><span aria-hidden=\"true\">&times;</span></button>",
			placement: 'top',
			container: 'body',
			html: true,
			trigger: 'hover'
		});
	}
}
