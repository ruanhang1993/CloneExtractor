$(document).ready(function() {
	window.localStorage.setItem("versiondetail",window.location.href);
	commit_id = GetRequestTwo()['first'];
	projectName = decodeURI(GetRequestTwo()['second']);
	$('#projectName').text(projectName);
	$('#versionHistory').attr('href',window.localStorage.getItem('versionhis'));
	$("#java_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#all_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#cplusplus_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#csharp_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#js_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#vb_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	$("#others_filedetailbtn").click(function() {
		location.href = "../Zhonghui/versionfiledetail.html?commitId="+ commit_id+"&projectName="+encodeURI(projectName);
	});
	getCommit();
});
var commit;
var commitLs;
var commit_id;
var projectName;
function getCommit() {
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
				commitLs = data.commitLs;
				$('#oneVersion').html(commit.revisionId);
				loadOneCommit("java");
				loadOneCommit("cplusplus");
				loadOneCommit("csharp");
				loadOneCommit("others");
				loadOneCommit("js");
				loadOneCommit("vb");
				loadCommit('all');
				enableLanguage("all");
				showTab("all");
				showChart();
			} else {
				alert("failed");
			}
		}
	});
}
function loadCommit(language) {
	$("#"+language+"_revisionId").text(commit.revisionId);
	$("#"+language+"_commitDate").text(deleteT(commit.commitDate));
	$("#"+language+"_startDate").text(deleteT(commit.scanDate));
	$("#"+language+"_endDate").text(deleteT(commit.submitDoneDate));
	$("#"+language+"_loc").text(commit.loc);
	$("#"+language+"_eloc").text(commit.eloc);
	$("#"+language+"_note").text(commit.cmloc);
	$("#"+language+"_blank").text(commit.bloc);
	$("#"+language+"_cloc").text(commit.cloc);
	$("#"+language+"_ecloc").text(commit.ecloc);
	$("#"+language+"_note_cloc").text(commit.cmcloc);
	$("#"+language+"_blank_cloc").text(commit.bcloc);
	$("#"+language+"_fileNum").text(commit.fileNum);
	$("#"+language+"_ecloneRate").text((commit.ecloc / commit.eloc * 100).toFixed(2) + "%")
	$("#"+language+"_cloneFileNum").text(commit.cloneFileNum);
	$("#"+language+"_finalLine").text(commit.ccloc);
	$("#"+language+"_efinalLine").text(commit.eccloc);
}
// language---java,c,cplusplus
function loadOneCommit(language) {
	$('#'+language+'_li').css('display','none');
	var pgLanguage;
	if(language == 'cplusplus')
		pgLanguage = "c++";
	else if(language == 'csharp')
		pgLanguage = "c#";
	else 
		pgLanguage = language;
	var commitL;
	for (var i =0;i<commitLs.length;i++){
		if(commitLs[i].language == pgLanguage){
			commitL = commitLs[i];
			break;
		}
	}
	$("#"+language+"_revisionId").text(commit.revisionId);
	$("#"+language+"_commitDate").text(deleteT(commit.commitDate));
	$("#"+language+"_startDate").text(deleteT(commit.scanDate));
	$("#"+language+"_endDate").text(deleteT(commit.submitDoneDate));
	if(commitL != null){
		$("#"+language+"_loc").text(commitL.loc);
		$("#"+language+"_eloc").text(commitL.eloc);
		$("#"+language+"_note").text(commitL.cmloc);
		$("#"+language+"_blank").text(commitL.bloc);
		$("#"+language+"_cloc").text(commitL.cloc);
		$("#"+language+"_ecloc").text(commitL.ecloc);
		$("#"+language+"_note_cloc").text(commitL.cmcloc);
		$("#"+language+"_blank_cloc").text(commitL.bcloc);
		$("#"+language+"_fileNum").text(commitL.fileNum);
		$("#"+language+"_ecloneRate").text((commitL.ecloc / commitL.eloc * 100).toFixed(2) + "%")
		$("#"+language+"_cloneFileNum").text(commitL.cloneFileNum);
		$("#"+language+"_finalLine").text(commitL.ccloc);
		$("#"+language+"_efinalLine").text(commitL.eccloc);
	}
}
function enableLanguage(language){
	$('#'+language+'_li').css('display','block');
	$("#"+language+"_li").attr('class', "none");
	$("#"+language+"_a").attr('href', "#"+language);
}
function showTab(language){
	$("#"+language+"_li").attr('class', "active");
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}

// java
var javaCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var javaECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var javaCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};
// all
var allCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
           display:true,
           text:"代码行克隆比例"
       }
	}
};
var allECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
           display:true,
           text:"有效代码行克隆比例"
       }
	}
};
var allCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
           display:true,
           text:"文件克隆比例"
       }
	}
};
// cplusplus
var cplusplusCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var cplusplusECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var cplusplusCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};

// c#
var csharpCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var csharpECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var csharpCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};
// js
var jsCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var jsECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var jsCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};
// vb
var vbCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var vbECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var vbCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};
// others
var othersCloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆代码行", "非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"代码行克隆比例"
        }
	}
};
var othersECloneLocConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "有效克隆代码行", "有效非克隆代码行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"有效代码行克隆比例"
        }
	}
};
var othersCloneFileConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "克隆文件", "非克隆文件" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"文件克隆比例"
        }
	}
};

