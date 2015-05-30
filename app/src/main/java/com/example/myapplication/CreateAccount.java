package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;

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
import java.util.Locale;

public class CreateAccount extends Activity {
    private final int SELECT_PHOTO = 1;
    private EditText groupName;
    private EditText startDate;
    private EditText member;
    private ArrayList<EditText> members;
    private LinearLayout membersLayout;
    private SimpleDateFormat dateFormatter;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        initializeViews();
    }

    private void initializeViews() {
        groupName = (EditText) findViewById(R.id.groupNameInput);

        startDate = (EditText) findViewById(R.id.startDatePicker);
        startDate.setInputType(InputType.TYPE_NULL);

        member = (EditText) findViewById(R.id.member1);
        members = new ArrayList<EditText>();
        members.add(member);

        membersLayout = (LinearLayout) findViewById(R.id.membersLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clearAccount) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        ImageView groupPic = (ImageView) findViewById(R.id.groupPic);
                        groupPic.setImageBitmap(selectedImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.startDatePicker:
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        startDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.groupPicPicker:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.addMemberLayout:
                EditText newMember = new EditText(this);
                newMember.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                Button remove = new Button(this);
                remove.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                remove.setText("remove");
                LinearLayout ll = new LinearLayout(this);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(newMember);
                ll.addView(remove);
                membersLayout.addView(ll);
                members.add(newMember);
                remove.setOnClickListener(new RemoveButtonListener(ll, member));
                break;
            case R.id.submit:
                String name = groupName.getText().toString();
                if(name.equals("")){
                    showErrorDialog("Group name cannot be blank!");
                    break;
                }

                String startTime = startDate.getText().toString();
                if(startTime.equals("")){
                    showErrorDialog("Start time cannot be blank!");
                    break;
                }

                //TODO: check if account name is used before. if used, showErrorDialog("This account name already exist.");

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

                //insert account into Account Table
                ContentValues ctx = new ContentValues();
                ctx.put(AccountBookDatabase.KEY_ACCTNAME, name);
                ctx.put(AccountBookDatabase.KEY_DATE_ACCT, startTime);
                ctx.put(AccountBookDatabase.KEY_LASTUPDATE, startTime);
                ctx.put(AccountBookDatabase.KEY_PHOTO_ACCT, imagePath);
                MainActivity.ABD.insert(AccountBookDatabase.Account_table, ctx);

                ctx.clear();
                Cursor c = MainActivity.ABD.query("SELECT last_insert_rowid()"); //this returns the id of the account just inserted
                c.moveToFirst();
                //store common key,value pairs for all members
                ctx.put(AccountBookDatabase.KEY_ACCTID_MEMBER, c.getInt(0));
                ctx.put(AccountBookDatabase.KEY_BALANCE, 0);

                for(int i = 0; i < members.size(); i++){
                    String member = members.get(i).getText().toString();
//TODO: only insert if member.equals("") is not true, if all member are "", error message: showErrorDialog("Members cannot be blank!");
                    //insert members into Member Table
                    ctx.put(AccountBookDatabase.KEY_MEMBERNAME, member);
                    MainActivity.ABD.insert(AccountBookDatabase.Member_table, ctx);
                    ctx.remove(AccountBookDatabase.KEY_MEMBERNAME); // removes the member name that's already been inserted
                }

                finish();
                break;
        }
    }

    private class RemoveButtonListener implements View.OnClickListener {
        private LinearLayout ll;
        private EditText member;

        public RemoveButtonListener(LinearLayout ll, EditText member) {
            this.ll = ll;
            this.member = member;
        }

        @Override
        public void onClick(View v) {
            ((LinearLayout) ll.getParent()).removeView(ll);
            members.remove(member);
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
