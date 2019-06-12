package za.co.moxomo.v2.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import io.fabric.sdk.android.Fabric;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.adapters.ViewPagerAdapter;
import za.co.moxomo.v2.databinding.ActivityMainBinding;
import za.co.moxomo.v2.enums.FragmentEnum;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.viewmodel.MainActivityViewModel;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;

import static za.co.moxomo.v2.fragments.HomePageFragment.CUSTOM_TAB_PACKAGE_NAME;


public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPagerAdapter mAdapter;
    private SearchView mSearchView;
    private MainActivityViewModel mainActivityViewModel;
    private ActivityMainBinding binding;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent customTabsIntent;
    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                mClient.warmup(0L);
                mCustomTabsSession = mClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };
        Utility.changeStatusBarColor(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);

        binding.viewpager.setOffscreenPageLimit(2);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(mAdapter);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    handleSuggestions(Collections.emptyList());
                    binding.fab.setVisibility(View.INVISIBLE);
                } else {
                    binding.fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        binding.tablayout.setupWithViewPager(binding.viewpager, true);
        binding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.fabCreateAlert.setOnClickListener(view -> createAlert());
        binding.fabEditAlerts.setOnClickListener(view -> viewAlerts());
        binding.fab.setOnMenuButtonClickListener(view -> binding.fab.toggle(true));

        binding.fab.showMenuButton(true);
        binding.fab.setClosedOnTouchOutside(true);

        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_view_alerts:
                    viewAlerts();
                    break;
                case R.id.nav_create_alert:
                    createAlert();
                    break;
                case R.id.nav_privacy_policy:
                    customTabsIntent.launchUrl(this, Uri.parse("https://www.moxomo.co.za/privacy_policy.html"));
                default:
                    break;

            }
            binding.navDrawerLayout.closeDrawer(Gravity.LEFT, false);
            return false;
        });
        handleIntent(getIntent());

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)

                .setToolbarColor(ContextCompat.getColor(this, R.color.action_color))
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = task.getResult().getToken();
                    mainActivityViewModel.sendFCMToken(token, Utility.getFcmTokenInSharedPref(getApplicationContext()));
                    Utility.storeFcmTokenInSharedPref(getApplicationContext(), token);
                });
        createNotificationChannel();
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
        String [] columNames = { SearchManager.SUGGEST_COLUMN_TEXT_1 }; int [] viewIds = { android.R.id.text1 };
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.searchview_layout, null, columNames, viewIds, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchEditText.setPadding(-80, 2, 0, 2);
        searchEditText.setPaddingRelative(0, 0, 0, 2);
        mSearchView.setSuggestionsAdapter(cursorAdapter);
        mSearchView.clearFocus();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (s.length() < 2) {
                            handleSuggestions(Collections.emptyList());
                            mainActivityViewModel.getSearchString().setValue(null);
                        }
                        if (s.length() < 2) {
                            return false;
                        }
                        if (s.length() > 2) {
                            mainActivityViewModel.getKeywordSuggestion(s);
                        }
                        return false;
                    }
                });

        mainActivityViewModel.getKeywordSuggestions().observe(this, suggestions -> {
            handleSuggestions(suggestions);
        });

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                String query = cursor.getString(cursor.getColumnIndexOrThrow("suggest_text_1"));
                mainActivityViewModel.getSearchString().setValue(!query.isEmpty() ? query : null);

                mSearchView.setQuery(query, false);
                mSearchView.clearFocus();

                binding.viewpager.arrowScroll(View.FOCUS_LEFT);
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(v -> {
            binding.viewpager.setCurrentItem(0);
        });

        mSearchView.setOnCloseListener(() -> {

            mSearchView.clearFocus();
            return true; //returning true will stop search view from collapsing
        });


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
    public void onResume() {
        super.onResume();
        if(cursorAdapter != null) {
            handleSuggestions(Collections.emptyList());
        }
        Utility.changeStatusBarColor(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            binding.viewpager.setCurrentItem(0, true);
            binding.navDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(cursorAdapter !=null) {
            handleSuggestions(Collections.emptyList());
        }
        mSearchView.setIconified(false);
        mSearchView.onActionViewCollapsed();
        super.onBackPressed();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Moxomo";
            String description = "Alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ALERTS", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createAlert() {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra(FragmentEnum.CREATE_ALERT.name(), FragmentEnum.CREATE_ALERT.getFragmentId());
        intent.putExtra(getString(R.string.fragment_title), FragmentEnum.CREATE_ALERT.getTitle());
        startActivity(intent);
    }

    private void viewAlerts() {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra(FragmentEnum.VIEW_ALERT.name(), FragmentEnum.VIEW_ALERT.getFragmentId());
        intent.putExtra(getString(R.string.fragment_title), FragmentEnum.VIEW_ALERT.getTitle());
        startActivity(intent);

    }

    private Cursor createCursorFromResult(List<String> suggestions) {
        String[] menuCols = new String[]{BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA};

        MatrixCursor cursor = new MatrixCursor(menuCols);
        int counter = 0;

        for (String suggestion : suggestions) {
            cursor.addRow(new Object[]{counter, suggestion, suggestion});
            counter++;
        }

        return cursor;
    }


    private void handleSuggestions(List<String> suggestions){
        Cursor cursor = createCursorFromResult(suggestions);
        cursorAdapter.changeCursor(cursor);
    }


}

