package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateItem extends Activity {
    private final int SELECT_PHOTO = 1;
    private EditText eventDate;
    private SimpleDateFormat dateFormatter;
    private String photoPath;
    private EditText itemName;
    private EditText amountInput;
    private EditText payer;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        GPSTracker gps;

        eventDate = (EditText) findViewById(R.id.itemDateInput);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        eventDate.setText(dateFormatter.format(new Date()));

        itemName = (EditText) findViewById(R.id.itemDateInput);
        amountInput = (EditText) findViewById(R.id.amountInput);
        payer = (EditText) findViewById(R.id.payerInput);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clearItem) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.itemDateInput:
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        eventDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.payerInput:
                break;
            case R.id.payToInput:
                break;
            case R.id.itemPicPicker:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.getLocation:
                gps = new GPSTracker(CreateItem.this);
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    //TODO:
                    //save to database
                    EditText location = (EditText) findViewById(R.id.locationInput);
                    location.setText(gps.getAddress());

                    Location location = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);

                    //you can hard-code the lat & long if you have issues with getting it
                    //remove the below if-condition and use the following couple of lines
                    //double latitude = 37.422005;
                    //double longitude = -122.084095

                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                    } else {
                        showSettingsAlert();
                    }

                }else{
                    gps.showSettingsAlert();
                }
                break;
            case R.id.done:
                String itemname = itemName.getText().toString();
                String eventTime = eventDate.getText().toString();
                double amount = Double.parseDouble(amountInput.getText().toString());
                String payerName = payer.getText().toString();
                //insert item into Item Table
                ContentValues ctx = new ContentValues();
                ctx.put(AccountBookDatabase.KEY_ITEMNAME, itemname);
                ctx.put(AccountBookDatabase.KEY_DATE_ITEM, eventTime);
                ctx.put(AccountBookDatabase.KEY_VALUE, amount);
                ctx.put(AccountBookDatabase.KEY_BUYER, payerName);
                ctx.put(AccountBookDatabase.KEY_PHOTO_ITEM, photoPath);
                ctx.put();

                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ImageView itemPic = (ImageView) findViewById(R.id.itemPic);
                        itemPic.setImageBitmap(selectedImage);
                        photoPath = imageUri.toString();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
