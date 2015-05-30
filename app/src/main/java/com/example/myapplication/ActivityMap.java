package com.example.myapplication;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityMap extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        // map.addMarker(new MarkerOptions().position(LOCATION_BUILDING).title("Find Me Here!"));
        //get map fragment
        //Enabling MyLocation layer of Google Map
        map.setMyLocationEnabled(true);
        //Invoke LoaderCallbacks to retrieve and draw already saved locations in map
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1){
        Loader<Cursor> c = new CursorLoader(this, LocationsContentProvider.CONTENT_URI, null, null, null, null);
        //Uri to the content provider LocationsContentProvider
        //Fetches all the rows from locations table
//        getContentResolver().query(LocationsContentProvider.CONTENT_URI, null, null, null, null);
        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1){
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;
        //Number of locations available in the SQLite database table
        if(arg1 != null){
            locationCount = arg1.getCount();
            //Move the current record pointer to the first row of the table
            arg1.moveToFirst();
        } else {
            locationCount = 0;
        }

        for(int i = 0; i < locationCount; i++){
            //Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(LocationDB.LATITUDE));
            //Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(LocationDB.LONGITUDE));
            //Get the zoom level
            zoom = (float) arg1.getDouble(arg1.getColumnIndex(LocationDB.ZOOM_LEVEL));
            //Creating an instance of LatLng to plot the location in Google Maps
            LatLng point = new LatLng(lat, lng);
            //Drawing the marker in the Google Maps
            map.addMarker(new MarkerOptions().position(point));
            //Traverse the pointer to the next row
            arg1.moveToNext();
        }

        if(locationCount > 0){
            //Moving CameraPosition to last clicked position
            //Setting the zoom level in the map on last position is clicked
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
            map.animateCamera(update);
        }
    }

    public void onLoaderReset(Loader<Cursor> arg0){

    }
}
