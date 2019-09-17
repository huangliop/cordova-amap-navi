
var exec = require('cordova/exec');
var cordova = require('cordova');

function AMap() {
    
}

function naviCallback(params) {
    cordova.fireWindowEvent("amapnavi",params);
}
function locationCallback(params){
    cordova.fireWindowEvent("amaplocation",params);
}
/** 
 * 启动导航
 * @param start {name,lat,lng,poiid} 起始点，
 * @param end {同上} 结束点
 * @param wayList [{name,lat,lng,poiid}...] 途径点列表
 * @param carInfo {carType,carNumber,size,load,weight,length,width,height,axis,loadSwitch,restriction} 车辆信息
 */
AMap.prototype.startNavi=function ({start,wayList,end,carInfo}) {
    exec(naviCallback,null,"Amap","startNavi",[start,wayList,end,carInfo]);
}
AMap.prototype.getLocation=function(option,successCallback,errorCallback){
    exec(successCallback,errorCallback,"Amap","getLocation",[option||{}]);
}
AMap.prototype.startLocation=function(option,errorCallback){
    exec(locationCallback,errorCallback,"Amap","startLocation",[option||{}]);
}
AMap.prototype.stopLocation=function(successCallback,errorCallback){
    exec(successCallback,errorCallback,"Amap","stopLocation",[]);
}
module.exports = new AMap();