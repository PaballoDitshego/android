package za.co.moxomo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import za.co.moxomo.fragments.DetailPageFragment;
import za.co.moxomo.fragments.HomePageFragment;
import za.co.moxomo.fragments.NotificationFragment;
import za.co.moxomo.fragments.SearchResultsFragment;

/**
 * Created by Paballo Ditshego on 5/19/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    private int NUM = 3;
    private ArrayList<Fragment> pagerList = new ArrayList<>();


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);



    }


    @Override

    public Fragment getItem(int position) {


        //   return  pagerList.get(position);

        if (position == 0) {
            return HomePageFragment.newInstance();

       }
        if(position==1){
            return DetailPageFragment.newInstance();
        }
        if(position==2){
            return NotificationFragment.newInstance();
        }
        if(position==3){
            return SearchResultsFragment.newInstance();
        } else {
            return HomePageFragment.newInstance();
        }



    }

    public void addFragment(Fragment fragment) {
        pagerList.add(fragment);
    }

    @Override

    public int getCount() {
        //  return pagerList.size();
        return NUM;

    }

    public void setCount(int num) {
        this.NUM = num;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container,  position,  object);
    }






}