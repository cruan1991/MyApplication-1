package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    // Database
    public static AccountBookDatabase ABD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ABD = new AccountBookDatabase(getApplicationContext());
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
        startActivity(intent);
    }

    public void createNewActivity(View view) {
        Intent intent = new Intent(this,CreateAccount.class);
        startActivity(intent);
    }

    public void viewActivities(View view) {
        Intent intent = new Intent(this,AccountList.class);
        startActivity(intent);
    }
}
