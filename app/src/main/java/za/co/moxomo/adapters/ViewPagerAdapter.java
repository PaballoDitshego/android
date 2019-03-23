package za.co.moxomo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import za.co.moxomo.fragments.HomePageFragment;
import za.co.moxomo.fragments.NotificationFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM = 2;


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomePageFragment.newInstance();
            case 1:
                return NotificationFragment.newInstance();
        }
        return HomePageFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "Notifications";
                default:
                return null;
        }
   }


    @Override
    public int getCount() {
        return NUM;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


}