
var exec = require('cordova/exec');
var cordova = require('cordova');

function Navi() {
    
}

function eventCallback(params) {
    cordova.fireDocumentEvent("amapnavi",params);
}
/** 
 * 启动导航
 * @param start {name,lat,lng,poiid} 起始点，
 * @param end {同上} 结束点
 * @param wayList [{name,lat,lng,poiid}...] 途径点列表
 * @param carInfo {carType,carNumber,size,load,weight,length,width,height,axis,loadSwitch,restriction} 车辆信息
 */
Navi.prototype.startNavi=function ({start,wayList,end,carInfo}) {
    exec(eventCallback,null,"Amap","startNavi",[start,wayList,end,carInfo]);
}
module.exports = new Navi();