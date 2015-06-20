package za.co.moxomo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Paballo Ditshego on 5/19/15.
 */
public class PageAdapter extends FragmentStatePagerAdapter {


    private int NUM = 4;


    public PageAdapter(FragmentManager fm) {

        super(fm);


    }


    @Override

    public Fragment getItem(int position) {

       if(position ==0){
           return TimeLineFragment.newInstance();

       }
        if(position==1){
            return DetailViewFragment.newInstance();
        }
        if(position==2){
            return WebFragment.newInstance();
        }
        if(position==3){
            return SearchFragment.newInstance();
        }
        else {
            return TimeLineFragment.newInstance();
        }



    }

    @Override

    public int getCount() {
        return NUM;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container,  position,  object);
    }






}