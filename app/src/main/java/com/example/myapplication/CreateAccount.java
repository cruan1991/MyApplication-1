package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private SimpleDateFormat dateFormatter;
    private String photoPath;

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
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ImageView groupPic = (ImageView) findViewById(R.id.groupPic);
                        groupPic.setImageBitmap(selectedImage);
                        photoPath = imageUri.toString();
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
                newMember.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                Button remove = new Button(this);
                remove.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                remove.setText("remove");
                LinearLayout ll = new LinearLayout(this);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(newMember);
                ll.addView(remove);
                LinearLayout membersLayout = (LinearLayout) findViewById(R.id.membersLayout);
                membersLayout.addView(ll);
                members.add(newMember);
                remove.setOnClickListener(new RemoveButtonListener(ll, member));
                break;
            case R.id.submit:
                String name = groupName.getText().toString();
                String startTime = startDate.getText().toString();

                //insert account into Account Table
                ContentValues ctx = new ContentValues();
                ctx.put(AccountBookDatabase.KEY_ACCTNAME, name);
                ctx.put(AccountBookDatabase.KEY_DATE_ACCT, startTime);
                ctx.put(AccountBookDatabase.KEY_LASTUPDATE, startTime);
                ctx.put(AccountBookDatabase.KEY_PHOTO_ACCT, photoPath);
                MainActivity.ABD.insert(AccountBookDatabase.Account_table, ctx);

                ctx.clear();
                Cursor c = MainActivity.ABD.query("SELECT last_insert_rowid()"); //this returns the id of the account just inserted
                c.moveToFirst();
                //store common key,value pairs for all members
                ctx.put(AccountBookDatabase.KEY_ACCTID_MEMBER, c.getInt(0));
                ctx.put(AccountBookDatabase.KEY_BALANCE, 0);
                for(int i = 0; i < members.size(); i++){
                    String member = members.get(i).getText().toString();

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
}
