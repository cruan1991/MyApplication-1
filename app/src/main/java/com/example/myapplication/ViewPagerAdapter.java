package com.example.myapplication;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //定义三个Fragment的索引
    public static final int Fragment_Index_0=0;
    public static final int Fragment_Index_1=1;


    public ViewPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int Index)
    {
        Fragment mFragemnt=null;
        switch(Index)
        {
            case Fragment_Index_0:
                mFragemnt=new Fragment_0();
                break;
            case Fragment_Index_1:
                mFragemnt=new Fragment_1();
                break;

        }
        return mFragemnt;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

}
