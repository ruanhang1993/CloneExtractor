$(document).ready(function() {
	window.localStorage.setItem("versionhis",window.location.href);
	project_id = GetRequest();
	$('#versionHisBtn').attr('href','printoneproject.html?projectId='+project_id);
	getCommits();
	$("#diffExtractBtn").click(function() {
		extractDiff();
	});
	window.onload = function() {
        showChart();
    };
});
var commitsInfo;
var project_id;
var pre_commit_id;
var commit_id;
var projectName;
var thisProject;
function getCommits() {
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractVersions.action",
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
				commitsInfo = data.commits;
				projectName = data.projectName;
				thisProject = data.thisProject;
				loadCommits();
				showChart();
				var table = $('.datatable').DataTable();
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
				$("#projectName").text(data.projectName);
				$("#printTableLabel").text(data.projectName+" 版本表格");
			} else {
				alert("failed");
			}
		}
	});

}
function loadOneCommit(i) {
	var commit = commitsInfo[i];
	var iden = 'commit' + i;
	var content = "<tr id = " + iden + ">";
	content += "<td><button type=\"button\" class=\"close\" onclick=\"deleteOneCommit(this)\" data-id=\""+commit.commitId+"\"><span>&times;</span></button></td>";
	content += "<td>";
	content += "<input name='diffExtract' type='checkbox' value = '"
			+ commit.commitId + "'/>";
	content += "</td>";
	content += "<td>";
	content += commit.revisionId;
	content += "</td>";
	content += "<td>";
	content += commit.fileNum;
	content += "</td>";
	content += "<td>";
	content += commit.loc;
	content += "</td>";
	content += "<td>";
	content += commit.eloc;
	content += "</td>";
	content += "<td>";
	content += commit.cloc;
	content += "</td>";
	content += "<td>";
	content += commit.ccloc;
	content += "</td>";
	content += "<td>";
	content += deleteT(commit.submitDate);
	content += "</td>";
	content += "<td>";
	content += deleteT(commit.submitDoneDate);
	content += "</td><td><a href='versiondetail.html?commitId="
			+ commit.commitId + "&projectName="+encodeURI(projectName)
			+"'><span class=\"glyphicon glyphicon-share-alt\"></span></a></td>";
	content += "<td><button style=\"padding:1px 10px;\" type=\"button table-button\" class=\"btn btn-default\" onclick=\"printVersion(this)\" data-id=\""+i+"\">打印</button></td>";
	content += "</tr>";
	$("#versionhis").append(content);
	var f = document.getElementById(iden);
	f.onmousedown = function(e) {
	};
}
function deleteT(date){
	if(date == null){
		return "null";
	}else{
		return date.replace("T"," ");
	}
}
function loadCommits() {
	len = commitsInfo.length;
	for (i = 0; i < len; i++) {
		result.push({'label':commitsInfo[i].revisionId,'cloc':commitsInfo[i].cloc,'eloc':commitsInfo[i].eloc});
		loadOneCommit(i);
	}

}
// check whether there are exactly 2 items are chosen
function checkChoose() {
	pre_commit_id = null;
	commit_id = null;
	var obj = document.getElementsByName('diffExtract');
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			if (pre_commit_id == null)
				pre_commit_id = obj[i].value;
			else if (commit_id == null) {
				commit_id = obj[i].value;
			} else {
				// means that the chosen checkbox is bigger than 2
				return -1;
			}
		}
	}
	if (pre_commit_id == null || commit_id == null)
		// 0 means that not enough item is chosen
		return 0
	return 1;
}
function extractDiff() {
	var date = $('#dtp_input1').val();

	var check = checkChoose();
	if (check == -1) {
		alert("您选中的项目超过两个");
	}
	if (check == 0) {
		alert("您选中的项目少于两个");
	}
	if (date == "") {
		alert("您没有选择日期");
		check = -2;
	}
	if (check == 1) {
		alert("start compare");
		$.ajax({
			type : "post",
			url : "../Zhonghui/extractDiff.action",
			dataType : "json",
			data : {
				preCommitId : pre_commit_id,
				commitId : commit_id,
				compareDate : date
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("XMLHttpRequest:" + XMLHttpRequest.status);
				console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
			},
			success : function(data) {
				if (data.successful == "true" || data.successful == true) {

				} else {
					alert("failed");
				}
			}
		});
	}
}

