
var argscheck = require('cordova/argscheck');
var channel = require('cordova/channel');
var utils = require('cordova/utils');
var exec = require('cordova/exec');
var cordova = require('cordova');

function Navi() {
    
}
/** 
 * 启动导航
 * @param start {name,lat,lng,poiid} 起始点，
 * @param end {同上} 结束点
 * @param wayList [{name,lat,lng,poiid}...] 途径点列表
 * @param carInfo {carType,carNumber,size,load,weight,length,width,height,axis,loadSwitch,restriction} 车辆信息
 */
Navi.prototype.startNavi=function ({start,wayList,end,carInfo},successCallback,errorCallback) {
    exec(successCallback,errorCallback,"AmapNavi","startNavi",[start,wayList,end,carInfo]);
}
module.exports = new Navi();