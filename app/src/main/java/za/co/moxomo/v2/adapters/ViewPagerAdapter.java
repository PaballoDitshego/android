package za.co.moxomo.v2.adapters;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import za.co.moxomo.v2.fragments.HomePageFragment;
import za.co.moxomo.v2.fragments.NotificationFragment;

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
                return "Jobs";
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