var config = {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    fill: false,
	                label: "克隆代码行",
		            backgroundColor: "rgba(75,192,192,0.4)",
		            borderColor: "rgba(75,192,192,1)",
		            pointBorderColor: "rgba(75,192,192,1)",
		            pointBackgroundColor: "#fff",
		            pointHoverBackgroundColor: "rgba(75,192,192,1)",
		            pointHoverBorderColor: "rgba(220,220,220,1)"
                },{
                    data: [],
                    fill: false,
	                label: "有效代码行",
		            backgroundColor: "rgba(109,107,241,0.4)",
		            borderColor: "rgba(109,107,241,1)",
		            pointBorderColor: "rgba(109,107,241,1)",
		            pointBackgroundColor: "#fff",
		            pointHoverBackgroundColor: "rgba(109,107,241,1)",
		            pointHoverBorderColor: "rgba(220,220,220,1)"
                }]
            },
            options: {
                responsive: true,
                title:{
                    display:true,
                    text:"版本克隆数据统计"
                },
                scales: {
                    xAxes: [{
                        display: true,
                        ticks: {
                            userCallback: function(dataLabel, index) {
                                return index % 2 === 0 ? dataLabel : '';
                            }
                        }
                    }],
                    yAxes: [{
                        display: true,
                        beginAtZero: false
                    }]
                }
            }
        };
var result = [];
function showChart() {
	var ctx = $("#lineChart").get(0).getContext("2d");
	getData(result);
	window.myLineChart = new Chart(ctx, config);
	window.myLineChart.update();
}

function getData(list) {
	if (list.length > 0) {
		if (list.length > 1){
			var firstVersion = parseInt(list[0].label.substr(1));
			var secondVersion = parseInt(list[1].label.substr(1));
			if(firstVersion < secondVersion){
				var i = 0;
				for(i; i < list.length; i++){
					config.data.labels.push(list[i].label);
		        	config.data.datasets[0].data.push(list[i].cloc);
		        	config.data.datasets[1].data.push(list[i].eloc);
				}
			}else{
				var i = list.length -  1;
				for(i; i >= 0; i--){
					config.data.labels.push(list[i].label);
		        	config.data.datasets[0].data.push(list[i].cloc);
		        	config.data.datasets[1].data.push(list[i].eloc);
				}
			}
		}else{
			config.data.labels.push(list[0].label);
		    config.data.datasets[0].data.push(list[0].cloc);
		    config.data.datasets[1].data.push(list[0].eloc);
		}
    }
}


function deleteOneCommit(element) {
	var index = $("#versionhis tr").index($(element).closest("tr"));
	var commit_id = $(element).attr('data-id');
	$.ajax({
		type : "post",
		url : "../Zhonghui/deleteCommit.action",
		dataType : "json",
		data : {
			commitId : commit_id
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
			} else {
			}
		}
	});
	$("#versionhis tr:eq(" + index + ")").remove();
	
	var start = $("#versionhis").dataTable().fnSettings()._iDisplayStart;  
    var total = $("#versionhis").dataTable().fnSettings().fnRecordsDisplay();  
    window.location.reload();  
    if((total-start)==1){  
        if (start > 0) {  
            $("#versionhis").dataTable().fnPageChange( 'previous', true );  
        }  
    }  
}

