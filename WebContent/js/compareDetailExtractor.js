$(document).ready(function() {
	window.localStorage.setItem("comparedetail",window.location.href);
	compare_id = GetRequestTwo()['first'];
	projectName = decodeURI(GetRequestTwo()['second']);
	$('#projectName').text(projectName);
	$('#compareHistory').attr('href',window.localStorage.getItem('comparehis'));
	$("#comparefilebtn").click(function() {
		location.href = "../Zhonghui/comparefiledetail.html?compareId="+ compare_id+"&projectName="+encodeURI(projectName);
	});
	getcompare();

});
var compare;
var compare_id;
var projectName;
function getcompare() {
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
				$('#oneCompare').html(compare.revisionId+" & "+compare.preRevisionId);
				$.ajax({
					type : "post",
					url : "../Zhonghui/extractOneVersion.action",
					dataType : "json",
					data : {
						commitId : compare.commitId
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("XMLHttpRequest:" + XMLHttpRequest.status);
						console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
					},
					success : function(data) {
						if (data.successful == "true" || data.successful == true) {
							var commitLs = data.commitLs;
							var languageAll = "";
							for(var i in commitLs){
								languageAll = languageAll+"/"+commitLs[i].language;
							}
							$('#language').html(languageAll);
							loadOnecompare();
							showChart();
						} else {
							alert("failed");
						}
					}
				});
			} else {
				alert("failed");
			}
		}
	});

}
function loadOnecompare() {
	$("#preRevisionId").text(compare.preRevisionId);
	$("#revisionId").text(compare.revisionId);
	$("#compareDate").text(deleteT(compare.processDate));
	$("#finishDate").text(deleteT(compare.processDoneDate));

	$("#chloc").text(compare.chloc);
	$("#noteChloc").text(compare.cmchloc);
	$("#blankChloc").text(compare.bchloc);
	$("#echloc").text(compare.echloc);

	$("#cloc").text(compare.cloc);
	$("#blankCloc").text(compare.bcloc);
	$("#noteCloc").text(compare.cmcloc);
	$("#ecloc").text(compare.ecloc);

	$("#dcloc").text(compare.dcloc);
	$("#noteDcloc").text(compare.cmdcloc);
	$("#blankDcloc").text(compare.bdcloc);
	$("#edcloc").text(compare.edcloc);

	$("#cloneRate").text(
			((compare.cloc + compare.dcloc) / compare.chloc * 100).toFixed(2)
					+ "%");
	$("#ecloneRate").text(
			((compare.ecloc + compare.edcloc) / compare.echloc * 100)
					.toFixed(2)
					+ "%");
	$("#fileNum").text(compare.changeFileNum);
	$("#cloneFileNum").text(compare.cloneChangeFileNum);
	$("#versionSpan").text(compare.versionSpan);
	$("#timeSpan").text(timespan2date(compare.timeSpan));
	$("#finalLine").text(compare.ccloc);
	$("#efinalLine").text(compare.eccloc);
}
function timespan2date(timespan){
	var total_second =  parseInt(timespan/1000);
	var years = parseInt(total_second / (60*60*24*30*12));
	var months = parseInt(total_second / (60*60*24*30));
	var days = parseInt(total_second / (60*60*24));
	var hours = parseInt(total_second/(60*60))%24;
	var minutes = parseInt(total_second/60) % 60;
	var seconds = total_second % 60;
	var result = "";
	if(years > 0){
		result = result+years+"年";
	}
	if(months > 0){
		result = result+months+"月";
	}
	if(days > 0){
		result = result+days+"日";
	}
	if(result == ""){
		result = hours+"h "+minutes+"m "+seconds+"s";
	}
	return result;
}

// added by rh
var cloneLocConfig = {
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
var eCloneLocConfig = {
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
var cloneFileConfig = {
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
var cloneFileTypeConfig = {
	type : 'pie',
	data : {
		datasets : [],
		labels : [ "增加克隆行", "删除克隆行" ]
	},
	options : {
		responsive : true,
		title:{
            display:true,
            text:"克隆类型详情"
        }
	}
};

function showChart() {
	getData([ compare.cloc + compare.dcloc,
			compare.chloc - compare.cloc - compare.dcloc ], [
			compare.ecloc + compare.edcloc,
			compare.echloc - compare.ecloc - compare.edcloc ], [
			compare.cloneChangeFileNum,
			compare.changeFileNum - compare.cloneChangeFileNum ], [
			compare.cloc, compare.dcloc ]);
	// getData([2012321,20213213], [201221,2043242], [2012312,302312],
	// [2012312,302312]);

	var cloneLocCtx = $("#cloneLocChart").get(0).getContext("2d");
	new Chart(cloneLocCtx, cloneLocConfig);

	var eCloneLocCtx = $("#eCloneLocChart").get(0).getContext("2d");
	new Chart(eCloneLocCtx, eCloneLocConfig);

	var cloneFileCtx = $("#cloneFileChart").get(0).getContext("2d");
	new Chart(cloneFileCtx, cloneFileConfig);

	var cloneFileTypeCtx = $("#cloneFileTypeChart").get(0).getContext("2d");
	new Chart(cloneFileTypeCtx, cloneFileTypeConfig);
}

function getData(cloneLoc, eCloneLoc, cloneFile, cloneFileType) {
	// {data: [20,20],backgroundColor: ["#F7464A","#46BFBD"]}
	var cloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneLoc
	};
	cloneLocConfig.data.datasets.push(cloneLocDataset);

	var eCloneLocDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : eCloneLoc
	};
	eCloneLocConfig.data.datasets.push(eCloneLocDataset);

	var cloneFileDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFile
	};
	cloneFileConfig.data.datasets.push(cloneFileDataset);

	var cloneFileTypeDataset = {
		backgroundColor : [ "#FFB6C1", "#AFD7ED" ],
		data : cloneFileType
	};
	cloneFileTypeConfig.data.datasets.push(cloneFileTypeDataset);
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}