function showChart() {
	for (var i =0;i<commitLs.length;i++){
		var commitL = commitLs[i];
		if(commitLs[i].language == 'java'){
			enableLanguage('java');
			getJavaData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}
		// else if(commitLs[i].language == 'c'){
		// 	getCData([ commitL.cloc, commitL.loc - commitL.cloc ], 
		// 			[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
		// 			[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		// }
		else if(commitLs[i].language == 'c++'){
			enableLanguage('cplusplus');
			getCplusplusData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}else if(commitLs[i].language == 'c#'){
			enableLanguage('csharp');
			getCsharpData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}else if(commitLs[i].language == 'vb'){
			enableLanguage('vb');
			getVbData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}else if(commitLs[i].language == 'js'){
			enableLanguage('js');
			getJsData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}else if(commitLs[i].language == 'others'){
			enableLanguage('others');
			getOthersData([ commitL.cloc, commitL.loc - commitL.cloc ], 
					[ commitL.ecloc, commitL.eloc - commitL.ecloc ], 
					[ commitL.cloneFileNum,commitL.fileNum - commitL.cloneFileNum ]);
		}
	}
	getAllData([ commit.cloc, commit.loc - commit.cloc ], 
					[ commit.ecloc, commit.eloc - commit.ecloc ], 
					[ commit.cloneFileNum,commit.fileNum - commit.cloneFileNum]);
	// getData([2012321,20213213], [201221,2043242], [2012312,302312]);

	// java
	var javaCloneLocCtx = $("#java_cloneLocChart").get(0).getContext("2d");
	new Chart(javaCloneLocCtx, javaCloneLocConfig);

	var javaECloneLocCtx = $("#java_eCloneLocChart").get(0).getContext("2d");
	new Chart(javaECloneLocCtx, javaECloneLocConfig);

	var javaCloneFileCtx = $("#java_cloneFileChart").get(0).getContext("2d");
	new Chart(javaCloneFileCtx, javaCloneFileConfig);

	// all
	var allCloneLocCtx = $("#all_cloneLocChart").get(0).getContext("2d");
	new Chart(allCloneLocCtx, allCloneLocConfig);

	var allECloneLocCtx = $("#all_eCloneLocChart").get(0).getContext("2d");
	new Chart(allECloneLocCtx, allECloneLocConfig);

	var allCloneFileCtx = $("#all_cloneFileChart").get(0).getContext("2d");
	new Chart(allCloneFileCtx, allCloneFileConfig);

	// c++
	var cplusplusCloneLocCtx = $("#cplusplus_cloneLocChart").get(0).getContext("2d");
	new Chart(cplusplusCloneLocCtx, cplusplusCloneLocConfig);

	var cplusplusECloneLocCtx = $("#cplusplus_eCloneLocChart").get(0).getContext("2d");
	new Chart(cplusplusECloneLocCtx, cplusplusECloneLocConfig);

	var cplusplusCloneFileCtx = $("#cplusplus_cloneFileChart").get(0).getContext("2d");
	new Chart(cplusplusCloneFileCtx, cplusplusCloneFileConfig);

	// csharp
	var csharpCloneLocCtx = $("#csharp_cloneLocChart").get(0).getContext("2d");
	new Chart(csharpCloneLocCtx, csharpCloneLocConfig);

	var csharpECloneLocCtx = $("#csharp_eCloneLocChart").get(0).getContext("2d");
	new Chart(csharpECloneLocCtx, csharpECloneLocConfig);

	var csharpCloneFileCtx = $("#csharp_cloneFileChart").get(0).getContext("2d");
	new Chart(csharpCloneFileCtx, csharpCloneFileConfig);
	// js
	var jsCloneLocCtx = $("#js_cloneLocChart").get(0).getContext("2d");
	new Chart(jsCloneLocCtx, jsCloneLocConfig);

	var jsECloneLocCtx = $("#js_eCloneLocChart").get(0).getContext("2d");
	new Chart(jsECloneLocCtx, jsECloneLocConfig);

	var jsCloneFileCtx = $("#js_cloneFileChart").get(0).getContext("2d");
	new Chart(jsCloneFileCtx, jsCloneFileConfig);
	// vb
	var vbCloneLocCtx = $("#vb_cloneLocChart").get(0).getContext("2d");
	new Chart(vbCloneLocCtx, vbCloneLocConfig);

	var vbECloneLocCtx = $("#vb_eCloneLocChart").get(0).getContext("2d");
	new Chart(vbECloneLocCtx, vbECloneLocConfig);

	var vbCloneFileCtx = $("#vb_cloneFileChart").get(0).getContext("2d");
	new Chart(vbCloneFileCtx, vbCloneFileConfig);
	// others
	var othersCloneLocCtx = $("#others_cloneLocChart").get(0).getContext("2d");
	new Chart(othersCloneLocCtx, othersCloneLocConfig);

	var othersECloneLocCtx = $("#others_eCloneLocChart").get(0).getContext("2d");
	new Chart(othersECloneLocCtx, othersECloneLocConfig);

	var othersCloneFileCtx = $("#others_cloneFileChart").get(0).getContext("2d");
	new Chart(othersCloneFileCtx, othersCloneFileConfig);
}

function getJavaData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	javaCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	javaECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	javaCloneFileConfig.data.datasets.push(cloneFileDataset);
}
//all
function getAllData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	allCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	allECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	allCloneFileConfig.data.datasets.push(cloneFileDataset);
}
// c++
function getCplusplusData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	cplusplusCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	cplusplusECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	cplusplusCloneFileConfig.data.datasets.push(cloneFileDataset);
}
// csharp
function getCsharpData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	csharpCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	csharpECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	csharpCloneFileConfig.data.datasets.push(cloneFileDataset);
}
//vb
function getVbData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	vbCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	vbECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	vbCloneFileConfig.data.datasets.push(cloneFileDataset);
}
// js
function getJsData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	jsCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	jsECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	jsCloneFileConfig.data.datasets.push(cloneFileDataset);
}
// others
function getOthersData(cloneLoc, eCloneLoc, cloneFile) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	othersCloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	othersECloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	othersCloneFileConfig.data.datasets.push(cloneFileDataset);
}