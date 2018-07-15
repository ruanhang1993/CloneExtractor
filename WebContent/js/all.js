function GetRequest() {
var url = location.search; //获取url中"?"符后的字串
if (url.indexOf("?") != -1) { //判断是否有参数
var str = url.substr(1); //从第一个字符开始 因为第0个是?号 获取所有除问号的所有符串
strs = str.split("="); //用等号进行分隔 （因为知道只有一个参数 所以直接用等号进分隔 如果有多个参数 要用&号分隔 再用等号进行分隔）
return strs[1];
}
}
function GetRequestTwo() {
	var urlinfo = location.search;
	var len = urlinfo.length; 
	var offset = urlinfo.indexOf("?");
	var newsidinfo = urlinfo.substr(offset,len);
	var newsids = newsidinfo.split("&");
	var idStr = newsids[0].split("=");
	var versionStr = newsids[1].split("=");
	var obj = {"first":idStr[1], "second":versionStr[1]};
    return obj;
}
