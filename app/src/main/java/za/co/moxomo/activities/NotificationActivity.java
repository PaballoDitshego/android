package za.co.moxomo.activities;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import za.co.moxomo.R;
import za.co.moxomo.databasehelpers.NotificationDatabaseHelper;
import za.co.moxomo.fragments.DetailPageFragment;
import za.co.moxomo.fragments.WebViewFragment;
import za.co.moxomo.helpers.ApplicationConstants;

/**
 * The activity that handles pendingintents, also used as the webview activity
 */


public class NotificationActivity extends AppCompatActivity implements DetailPageFragment.OnApplyButtonInteractionListener {

    private WebViewFragment webViewFragment;
    private DetailPageFragment detailPageFragment;
    private FragmentManager fm = getSupportFragmentManager();


    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();


        NotificationDatabaseHelper helper = new NotificationDatabaseHelper(this);
        if (intent.hasExtra("notification")) {

            long row_id = intent.getLongExtra("row_id", 0);
            Cursor cursor = helper.getNotification(row_id);
            String type = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.TYPE));
            String action_string = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.ACTION_STRING));
            cursor.close();

            switch (type) {

                case ApplicationConstants.ACTION_NEWS_ALERT:
                    helper.updateNotification(row_id, "read");
                    loadInBrowser(action_string);
                    break;

                case ApplicationConstants.ACTION_JOB_ALERT:
                    helper.updateNotification(row_id, "read");
                    if (detailPageFragment == null) {
                        detailPageFragment = DetailPageFragment.newInstance();
                    }

                    fm.beginTransaction().addToBackStack("df").add(R.id.frame, detailPageFragment).commit();
                    detailPageFragment.getEntry(Long.parseLong(action_string));
                    break;

                default:
                    break;
            }

        } else {

            String url = intent.getStringExtra("url");
            loadInBrowser(url);
        }


    }

    @Override
    public void onApplyButtonInteraction(String url) {
        if (webViewFragment == null) {
            webViewFragment = WebViewFragment.newInstance(url);
            webViewFragment.setRetainInstance(true);
        }
        fm.beginTransaction().addToBackStack("wv").replace(R.id.frame, webViewFragment).commit();


    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }


    private void loadInBrowser(String url) {
        if (webViewFragment == null) {
            webViewFragment = WebViewFragment.newInstance(url);
            webViewFragment.setRetainInstance(true);
        }
        fm.beginTransaction().addToBackStack("wv").add(R.id.frame, webViewFragment).commit();


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentById(R.id.frame) instanceof WebViewFragment) {
            WebViewFragment webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
            if (webViewFragment.getWebView().canGoBack()) {
                webViewFragment.getWebView().goBack();


            }


        }
    }
}