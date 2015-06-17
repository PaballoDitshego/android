package za.co.moxomo;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

import de.greenrobot.event.EventBus;
import za.co.moxomo.events.BrowserViewEvent;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.events.PageBackEvent;
import za.co.moxomo.events.SearchEvent;


public class MainActivity extends AppCompatActivity implements TimeLineFragment.OnSearchInteractionListener,
        DetailViewFragment.OnFragmentInteractionListener, SearchFragment.OnSearchItemInteractionListener {



    private PageAdapter mAdapter;
    private ViewPager mViewPager;
    private EventBus bus = EventBus.getDefault();
    private Stack<Integer> backStack = new Stack<>();
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        handleIntent(getIntent());
        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_button));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

       changeSearchViewTextColor(searchView);




        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        Integer[] array = new Integer[getBackStack().size()];
        getBackStack().copyInto(array);
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (Integer i : integerArrayList) {
            integerArrayList.add(array[i]);
        }

        savedInstanceState.putIntegerArrayList("back_stack", integerArrayList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if (getBackStack().empty()) {
            ArrayList<Integer> values = savedInstanceState.getIntegerArrayList("back_stack");
            getBackStack().addAll(values);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(mViewPager.getCurrentItem() !=3) {
                backStack.add(mViewPager.getCurrentItem());
            }
            bus.post(new SearchEvent(query));

        }
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
        if(id==android.R.id.home){
            mViewPager.setCurrentItem(0);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onFragmentInteraction(Long id) {

        //this will call the getEntry(id) method in DetailViewFragment and show page
        backStack.add(mViewPager.getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));


    }

    @Override
    public void onFragmentInteraction(String url) {
        bus.post(new BrowserViewEvent(url));
    }


    public Stack<Integer> getBackStack() {
        return backStack;
    }

    @Override
    public void onBackPressed() {
        //Handles back button navigation
        if (mViewPager.getCurrentItem() == 2) {
          //workaround to enable back on the
           bus.post(new PageBackEvent());
        }
        else {
            if (getBackStack().empty()) {
                super.onBackPressed(); //exits application
            } else {
                int previousItem = getBackStack().pop();
                mViewPager.setCurrentItem(previousItem);

            }

        }
    }


    public ProgressBar getmProgressBar() {
        return mProgressBar;
    }


    public ViewPager getPager() {

        return mViewPager;
    }



    @Override

    public void onSearchInteraction(Long id) {
        //implements inteface defined in SearchFragment.java
        backStack.add(mViewPager.getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));
    }


    //gets editText of searchView and changes the color to white
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }}
