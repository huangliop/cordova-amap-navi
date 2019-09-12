package com.gd.amap.navi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

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


import java.util.ArrayList;
import java.util.List;

import static org.apache.cordova.camera.CameraLauncher.PERMISSION_DENIED_ERROR;

/**
* This class echoes a string called from JavaScript.
*/
public class AmapNavi extends CordovaPlugin implements INaviInfoCallback {
    private static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_REQUEST_CODE=874;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext=callbackContext;
        if ("startNavi".equals(action)) {//开始导航
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
        return false;
    }

    private void requsetPermission(String permission){
        if (!this.cordova.hasPermission(permission)){
            return;
        }else {
            this.cordova.requestPermission(this,LOCATION_REQUEST_CODE,permission);
        }
    }


    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for(int r:grantResults)
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }
        switch(requestCode)
        {
            case LOCATION_REQUEST_CODE:

                break;
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

    private void updateInfo(JSONObject object){
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, object);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        }
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
        updateInfo(object);
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
        updateInfo(object);
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
        updateInfo(object);
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
        updateInfo(object);
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
        updateInfo(object);
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
}