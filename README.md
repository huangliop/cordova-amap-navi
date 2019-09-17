# cordova-plugin-amap

高德导航和定位的cordova插件

- android依赖版本:6.9.1

- io依赖版本:

高德定位

- android版本: 4.7.0 

## Install 

`cordova plugin add https://github.com/huangliop/cordova-plugin-amap.git --variable ANDROID_KEY=key --variable IOS_KEY=key`

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
 window.amap.getLocation(options,location => {},location => {}
                    );
```
### 连续定位

```js
//连续定位在启动后，位置信息需要在下面的定位事件回调里面获取
window.amap.startLocation(options);
```

### 定位参数options说明

|名称|说明|
|--|--|
|purpose|设置定位场景，目前支持三种场景（0=签到、1=出行、2=运动，默认无场景）|
|mode|定位模式：0=低功耗模式，1=设备定位模式，2=高精度模式|
|interval|连续定位时，定位的间隔|
|address|是否需要返回地址信息|
|mock|是否允许模拟软件Mock位置结果|
|timeout|定位的超时，默认30s（单位：毫秒）|
|cache|是否开启定位缓存机制|

### 定位返回的结果location说明

|字段|说明|
|--|--|
|lat|纬度|
|lng|经度|
|acc|定位精度 单位:米|
|alt|海拔高度信息|
|spe|速度,单位：米/秒|
|bea|方向角信息|
|bui|室内定位建筑物Id|
|flo|室内定位楼层|
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
|gps|获取GPS当前状态，返回值可参考AMapLocation类提供的常量|
|loc|定位结果来源|
|locD|信息描述|
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
window.addEventListener('amaplocation',location => {
                            console.log(loaction)
                        },
                        false
                    );
```