package za.co.moxomo.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.clans.fab.FloatingActionButton;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import za.co.moxomo.R;
import za.co.moxomo.adapters.ViewPagerAdapter;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.databinding.ActivityMainBinding;
import za.co.moxomo.viewmodel.MainActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;


public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private ViewPagerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private MainActivityViewModel mainActivityViewModel;
    private ActivityMainBinding binding;
    private InjectionComponent injectionComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        mProgressBar = findViewById(R.id.progress_spinner);

        binding.viewpager.setOffscreenPageLimit(2);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(mAdapter);
        binding.tablayout.setupWithViewPager(binding.viewpager, true);
        binding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.fabCreateAlert.setOnClickListener(view -> {

        });
        binding.fabEditAlerts.setOnClickListener(view->{

        });
        binding.fab.setOnMenuButtonClickListener(view->{
            binding.fab.toggle(true);
        });
        binding.fab.showMenuButton(true);
        binding.fab.setClosedOnTouchOutside(true);

        handleIntent(getIntent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search = menu.findItem(R.id.search_button);
        mSearchView =
                (SearchView) search.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setGravity(Gravity.LEFT);
        SearchView.SearchAutoComplete searchEditText = mSearchView.findViewById(R.id.search_src_text);
        searchEditText.setPadding(-80, 2, 0, 2);
        searchEditText.setPaddingRelative(0, 0, 0, 2);


        mSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() < 1) {
                    mainActivityViewModel.getSearchString().setValue(null);
                }
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter c = mSearchView.getSuggestionsAdapter();
                Cursor cur = c.getCursor();
                cur.moveToPosition(position);
                String query = cur.getString(position);
                mainActivityViewModel.getSearchString().setValue(!query.isEmpty() ? query : null);
                mSearchView.setQuery(query, false);
                mSearchView.clearFocus();
                binding.viewpager.arrowScroll(View.FOCUS_LEFT);
                return false;
            }
        });
        mSearchView.clearFocus();
        mSearchView.setOnSearchClickListener(v -> {
            binding.viewpager.setCurrentItem(0);
        });

        mSearchView.setOnCloseListener(() -> {
            return true; //returning true will stop search view from collapsing
        });

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String query;
        //handle search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            mainActivityViewModel.getSearchString().setValue(!query.isEmpty() ? query : null);
            binding.viewpager.arrowScroll(View.FOCUS_LEFT);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri detailUri = intent.getData();
            query = detailUri.getLastPathSegment();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        mSearchView.setIconified(false);
        mSearchView.onActionViewCollapsed();
        super.onBackPressed();
    }

}

