# cordova-amap-navi
高德导航的cordova插件

- android依赖版本:6.9.1

- io依赖版本:

## Install 

`cordova plugin add https://github.com/huangliop/cordova-amap-navi.git --variable ANDROID_KEY=key --variable IOS_KEY=key`

## Useage

参选详情请参见[https://lbs.amap.com/api/android-navi-sdk/guide/navi-component/use-navi-component](https://lbs.amap.com/api/android-navi-sdk/guide/navi-component/use-navi-component)

- 参数中的点都可以不传

### 普通导航

```js
window.amapNavi.startNavi({
    start: { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231
        ,poiid:"B000A83M61"
         }
    wayList:[
        { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        },{ 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        }
    ]
    , end: { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        }
    } );

```
### 货车导航

```js
window.amapNavi.startNavi({
    start: { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231
        ,poiid:"B000A83M61"
         }
    wayList:[
        { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        },{ 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        }
    ]
    , end: { 
        name: "北京站"
        , lat: 39.904556
        , lng: 116.427231 
        }
    ,carInfo:{
        carType:1,
        carNumber:"渝B88888",
        size:4,
        load:50,
        weight:20,
        length:25,
        width:2,
        height:4,
        axis:6,
        loadSwitch:true,
        restriction:true
    }
    } );
```