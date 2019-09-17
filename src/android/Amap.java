package com.gd.amap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapNaviLocation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* This class echoes a string called from JavaScript.
*/
public class Amap extends CordovaPlugin implements INaviInfoCallback , AMapLocationListener {
    private static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_REQUEST_CODE=874;
    private CallbackContext naviCallbackContext;
    private CallbackContext locationCallbackContext;
    //是否为只定位一次的模式
    private boolean isLocationOnce;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("startNavi".equals(action)) {//开始导航
            this.naviCallbackContext=callbackContext;
            Poi start =args.isNull(0)?null:jsonToPoi(args.getJSONObject(0));
            List<Poi> list=args.isNull(1)?null:jsonToPoiList(args.getJSONArray(1));
            Poi end =args.isNull(2)?null:jsonToPoi(args.getJSONObject(2));
            AmapNaviParams params=new AmapNaviParams(start, list, end, AmapNaviType.DRIVER);
            AMapCarInfo info=args.isNull(3)?null:jsonToCarInfo(args.getJSONObject(3));
            if(info!=null){
                params.setCarInfo(info);
            }
            AmapNaviPage.getInstance().showRouteActivity(this.cordova.getContext(), params,this);
            PluginResult result=new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }
        if("getLocation".equals(action)){
            //获取一次定位
            startLocation(callbackContext,args.getJSONObject(0),true);
            return  true;
        }
        if("startLocation".equals(action)){
            //连续定位
            startLocation(callbackContext,args.getJSONObject(0),false);
            return  true;
        }
        if("stopLocation".equals(action)){
            mLocationClient.stopLocation();
            return  true;
        }
        return false;
    }


    private void startLocation(CallbackContext callbackContext,JSONObject opt, boolean isOnce){
        locationCallbackContext=callbackContext;
        isLocationOnce=isOnce;
        initLocation();
        AMapLocationClientOption option = jsonToLocationOption(opt);
        option.setOnceLocation(true);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }


    /**
     * json数据转换为Poi对象
     * @param object
     * @return
     */
    private Poi jsonToPoi(JSONObject object){
        Poi p= null;
        try {
            String name=object.has("eventType")?object.getString("eventType"):null;
            String poi=object.has("poiid")?object.getString("poiid"):null;
            Long lat=object.has("lat")?object.getLong("lat"):null;
            Long lng=object.has("lng")?object.getLong("lng"):null;
            p = new Poi(name,new LatLng(lat,lng),poi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * 途径点转换
     * @param object
     * @return
     */
    private List<Poi> jsonToPoiList(JSONArray object){
        List<Poi> list=new ArrayList<>();
        for (int i = 0; i < object.length(); i++) {
            try {
                list.add(jsonToPoi(object.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * json对象转为AMapCarInfo对象
     * @param object
     * @return
     */
    private AMapCarInfo jsonToCarInfo(JSONObject object){
        AMapCarInfo info=new AMapCarInfo();
        try {
            info.setCarType(object.has("carType")?object.getString("carType"):null);
            info.setCarNumber(object.has("carNumber")?object.getString("carNumber"):null);
            info.setVehicleSize(object.has("size")?object.getString("size"):null);
            info.setVehicleLoad(object.has("load")?object.getString("load"):null);
            info.setVehicleWeight(object.has("weight")?object.getString("weight"):null);
            info.setVehicleLength(object.has("length")?object.getString("length"):null);
            info.setVehicleWidth(object.has("width")?object.getString("width"):null);
            info.setVehicleHeight(object.has("height")?object.getString("height"):null);
            info.setVehicleAxis(object.has("axis")?object.getString("axis"):null);
            info.setVehicleLoadSwitch(object.has("loadSwitch")&&object.getBoolean("loadSwitch"));
            info.setRestriction(object.has("restriction")&&object.getBoolean("restriction"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
    private AMapLocationClientOption jsonToLocationOption(JSONObject object){
        AMapLocationClientOption option=new AMapLocationClientOption();

        try {
            if(object.has("purpose")){
                option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.values()[object.getInt("purpose")]);
            }
            if (object.has("mode")){
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.values()[object.getInt("mode")]);
            }
            if(object.has("interval")){
                option.setInterval(object.getLong("interval"));
            }
            if(object.has("address")){
                option.setNeedAddress(object.getBoolean("address"));
            }
            if(object.has("mock")){
                option.setMockEnable(object.getBoolean("mock"));
            }
            if(object.has("timeout")){
                option.setHttpTimeOut(object.getLong("timeout"));
            }
            if(object.has("cache")){
                option.setLocationCacheEnable(object.getBoolean("cahe"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return option;
    }

    private void updateInfo(CallbackContext cb, JSONObject object,boolean keep){
        if (cb != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, object);
            result.setKeepCallback(keep);
            cb.sendPluginResult(result);
        }
    }

    private void initLocation(){
        if(mLocationClient==null){
            //初始化定位
            mLocationClient = new AMapLocationClient(cordova.getActivity().getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
        }
    }

    private JSONObject locationToJSON(AMapLocation location){
        JSONObject object=new JSONObject();
        try {
            object.put("lat",location.getLatitude());
            object.put("lng",location.getLongitude());
            object.put("acc",location.getAccuracy());
            object.put("alt",location.getAltitude());
            object.put("spe",location.getSpeed());
            object.put("bea",location.getBearing());
            object.put("bui",location.getBuildingId());
            object.put("flo",location.getFloor());
            object.put("add",location.getAddress());
            object.put("cou",location.getCountry());
            object.put("pro",location.getProvince());
            object.put("cit",location.getCity());
            object.put("dis",location.getDistrict());
            object.put("str",location.getStreet());
            object.put("strNum",location.getStreetNum());
            object.put("citCode",location.getCityCode());
            object.put("adc",location.getAdCode());
            object.put("poi",location.getPoiName());
            object.put("aoi",location.getAoiName());
            object.put("gps",location.getGpsAccuracyStatus());
            object.put("loc",location.getLocationType());
            object.put("locD",location.getLocationDetail());
            object.put("ecode",location.getErrorCode());
            object.put("einfo",location.getErrorInfo());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(location.getTime());
            object.put("tim",df.format(date));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  object;
    }
    //------------------------INaviInfoCallback--method-----------------------
    @Override
    public void onInitNaviFailure() {
        JSONObject object=new JSONObject();
        try {
            object.put("eventType","initNaviFailure");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateInfo(naviCallbackContext,object,true);
    }

    @Override
    public void onGetNavigationText(String s) {
        JSONObject object=new JSONObject();
        try {
            object.put("eventType","navigationText");
            object.put("data",s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateInfo(naviCallbackContext,object,true);
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        JSONObject object=new JSONObject();
        JSONObject location=new JSONObject();
        try {
            object.put("eventType","locationChange");
            location.put("lat",aMapNaviLocation.getCoord().getLatitude());
            location.put("lng",aMapNaviLocation.getCoord().getLongitude());
            object.put("data",location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateInfo(naviCallbackContext,object,true);
    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {
        JSONObject object=new JSONObject();
        try {
            object.put("eventType","startNavi");
            object.put("data",i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateInfo(naviCallbackContext,object,true);
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {
        JSONObject object=new JSONObject();
        try {
            object.put("eventType","exitPage");
            object.put("data",i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateInfo(naviCallbackContext,object,true);
    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    //--------------INaviInfoCallback----end-------------------
    //--------------AMapLocationListener-----------------------
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if(isLocationOnce){
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    locationCallbackContext.success(locationToJSON(amapLocation));
                }else {
                    locationCallbackContext.error(locationToJSON(amapLocation));
                }
            }else {
                locationCallbackContext.error(0);
            }
            mLocationClient.stopLocation();
            locationCallbackContext=null;
        }else {
            updateInfo(locationCallbackContext,locationToJSON(amapLocation),true);
        }
    }
    //-------------AMapLocationListener--end------------------


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null){
            mLocationClient.onDestroy();
            mLocationClient=null;
        }
    }
}