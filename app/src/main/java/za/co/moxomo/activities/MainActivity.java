package za.co.moxomo.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import za.co.moxomo.R;
import za.co.moxomo.adapters.VacancyListAdapter;
import za.co.moxomo.adapters.ViewPagerAdapter;
import za.co.moxomo.databinding.ActivityMainBinding;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.events.SearchEvent;
import za.co.moxomo.fragments.DetailPageFragment;
import za.co.moxomo.fragments.HomePageFragment;
import za.co.moxomo.fragments.SearchResultsFragment;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.helpers.PageTransformer;

import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.viewmodel.ViewModelFactory;
import za.co.moxomo.viewmodel.MainActivityViewModel;


public class MainActivity extends AppCompatActivity implements HomePageFragment.OnHomeListInteractionListener,
        DetailPageFragment.OnApplyButtonInteractionListener,
        SearchResultsFragment.OnSearchListInteractionListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private ViewPagerAdapter mAdapter;
    private final EventBus bus = EventBus.getDefault();
    private Stack<Integer> mBackStack;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private MainActivityViewModel mainActivityViewModel;
    private ActivityMainBinding binding;
    private InjectionComponent injectionComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mBackStack = mainActivityViewModel.getmBackStack();


        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        binding.viewpager.setPageTransformer(false, new PageTransformer(this));
        binding.viewpager.setOffscreenPageLimit(2);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(mAdapter);

        handleIntent(getIntent());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search = menu.findItem(R.id.search_button);
        //  mSearchView =
        //  (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_button));
        mSearchView =
                (SearchView) search.getActionView();
        mSearchView.setOnSearchClickListener(v -> {
                    if (binding.viewpager.getOffscreenPageLimit() < 3) {
                        binding.viewpager.setOffscreenPageLimit(3);
                        if (mAdapter.getCount() < 4) {
                            mAdapter.setCount(4);
                            mAdapter.notifyDataSetChanged();
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
        Collections.addAll(integerArrayList, array);

        savedInstanceState.putIntegerArrayList("back_stack", integerArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
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

        String query = null;
        //handle search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            mainActivityViewModel.getSearchString().postValue(!query.isEmpty() ? query : null);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri detailUri = intent.getData();
            query = detailUri.getLastPathSegment();

        }
        if (query != null) {
            if (binding.viewpager.getCurrentItem() != 3) {
                getBackStack().add(binding.viewpager.getCurrentItem());
            }
            bus.post(new SearchEvent(query));

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            //  binding.viewpager.setCurrentItem(0);
            return true;
        }
        if (id == R.id.action_account) {
            //starts account activity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_notifications) {
            if (binding.viewpager.getCurrentItem() != 2) {
                getBackStack().add(binding.viewpager.getCurrentItem());
            }
            binding.viewpager.setCurrentItem(2);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onHomeListInteraction(Long id) {

        //this will call the getEntry(id) method in DetailPageFragment and show page
        getBackStack().add(binding.viewpager.getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));


    }

    @Override
    public void onApplyButtonInteraction(String url) {

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);


    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
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
            binding.viewpager.setCurrentItem(previousItem);
        }

        // }
    }


    @Override
    public void onSearchListInteraction(Long id) {
        //implements inteface defined in SearchResultsFragment.java
        getBackStack().add(binding.viewpager.getCurrentItem()); //add current page to custom backstack
        bus.post(new DetailViewEvent(id));
    }

    public ViewPager getPager() {
        return binding.viewpager;
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

