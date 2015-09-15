package za.co.moxomo;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import za.co.moxomo.adapters.ViewPagerAdapter;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.events.SearchEvent;


public class MainActivity extends AppCompatActivity implements HomePageFragment.OnHomeListInteractionListener,
        DetailPageFragment.OnApplyButtonInteractionListener,
        SearchResultsFragment.OnSearchListInteractionListener {


    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private EventBus bus = EventBus.getDefault();
    private Stack<Integer> mBackStack = new Stack<>();
    private ProgressBar mProgressBar;
    private SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(false, new PageTransformer(this));
        mViewPager.setOffscreenPageLimit(2);


        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);

        handleIntent(getIntent());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_button));
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (getPager().getOffscreenPageLimit() < 3) {
                                                         getPager().setOffscreenPageLimit(3);
                                                         if (mAdapter.getCount() < 4) {
                                                             mAdapter.setCount(4);
                                                             mAdapter.notifyDataSetChanged();
                                                         }
                                                     }

                                                 }
                                             }

        );

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        changeSearchViewTextColor(mSearchView);



        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        //save backstack values
        Integer[] array = new Integer[getBackStack().size()];
        getBackStack().copyInto(array);
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            integerArrayList.add(array[i]);
        }

        savedInstanceState.putIntegerArrayList("back_stack", integerArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //restore backstack
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

        String query = null;
        //handle search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (getPager().getOffscreenPageLimit() < 3) {
                getPager().setOffscreenPageLimit(3);
                if (mAdapter.getCount() < 4) {
                    mAdapter.setCount(4);
                    mAdapter.notifyDataSetChanged();
                }
            }
            query = intent.getStringExtra(SearchManager.QUERY);


        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri detailUri = intent.getData();
            query = detailUri.getLastPathSegment();

        }
        if (query != null) {
            if (getPager().getCurrentItem() != 3) {
                getBackStack().add(mViewPager.getCurrentItem());
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
        if (id == android.R.id.home) {
            getPager().setCurrentItem(0);
            return true;
        }
        if (id == R.id.action_account) {
            //starts account activity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_notifications) {
            if (getPager().getCurrentItem() != 2) {
                getBackStack().add(mViewPager.getCurrentItem());
            }
            getPager().setCurrentItem(2);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onHomeListInteraction(Long id) {

        //this will call the getEntry(id) method in DetailPageFragment and show page
        getBackStack().add(mViewPager.getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));


    }

    @Override
    public void onApplyButtonInteraction(String url) {

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);


    }


    public Stack<Integer> getBackStack() {
        return mBackStack;
    }

    @Override
    public void onBackPressed() {


        mSearchView.setIconified(true);
        mSearchView.onActionViewCollapsed();
        if (getBackStack().empty()) {
            super.onBackPressed(); //exits application
        } else {
            int previousItem = getBackStack().pop();
            getPager().setCurrentItem(previousItem);
        }

        // }
    }


    public ProgressBar getProgressBar() {
        return mProgressBar;
    }


    public ViewPager getPager() {
        return mViewPager;
    }


    @Override
    public void onSearchListInteraction(Long id) {
        //implements inteface defined in SearchResultsFragment.java
        getBackStack().add(getPager().getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));
    }


    /*
     Changes searchview text color to white
     */
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
    }

}

