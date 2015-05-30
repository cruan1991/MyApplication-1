package com.example.myapplication;

import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityMap extends FragmentActivity {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        Bundle extras = getIntent().getExtras();
        String accountName = extras.getString("name");
        //TODO: check query
        String query = "select itemname, itemphoto, latitude, longitude from item where acctname = '" + accountName + "'";
        Cursor cursor = MainActivity.ABD.query(query);

        int locationCount = 0;
        String itemName, photoPath;
        double lat = 0;
        double lng = 0;
        float zoom = 10;
        if(cursor != null){
            locationCount = cursor.getCount();
            cursor.moveToFirst();
        } else {
            locationCount = 0;
        }

        for(int i = 0; i < locationCount; i++){
            itemName = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_ITEMNAME));
            photoPath = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_PHOTO_ITEM));
            lat = cursor.getDouble(cursor.getColumnIndex(AccountBookDatabase.KEY_LAT));
            lng = cursor.getDouble(cursor.getColumnIndex(AccountBookDatabase.KEY_LONG));
            int target = 200;
            Bitmap drawBmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(MainActivity.mDirPath + photoPath), target, target);
            LatLng point = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions().position(point).title(itemName).icon(BitmapDescriptorFactory.fromBitmap(drawBmp)));
            cursor.moveToNext();
        }

        if(locationCount > 0){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
            map.animateCamera(update);
        }
    }
}
