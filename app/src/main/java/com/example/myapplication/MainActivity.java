package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    private static final int REQUEST_CREATE_ITEM = 1;
    private static final int REQUEST_CREATE_ACTIVITY = 2;
    // Database
    public static AccountBookDatabase ABD;
    public static String mDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ABD = new AccountBookDatabase(getApplicationContext());

        File parentDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        mDirPath = parentDir.getAbsoluteFile() + "/MyApplication/";
        File storageDir = new File(mDirPath);

        if(!storageDir.exists() || !storageDir.isDirectory()){
            storageDir.mkdirs();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewItem(View view) {
        Intent intent = new Intent(this,CreateItem.class);
        startActivityForResult(intent, REQUEST_CREATE_ITEM);
    }

    public void createNewActivity(View view) {
        Intent intent = new Intent(this,CreateAccount.class);
        startActivityForResult(intent, REQUEST_CREATE_ACTIVITY);
    }

    public void viewActivities(View view) {
        Intent intent = new Intent(this,AccountList.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case REQUEST_CREATE_ITEM:
                if(resultCode == RESULT_OK){
                    Toast.makeText(getApplicationContext(),
                            "Item successfully created.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CREATE_ACTIVITY:
                if(resultCode == RESULT_OK){
                    Toast.makeText(getApplicationContext(),
                            "Activity successfully created.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
