package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;


public class AccountList extends ActionBarActivity {

    private AccountViewAdapter mAdapter;
    private static ArrayList<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        ListView listView = (ListView) findViewById(R.id.listView);
        //TODO: get all accounts from database
        accounts = new ArrayList<>();
        String query = "select acctname, acctphoto from account";
        Cursor cursor = MainActivity.ABD.query(query);
        int count;

        if(cursor != null){
            count = cursor.getCount();
            //Move the current record pointer to the first row of the table
            cursor.moveToFirst();
        } else {
            count = 0;
        }

        for(int i = 0; i < count; i++){
            String accountName = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_ACCTNAME));
            String accountPhotoPath = cursor.getString(cursor.getColumnIndex(AccountBookDatabase.KEY_PHOTO_ACCT));
            accounts.add(new Account(accountName, accountPhotoPath));
            cursor.moveToNext();
        }

        mAdapter = new AccountViewAdapter(this, R.layout.account_list_item, accounts);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent singleAccountIntent = new Intent(getApplicationContext(), AccountDetail.class); //change activity name here
                singleAccountIntent.putExtra("name", accounts.get(position).getName());
                startActivity(singleAccountIntent);
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogWindow(position);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogWindow(int position) {
        final int pos = position;
        TextView msg = new TextView(this);
        msg.setText(R.string.delete_message);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog
        // characteristics
        builder.setView(msg);

        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: delete table from database
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