//打印PDF
function printVersion(element){
	$('#coverarea').css('display','block');
	var i = $(element).attr('data-id');
	var commitTemp = commitsInfo[i];
	var commitClones;
	var commitFiles;
	var doc = new jsPDF(commitTemp.revisionId);
	$.ajax({
		type : "post",
		url : "../Zhonghui/extractCloneByCommit.action",
		dataType : "json",
		data : {
			commitId : commitTemp.commitId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			console.log("XMLHttpRequest:" + XMLHttpRequest.status);
			console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
		},
		success : function(data) {
			if (data.successful == "true" || data.successful == true) {
				commitClones = data.clones;
				commitFiles = data.files;
				var maxY = 286;
				var y = 10;
				var vStartY = y;
				var vEndY = y;
				doc.setLineWidth(0.5);
				if(commitClones!=null && commitClones.length>0){
					for(var j in commitClones){
						doc.setFontSize(13);
						//换行是对第二个参数加10，最大290
						//水平线 24参数同
						//垂直线 13参数同
						//doc.line(170, 10, 170, 288);
						if(y+10 >= maxY){
							y = 10;
							vStartY = y;
					 		vEndY = y;
							doc.addPage();
						}
						doc.line(10, y, 200, y);
						doc.text(15, y+7, ''+j+'. '+commitFiles[j]);
						y = y + 10;

						if(y+10 >= maxY){
							y = 10;
							vStartY = y;
					 		vEndY = y;
							doc.addPage();
						}
						doc.line(10, y, 200, y);
						doc.text(15, y+7, 'Clone Type: '+commitClones[j].cloneType);
						doc.text(110, y+7, 'Position: '+commitClones[j].beginLine+'~'+commitClones[j].endLine);
						y = y + 10;

						if(y+10 >= maxY){
							y = 10;
							vStartY = y;
					 		vEndY = y;
							doc.addPage();
						}
						doc.line(10, y, 200, y);
						doc.text(90, y+7, 'Relative Clone');
						y = y + 10;
						vStartY = y;
						vEndY = y;

						if(y+10 >= maxY){
							doc.line(170, vStartY, 170, vEndY);
							y = 10;
							vStartY = y;
					 		vEndY = y;
							doc.addPage();
						}
						doc.line(10, y, 200, y);
						doc.text(12, y+7, 'File Name');
						doc.text(172, y+7, 'Position');
						y = y + 10;
						vEndY = y;

						if(y+10 >= maxY){
							doc.line(170, vStartY, 170, vEndY);
							y = 10;
							vStartY = y;
					 		vEndY = y;
							doc.addPage();
						}
						doc.line(10, y, 200, y);

						$.ajax({
							type : "post",
							url : "../Zhonghui/getClones.action",
							dataType : "json",
							async: false,
							data : {
								cloneId : commitClones[j].cloneId
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								console.log("XMLHttpRequest:" + XMLHttpRequest.status);
								console.log("XMLHttpRequest:" + XMLHttpRequest.readyState);
							},
							success : function(data) {
								if (data.successful == "true" || data.successful == true) {
									var thisClones = data.clones;
									var thisFiles = data.files;
									for(var k in thisClones){
										if(y+8 >= maxY){
											doc.line(170, vStartY, 170, vEndY);
											y = 10;
											vStartY = y;
					 						vEndY = y;
											doc.addPage();
										}
										doc.setFontSize(10);
										doc.text(12, y+5, thisFiles[k]);
										doc.text(172, y+5, ''+thisClones[k].beginLine+'~'+thisClones[k].endLine);
										vEndY = y + 8;
										y = y+8;
									}
									doc.line(170, vStartY, 170, vEndY);
									doc.line(10, y, 200, y);
									if(y + 15 >= maxY){
										y = 10;
										vStartY = y;
					 					vEndY = y;
										doc.addPage();
									}else{
										y = y + 15;
									}
								} else {
									alert("failed");
								}
							}
						});
					}
				}
				// Output as Data URI
				doc.output('datauri');
			} else {
				alert("failed");
			}
		}
	});
}