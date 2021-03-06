package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateItem extends Activity {
    private final int SELECT_PHOTO = 1;
    private EditText eventDate;
    private SimpleDateFormat dateFormatter;
    private String photoPath;
    private EditText itemName;
    private EditText amountInput;
    private EditText itemAccount;
    private EditText paidBy;
    private EditText paidTo;
    private EditText location;
    private RadioGroup paidByList;
    private LinearLayout paidForList;
    private RadioGroup accountList;
    private double longitude;
    private double latitude;
    private Uri imageUri;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        gps = new GPSTracker(CreateItem.this);

        eventDate = (EditText) findViewById(R.id.itemDateInput);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        eventDate.setText(dateFormatter.format(new Date()));

        itemName = (EditText) findViewById(R.id.itemNameInput);
        amountInput = (EditText) findViewById(R.id.amountInput);
        itemAccount = (EditText) findViewById(R.id.accountSelect);
        paidBy = (EditText) findViewById(R.id.payerInput);
        paidTo = (EditText) findViewById(R.id.payToInput);
        location = (EditText) findViewById(R.id.locationInput);
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
        int count;
        Cursor cursor;
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
            case R.id.accountSelect:
                final Dialog accountSelectDialog = new Dialog(this);
                accountSelectDialog.setContentView(R.layout.select_account_dialog);
                accountSelectDialog.setTitle(R.string.select_account);

                //TODO: check query
                accountList = (RadioGroup) accountSelectDialog.findViewById(R.id.selectAccountList);
                String query = "select acctname from account";
                cursor = MainActivity.ABD.query(query);
                if(cursor != null){
                    count = cursor.getCount();
                    //Move the current record pointer to the first row of the table
                    cursor.moveToFirst();
                } else {
                    count = 0;
                }

                for(int i = 0; i < count; i++){
                    String accountName = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_ACCTNAME));
                    RadioButton account = new RadioButton(this);
                    account.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    account.setText(accountName);
                    accountList.addView(account);
                    cursor.moveToNext();
                }

                Button selectAccountOKButton = (Button) accountSelectDialog.findViewById(R.id.accountSelectOK);
                selectAccountOKButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = accountList.getChildCount();
                        RadioButton rb;

                        for(int i = 0; i < size; i++){
                            rb = (RadioButton) accountList.getChildAt(i);
                            if(rb.isChecked()){
                                itemAccount.setText(rb.getText());
                                break;
                            }
                        }

                        accountSelectDialog.dismiss();
                    }
                });

                accountSelectDialog.show();
                break;
            case R.id.payerInput:

                final Dialog paidByDialog = new Dialog(this);
                paidByDialog.setContentView(R.layout.paid_by_dialog);
                paidByDialog.setTitle(R.string.select_members);

                String accountName = itemAccount.getText().toString();
                if(accountName.equals("")){
                    showErrorDialog("Select account first!");
                    break;
                }

                paidByList = (RadioGroup) paidByDialog.findViewById(R.id.paidByList);

                //TODO: check query
                query = "select membername from member where acctname = '" + accountName + "'";
                cursor = MainActivity.ABD.query(query);
                if(cursor != null){
                    count = cursor.getCount();
                    //Move the current record pointer to the first row of the table
                    cursor.moveToFirst();
                } else {
                    count = 0;
                }

                for(int i = 0; i < count; i++){
                    String memberName = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_MEMBERNAME));

                    RadioButton member = new RadioButton(this);
                    member.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    member.setText(memberName);
                    paidByList.addView(member);

                    cursor.moveToNext();
                }

                Button paidByOKButton = (Button) paidByDialog.findViewById(R.id.paidByOK);
                paidByOKButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = paidByList.getChildCount();
                        String result = "";
                        RadioButton rb;

                        for(int i = 0; i < size; i++){
                            rb = (RadioButton) accountList.getChildAt(i);
                            if(rb.isChecked()){
                                paidBy.setText(rb.getText());
                                break;
                            }
                        }

                        paidByDialog.dismiss();
                    }
                });

                paidByDialog.show();
                break;
            case R.id.payToInput:
                final Dialog paidForDialog = new Dialog(this);
                paidForDialog.setContentView(R.layout.paid_for_dialog);
                paidForDialog.setTitle(R.string.select_members);

                accountName = itemAccount.getText().toString();
                if(accountName.equals("")){
                    showErrorDialog("Select account first!");
                    break;
                }

                paidForList = (LinearLayout) paidForDialog.findViewById(R.id.paidForList);

                //TODO: check query
                query = "select membername from member where acctname = '" + accountName + "'";
                cursor = MainActivity.ABD.query(query);
                if(cursor != null){
                    count = cursor.getCount();
                    //Move the current record pointer to the first row of the table
                    cursor.moveToFirst();
                } else {
                    count = 0;
                }

                for(int i = 0; i < count; i++){
                    String memberName = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_MEMBERNAME));

                    CheckBox member = new CheckBox(this);
                    member.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    member.setText(memberName);
                    paidForList.addView(member);

                    cursor.moveToNext();
                }

                CheckBox selectAll2 = (CheckBox) paidForDialog.findViewById(R.id.paidBySelectAll);
                selectAll2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int size = paidForList.getChildCount();
                        boolean checked = ((CheckBox)v).isChecked();
                        for(int i = 0; i < size; i++) {
                            CheckBox cb = (CheckBox)paidForList.getChildAt(i);
                            cb.setChecked(checked);
                        }
                    }
                });

                Button paidForOKButton = (Button) paidForDialog.findViewById(R.id.paidForOK);
                paidForOKButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = paidForList.getChildCount();
                        String result = "";
                        CheckBox cb;

                        for(int i = 0; i < size; i++){
                            cb = (CheckBox) paidForList.getChildAt(i);
                            if(cb.isChecked()){
                                result += cb.getText().toString() + ",";
                            }
                        }

                        if(!result.equals("")){
                            paidTo.setText(result.substring(0, result.length()-1));
                        }

                        paidForDialog.dismiss();
                    }
                });

                paidForDialog.show();
                break;
            case R.id.itemPicPicker:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.getLocation:
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                System.out.println(latitude);
                System.out.println(longitude);
                Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if(addresses != null) {
                        Address fetchedAddress = addresses.get(0);
                        StringBuilder strAddress = new StringBuilder();
                        for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                        }
                        location.setText(strAddress.toString().substring(0, strAddress.toString().length()-1));

                    } else {
                        location.setText("No location found");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not get address.", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.done:
                String itemname = itemName.getText().toString();
                if(itemname.equals("")){
                    showErrorDialog("Item name cannot be blank!");
                    break;
                }

                accountName = itemAccount.getText().toString();
                if(accountName.equals("")){
                    showErrorDialog("Account cannot be blank!");
                    break;
                }

                String eventTime = eventDate.getText().toString();
                if(eventTime.equals("")){
                    showErrorDialog("Date cannot be blank!");
                    break;
                }

                double amount = Double.parseDouble(amountInput.getText().toString());
                if(amount == 0){
                    showErrorDialog("Amount cannot be blank!");
                    break;
                }

                String payerName = paidBy.getText().toString();
                if(payerName.equals("")){
                    showErrorDialog("Paid by field cannot be blank!");
                    break;
                }

                String paidToName = paidTo.getText().toString();
                if(paidToName.equals("")){
                    showErrorDialog("Paid for field cannot be blank!");
                    break;
                }

                String[] paidTos = paidToName.split(",");
                double memberCost = amount / paidTos.length;
                for(int i = 0; i < paidTos.length; i++){
                    //TODO: add memberCost to each paidTos[i]'s balance
                }

                //TODO: subtract amount from payerName's balance

                String imagePath = "";
                if(imageUri == null){
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.default_user_image);
                    imagePath = "default_user_image.png";
                    File file = new File(MainActivity.mDirPath + imagePath);
                    if (!file.exists()){
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    File old = new File(getRealPathFromURI(imageUri));
                    imagePath = old.getName();
                    File f = new File(MainActivity.mDirPath + imagePath);
                    if (!f.exists())
                    {
                        try {
                            f.createNewFile();
                            copyFile(old, f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //TODO: save latitude and longitude
                //insert item into Item Table
                ContentValues ctx = new ContentValues();
                ctx.put(AccountBookDatabase.KEY_ITEMNAME, itemname);
                ctx.put(AccountBookDatabase.KEY_DATE_ITEM, eventTime);
                ctx.put(AccountBookDatabase.KEY_VALUE, amount);
                ctx.put(AccountBookDatabase.KEY_BUYER, payerName);
                ctx.put(AccountBookDatabase.KEY_PHOTO_ITEM, photoPath);
                //ctx.put();

                finish();
                break;
        }
    }

    private void showErrorDialog(String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error:");
        alertDialog.setMessage(error);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        imageUri = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
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

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
