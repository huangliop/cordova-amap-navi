package com.hl.amap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import io.cordova.hellocordova.MainActivity;

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
        if ("startNavi".equals(action)) {
            this.requsetPermission(LOCATION);
            AmapNaviPage.getInstance().showRouteActivity(this.cordova.getContext(), new AmapNaviParams(null), this);

            callbackContext.success();
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


    //------------------------INaviInfoCallback--method-----------------------
    @Override
    public void onInitNaviFailure() {
        if(this.callbackContext!=null){
            this.callbackContext.error("startNavi failed");
        }
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {
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