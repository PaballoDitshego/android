package za.co.moxomo.v2.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.adapters.AutoSuggestAdapter;
import za.co.moxomo.v2.adapters.ViewPagerAdapter;
import za.co.moxomo.v2.databinding.ActivityMainBinding;
import za.co.moxomo.v2.enums.FragmentEnum;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.helpers.GpsUtils;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.repository.Repository;
import za.co.moxomo.v2.viewmodel.MainActivityViewModel;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;

import static za.co.moxomo.v2.fragments.HomePageFragment.CUSTOM_TAB_PACKAGE_NAME;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    @Inject
    ViewModelFactory viewModelFactory;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPagerAdapter viewPagerAdapter;
    private SearchView mSearchView;
    private MainActivityViewModel mainActivityViewModel;
    private ActivityMainBinding binding;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent customTabsIntent;
    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CursorAdapter cursorAdapter;
    private AutoSuggestAdapter locationsSuggestAdapter;
    private boolean isPostSearch;
    private boolean locationHasText;
    private Handler locationsHandler;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean hasLocation;

    private boolean viewpageInitialised = false;
    boolean hasGeoPermission;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Geocoder geocoder;

    private boolean isGPS;
    private String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationUpdate();

        geocoder = new Geocoder(this, Locale.ENGLISH);
        setActionBar();
        initialiseCustomeTabService();
        initialiseNavigationDrawer();
        setFloatingActionButton();
        setLocationAutoComplete();
        setLocation();
        getFcmToken();
        handleIntent(getIntent());
        Utility.changeStatusBarColor(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.search_button);
        setSearchView(search);
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

        //handle search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            mainActivityViewModel.getSearchString().setValue(!query.isEmpty() ? query : null);
            binding.viewpager.arrowScroll(View.FOCUS_LEFT);
            binding.viewpager.setCurrentItem(0);

        } else if (intent.getAction().equals(getString(R.string.PUSH_NOTIFICATION))) {
            Log.d(TAG, "Is push, setting current item to 1");
            binding.viewpager.setCurrentItem(1);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (cursorAdapter != null) {
            handleSuggestions(Collections.emptyList());
        }
        Utility.changeStatusBarColor(this);
        // startLocationUpdates();
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
        if (cursorAdapter != null) {
            handleSuggestions(Collections.emptyList());
        }
        mSearchView.setIconified(false);
        mSearchView.onActionViewCollapsed();

        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {

        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ApplicationConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "on reqyest");
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (Utility.locationServicesEnabled(this)) {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                Log.d(TAG, "latittude " + location.getLatitude());
                                Log.d(TAG, "longitude " + location.getLongitude());
                                Repository.LONGITUDE = location.getLongitude();
                                Repository.LATITUDE = location.getLatitude();
                                hasGeoPermission = true;
                                if (!viewpageInitialised) initialiseViewPager();
                            }
                        });
                    } else {
                        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
                            @Override
                            public void gpsStatus(boolean isGPSEnable) {
                                isGPS = isGPSEnable;

                            }
                        });
                    }
                } else {

                }
            }
        } else {
            if (!viewpageInitialised) initialiseViewPager();
        }
        if (!viewpageInitialised) initialiseViewPager();
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

    private void viewProfile() {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtra(FragmentEnum.VIEW_PROFILE.name(), FragmentEnum.VIEW_PROFILE.getFragmentId());
        intent.putExtra(getString(R.string.fragment_title), FragmentEnum.VIEW_PROFILE.getTitle());
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

    private void initialiseViewPager() {
        Log.d(TAG, "init viewpager");
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(viewPagerAdapter);
        binding.viewpager.setOffscreenPageLimit(2);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    handleSuggestions(Collections.emptyList());
                  //  binding.fab.setVisibility(View.INVISIBLE);
                } else {
                   // binding.fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        binding.tablayout.setupWithViewPager(binding.viewpager, true);
        binding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewpageInitialised = true;
    }

    private void setLocationAutoComplete() {
        locationsSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        mainActivityViewModel.getLocationSuggestions().observe(this, result -> {
            locationsSuggestAdapter.setData(result);
            locationsSuggestAdapter.notifyDataSetChanged();
        });
        binding.geolocation.setAdapter(locationsSuggestAdapter);
        binding.geolocation.setThreshold(2);

        binding.geolocation.setOnItemClickListener((parent, view, position, id) -> {
            binding.geolocation.setText(locationsSuggestAdapter.getObject(position));
            Repository.AREA = binding.geolocation.getText().toString();
            if (query != null) {
                mainActivityViewModel.getSearchString().setValue((!query.isEmpty()) ? query : null);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.geolocation.getWindowToken(), 0);
            }

        });
        binding.geolocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                locationsHandler.removeMessages(TRIGGER_AUTO_COMPLETE);
                locationsHandler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
                if (count > 0) {
                    Repository.filterByLocation = true;
                    locationHasText = true;
                    binding.clearText.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                } else {
                    Repository.filterByLocation = false;
                    Repository.AREA = null;
                    locationHasText = false;
                    binding.clearText.setBackgroundResource(R.drawable.ic_my_location_black_24dp);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        locationsHandler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (binding.geolocation.hasFocus() && !TextUtils.isEmpty(binding.geolocation.getText())) {
                    mainActivityViewModel.getLocationSuggestion(binding.geolocation.getText().toString());
                }
            }
            return false;
        });
        binding.clearText.setOnClickListener(v -> {
            if (locationHasText) {
                binding.geolocation.getText().clear();
            } else {
                if (Utility.locationServicesEnabled(v.getContext()) && ((ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))) {
                    if (fusedLocationProviderClient != null) {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                            try {
                                if (location != null) {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (!addresses.isEmpty()) {
                                        Address address = addresses.get(0);
                                        Repository.LATITUDE = address.getLatitude();
                                        Repository.LONGITUDE = address.getLongitude();

                                        if (null != address.getLocality()) {
                                            binding.geolocation.setText(address.getLocality().concat(", ").concat(address.getAdminArea()));
                                        } else {
                                            if (null != address.getSubAdminArea() && null != address.getAdminArea()) {
                                                binding.geolocation.setText(address.getSubAdminArea().concat(", ").concat(address.getAdminArea()));
                                                Repository.AREA = address.getSubAdminArea().concat(", ").concat(address.getAdminArea());
                                            } else {
                                                binding.geolocation.setText(address.getAddressLine(0));
                                                Repository.AREA = address.getAddressLine(0);
                                            }
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } /*else {
                    setLocation();
                }*/
            }
        });
    }


    private void initialiseNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_view_alerts:
                    viewAlerts();
                    break;
                case R.id.nav_profile:
                    viewProfile();
                    break;

                case R.id.nav_privacy_policy:
                    customTabsIntent.launchUrl(this, Uri.parse("https://moxomo.co.za/privacy_policy.html"));
                    break;
                case R.id.nav_rate_app:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=za.co.moxomo.v2")));
                    break;
                case R.id.nav_share_app:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Find jobs on the go! : https://play.google.com/store/apps/details?id=za.co.moxomo.v2");
                    intent.setType("text/plain");
                    startActivity(intent);
                default:
                    break;
            }
            binding.navDrawerLayout.closeDrawer(Gravity.LEFT, false);
            return false;
        });

    }

    private void initialiseCustomeTabService() {
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

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(this, R.color.action_color))
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build();
    }

    private void setFloatingActionButton() {
        binding.fabCreateAlert.setOnClickListener(view -> createAlert());
        binding.fabEditAlerts.setOnClickListener(view -> viewAlerts());
        binding.fab.setOnMenuButtonClickListener(view -> binding.fab.toggle(true));
        binding.fab.showMenuButton(true);
        binding.fab.setClosedOnTouchOutside(true);

    }

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                findViewById(R.id.cToolbar);
        collapsingToolbarLayout.setTitle("Title");
    }


    private void setSearchView(MenuItem search) {
        mSearchView =
                (SearchView) search.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setGravity(Gravity.LEFT);


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView.SearchAutoComplete searchEditText = mSearchView.findViewById(R.id.search_src_text);
        searchEditText.setNextFocusDownId(binding.geolocation.getId());
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        String[] columNames = {SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] viewIds = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.searchview_layout, null, columNames, viewIds, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchEditText.setPadding(-50, 2, 0, 2);
        searchEditText.setPaddingRelative(0, 0, 0, 2);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    query = searchEditText.getText().toString();
                    if (binding.geolocation.getText().length() > 0) {
                        if (query != null) {
                            Log.d(TAG, "query " + query);
                            Log.d(TAG, "location " + binding.geolocation.getText().toString());
                            Log.d(TAG, "lat " + Repository.LATITUDE);
                            Log.d(TAG, "lon " + Repository.LONGITUDE);
                            Log.d(TAG, "area " + Repository.AREA);

                            mainActivityViewModel.getSearchString().setValue(query);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(binding.geolocation.getWindowToken(), 0);
                        }
                    } else {
                        binding.geolocation.requestFocus();
                    }
                    handled = true;
                }
                return handled;
            }
        });


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
                        if (s.length() == 0) {
                            Repository.AREA = null;
                            Repository.SEARCH_STRING = null;
                            query = null;
                            handleSuggestions(Collections.emptyList());
                            binding.geolocation.getText().clear();
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
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                query = cursor.getString(cursor.getColumnIndexOrThrow("suggest_text_1"));
                SearchEvent searchEvent = new SearchEvent();
                searchEvent.putQuery(query);
                Answers.getInstance().logSearch(searchEvent);
                mSearchView.setQuery(query, false);
                searchEditText.setSelection(0);
                searchEditText.requestFocus();
                mSearchView.setNextFocusDownId(R.id.location);
                mSearchView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                if (binding.geolocation.getText().length() > 0) {
                    if (query != null) {
                        Repository.AREA = binding.geolocation.getText().toString();
                        mainActivityViewModel.getSearchString().setValue(!query.isEmpty() ? query : null);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(binding.geolocation.getWindowToken(), 0);
                    }
                } else {
                    binding.geolocation.requestFocus();
                }

                binding.viewpager.arrowScroll(View.FOCUS_LEFT);
                binding.viewpager.setCurrentItem(0);

                return true;
            }
        });

        binding.geolocation.
                setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (!hasFocus && isPostSearch) {
                            isPostSearch = false;
                            binding.geolocation.requestFocus();
                            binding.geolocation.clearFocus();
                        }
                    }
                });

        binding.geolocation.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Repository.AREA = binding.geolocation.getText().toString();
                query = searchEditText.getText().toString();
                if (null != query) {
                    mainActivityViewModel.getSearchString().setValue(query);
                }
                handled = true;
            }
            return handled;
        });

        mSearchView.setOnSearchClickListener(v -> {
            binding.viewpager.setCurrentItem(0);
        });

        mSearchView.setOnCloseListener(() -> {
            binding.viewpager.setCurrentItem(0);
            mSearchView.clearFocus();
            return true; //returning true will stop search view from collapsing
        });

    }


    private void handleSuggestions(List<String> suggestions) {
        Cursor cursor = createCursorFromResult(suggestions);
        cursorAdapter.changeCursor(cursor);
    }

    private boolean checkGooglePlayServices() {
        int checkGooglePlayServices = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, checkGooglePlayServices, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            return false;
        }
        return true;
    }

    private void getFcmToken() {
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


    private void setLocation() {
        Log.d(TAG, "setting loc");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasGeoPermission =true;
            if (null != fusedLocationProviderClient) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        hasGeoPermission = true;
                        Repository.LONGITUDE = location.getLongitude();
                        Repository.LATITUDE = location.getLatitude();
                        if (!viewpageInitialised) initialiseViewPager();
                    }
                });
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (!viewpageInitialised) initialiseViewPager();

    }


    private void requestLocationUpdate() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    hasLocation = true;
                    if (location != null) {
                        Log.d(TAG, "Getting loc updates");
                        Repository.LATITUDE = location.getLatitude();
                        Repository.LONGITUDE = location.getLongitude();
                        if (fusedLocationProviderClient != null) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            break;
                        }
                    }
                }
            }

            ;
        };
    }

    }

