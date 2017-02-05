package com.yxf.baidumapclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView=null;
    private BaiduMap mBaiduMap=null;
    private LocationClient mLocationClient=null;
    private MyLocationListener mLocationListener=null;

    private BitmapDescriptor mLocationBitMap=null;//定位图标

    boolean isSatelliteTrue=false;
    boolean isFirstIn=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomBy(3.f);
        mBaiduMap.setMapStatus(msu);

        initLocation();
    }
    private void initLocation(){
        mLocationClient = new LocationClient(this);
        mLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd0911");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

        //设置图标定位
        mLocationBitMap= BitmapDescriptorFactory.fromResource(R.drawable.arrow);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if(mLocationClient!=null&&!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_style,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_style:
                if(isSatelliteTrue){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    isSatelliteTrue=false;
                    item.setIcon(R.drawable.satellite_false);
                }else if(!isSatelliteTrue){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    isSatelliteTrue = true;
                    item.setIcon(R.drawable.satellite_true);
                }
                break;
            case R.id.map_traffic:
                if(mBaiduMap.isTrafficEnabled()){
                    mBaiduMap.setTrafficEnabled(false);
                    item.setIcon(R.drawable.traffic_off);
                }else{
                    mBaiduMap.setTrafficEnabled(true);
                    item.setIcon(R.drawable.traffic_on);

                }

                break;
            default:

                break;

        }
        return super.onOptionsItemSelected(item);
    }
    class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data=new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            //设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, mLocationBitMap);
            mBaiduMap.setMyLocationConfigeration(config);

            if(isFirstIn){
                LatLng latlng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn=false;
                Toast.makeText(getApplicationContext(),"dingweichenggong",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
