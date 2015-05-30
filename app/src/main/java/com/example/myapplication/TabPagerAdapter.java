package com.example.myapplication;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class TabPagerAdapter extends PagerAdapter
{

    public static final int Fragment_Index_0=0;
    public static final int Fragment_Index_1=1;



    private ArrayList<View> mViews;
    public TabPagerAdapter(ArrayList<View> mViews)
    {
        this.mViews=mViews;
    }

    @Override
    public void destroyItem(View container, int position, Object object)
    {
        ((ViewPager)container).removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position)
    {
        ((ViewPager)container).addView(mViews.get(position), 0);
        return mViews.get(position);
    }

    @Override
    public int getCount()
    {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View mView, Object mObject)
    {
        return (mView==mObject);
    }



}
