
var argscheck = require('cordova/argscheck');
var channel = require('cordova/channel');
var utils = require('cordova/utils');
var exec = require('cordova/exec');
var cordova = require('cordova');

function Navi() {
    
}
Navi.prototype.startNavi=function (start,wayList,end,successCallback,errorCallback) {
    exec(successCallback,errorCallback,"AmapNavi","startNavi",[start,wayList,end]);
}
module.exports = new Navi();