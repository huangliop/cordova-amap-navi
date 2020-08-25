# cordova-plugin-amap-gd

高德导航和定位的cordova插件

- 导航功能开发版本:7.1.0

高德定位开发版本

- android版本: 4.7.0 

- iOS版本:2.6.3

## Install 

`cordova plugin add cordova-plugin-amap-gd --variable ANDROID_KEY=key --variable IOS_KEY=key`

### iOS安装后build报错

如果只iOS上build报错，请按[这里的](https://lbs.amap.com/api/ios-location-sdk/guide/create-project/cocoapods)方法处理

## Useage

参选详情请参见[https://lbs.amap.com/api/android-navi-sdk/guide/navi-component/use-navi-component](https://lbs.amap.com/api/android-navi-sdk/guide/navi-component/use-navi-component)

## android缺少权限

请配合这个插件获取权限[https://www.npmjs.com/package/cordova-plugin-android-permissions](https://www.npmjs.com/package/cordova-plugin-android-permissions)

### 普通导航

- 参数中的点为可选参数

```js
window.amap.startNavi({
    start: { 
            name: "北京站", lat: 39.904556, lng: 116.427231,poiid:"B000A83M61"
         },
    wayList:[
        { 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        },{ 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        }
    ]
    , end: { 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        }
    } );

```
### 货车导航

- 参数中的点为可选参数

```js
window.amap.startNavi({
    start: { 
            name: "北京站", lat: 39.904556, lng: 116.427231,poiid:"B000A83M61"
         },
    wayList:[
        { 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        },{ 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        }
    ]
    , end: { 
            name: "北京站", lat: 39.904556, lng: 116.427231 
        }
    ,carInfo:{
        carType:1,carNumber:"渝B88888",size:4,load:50,weight:20,
        length:25,width:2,height:4,axis:6,loadSwitch:true,restriction:true
    }
    } );
```
### 定位一次

```js
 window.amap.getLocation(options,location => {});
```
### 连续定位

```js
//连续定位在启动后，位置信息需要在下面的定位事件回调里面获取
window.amap.startLocation(options);
```

### 定位参数options说明

|名称|类型|说明|支持平台
|--|--|--|--|
|purpose|int|设置定位场景，目前支持三种场景（0=签到、1=出行、2=运动，默认无场景）|Android|
|mode|int|定位模式：0=低功耗模式，1=设备定位模式，2=高精度模式|其中iOS支持2和0类型|
|interval|int|连续定位时，定位的间隔|Android|
|address|boolean|是否需要返回地址信息|Android，iOS|
|mock|boolean|是否允许模拟软件Mock位置结果|Android|
|timeout|int|定位的超时，默认30s（单位：毫秒）|Android|
|cache|int|是否开启定位缓存机制|Android|

### 定位返回的结果location说明

|字段|说明|是否不支持iOS|
|--|--|--|
|lat|纬度|
|lng|经度|
|acc|定位精度 单位:米|是|
|alt|海拔高度信息|是|
|spe|速度,单位：米/秒|是|
|bea|方向角信息|是|
|bui|室内定位建筑物Id|是|
|flo|室内定位楼层|是|
|add|地址描述|
|cou|国家名称|
|pro|省名称|
|cit|城市名称|
|dis|城区名称|
|str|街道名称|
|strNum|街道门牌号信息|
|citCode|城市编码信息|
|adc|区域编码信息|
|poi|当前位置的POI名称|
|aoi|当前位置所处AOI名称|
|gps|获取GPS当前状态，返回值可参考AMapLocation类提供的常量,GPS_ACCURACY_GOOD = 1, GPS_ACCURACY_BAD = 0, GPS_ACCURACY_UNKNOWN = -1;|是|
|loc|定位结果来源.LOCATION_TYPE_GPS = 1;LOCATION_TYPE_SAME_REQ = 2;LOCATION_TYPE_FAST = 3; LOCATION_TYPE_FIX_CACHE = 4; LOCATION_TYPE_WIFI = 5;LOCATION_TYPE_CELL = 6; LOCATION_TYPE_AMAP = 7;LOCATION_TYPE_OFFLINE = 8;LOCATION_TYPE_LAST_LOCATION_CACHE = 9|是|
|locD|信息描述|是|
|einfo|错误信息描述|
|ecode|错误码|

### 导航事件

监听事件
```js
window.addEventListener('amapnavi',function(params){console.log(params.eventType);console.log(params.data)},false);
```

|事件eventType|说明|返回数据data|
|--|--|--|
|initNaviFailure|导航初始化失败||
|navigationText|导航语音的文本|字符串|
|locationChange|经纬度变化的回调|{lat:,lng:,}|
|startNavi|开始导航|数字|
|exitPage|退出导航|数字|

### 连续定位事件

```js
window.addEventListener('amaplocation',function(l){ console.log(l)},false);
```
