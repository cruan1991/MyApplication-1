package com.example.myapplication;

import java.util.ArrayList;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;



@SuppressLint("NewApi")
public class AccountDetail extends FragmentActivity implements TabListener, OnPageChangeListener
{

    //   TabHost mTabHost;
    private ActionBar mActionBar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<View> mViews;
    private ArrayList<ActionBar.Tab> mTabs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayUseLogoEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mTabs=new ArrayList<ActionBar.Tab>();

        ActionBar.Tab tab0 = mActionBar.newTab();
        tab0.setText("Current Account");
        mTabs.add(tab0);
        mActionBar.addTab(tab0);

        ActionBar.Tab tab1 = mActionBar.newTab();
        tab0.setText("Members");
        mTabs.add(tab1);
        mActionBar.addTab(tab1);

        mViewPager=(ViewPager) findViewById(R.id.ViewPager);
        mViews= new ArrayList<View>();
        mViews.add(LayoutInflater.from(this).inflate(R.layout.layout_0, null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.layout_1, null));

        mAdapter=new ViewPagerAdapter(mViews);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onTabReselected(Tab mTab, FragmentTransaction arg1)
    {
    }

    @Override
    public void onTabSelected(Tab mTab, FragmentTransaction arg1)
    {
        if(mViewPager!=null)
        {
            mViewPager.setCurrentItem(mTab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(Tab mTab, FragmentTransaction arg1)
    {

    }


    @Override
    public void onPageScrollStateChanged(int arg0)
    {

    }


    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onPageSelected(int Index)
    {

        mViewPager.setCurrentItem(Index);

        mActionBar.selectTab(mTabs.get(Index));
    }



}
