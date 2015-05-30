package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class AccountViewAdapter extends ArrayAdapter<Account> {
    private final List<Account> accounts;
    public AccountViewAdapter(Context context, int resource, List<Account> accounts) {
        super(context, resource, accounts);
        this.accounts = accounts;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.account_list_item, null);
        TextView textView = (TextView) row.findViewById(R.id.account_list_name);
        textView.setText(accounts.get(position).getName());
        ImageView imageView = (ImageView) row.findViewById(R.id.account_list_picture);
        int target = parent.getHeight();
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(MainActivity.mDirPath + accounts.get(position).getPic()), target, target);
        imageView.setImageBitmap(thumbImage);

        return row;
    }
}