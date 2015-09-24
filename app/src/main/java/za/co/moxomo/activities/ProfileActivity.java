package za.co.moxomo.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import za.co.moxomo.R;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.helpers.Utility;
import za.co.moxomo.helpers.VolleyApplication;


public class ProfileActivity extends AppCompatActivity {


    private static final String REG_ID = "regId";
    private static final String EMAIL_ID = "eMailId";
    private static final String FULL_NAME = "fullName";
    private static final String KEYWORDS = "push_strings";
    private static final String PROVINCE = "province";
    private static final String PUSH_STATE = "push_state";
    private ProgressBar mProgressBar;
    private GoogleCloudMessaging gcmObj;
    private Context applicationContext;
    private String regId = "";
    private String email;
    private String names;
    private String keywords;
    private String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        applicationContext = getApplicationContext();
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        final String[] titles = getResources().
                getStringArray(R.array.titles_array);
        ArrayAdapter title_adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, titles);
        String[] province_array = {"Gauteng", "Mpumalanga", "Limpopo", "NorthWest", "Free State", "KwaZulu Natal",
                "Northern Cape", "Eastern Cape", "Western Cape"};
        ArrayAdapter province_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, province_array);
        final EditText full_names = (EditText) findViewById(R.id.fullname);
        final EditText email_address = (EditText) findViewById(R.id.email);
        final AutoCompleteTextView province = (AutoCompleteTextView) findViewById(R.id.province);
        province.setAdapter(province_adapter);
        final MultiAutoCompleteTextView title_strings = (MultiAutoCompleteTextView) findViewById(R.id.titles);


        title_strings.setAdapter(title_adapter);
        title_strings.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        final Button button = (Button) findViewById(R.id.account_button);
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        if (prefs.contains(REG_ID)) {
            full_names.setText(prefs.getString(FULL_NAME, ""));
            email_address.setText(prefs.getString(EMAIL_ID, ""));
            province.setText(prefs.getString(PROVINCE, ""));
            title_strings.setText(prefs.getString(KEYWORDS, ""));
            button.setText("Update Profile");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                names = full_names.getText().toString().trim();
                email = email_address.getText().toString().trim();
                location = province.getText().toString().trim();
                keywords = title_strings.getText().toString().trim();
                RegisterUser(v);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);

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
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void RegisterUser(View view) {
        String emailID = email;
        String fullNames = names;
        String keyword = keywords;
        String area = location;

        if (!TextUtils.isEmpty(emailID) && Utility.validate(emailID)) {
            if (checkPlayServices()) {
                mProgressBar.setVisibility(View.VISIBLE);
                registerInBackground(emailID, fullNames, area, keyword);
            }
        }
        // When Email is invalid
        else {
            Toast.makeText(applicationContext, "Please enter valid email",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void registerInBackground(final String emailID, final String fullName, final String area, final String keywords) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }

                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJECT_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {

                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg.contains("Error")) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
                if (!TextUtils.isEmpty(regId)) {
                    storeRegIdinSharedPref(applicationContext, regId, emailID, fullName, area, keywords);
                    Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID, String fullNames, String area, String keywords) {

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sentTokenToServer", false).apply();
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.putString(FULL_NAME, fullNames);
        editor.putString(PROVINCE, area);
        editor.putString(KEYWORDS, keywords);
        editor.putBoolean(PUSH_STATE, true);

        editor.commit();
        storeRegIdinServer();

    }

    private void storeRegIdinServer() {

        String url = ApplicationConstants.APP_SERVER_URL;
        JSONObject user = new JSONObject();
        try {

            user.put("full_name", names);
            user.put("reg_id", regId);
            user.put("email", email);
            user.put("province", location);
            user.put("keywords", keywords);
        } catch (JSONException e) {

        }


        JsonObjectRequest request = new JsonObjectRequest(
                url, user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            Long id = jsonObject.getLong("id");
                            SharedPreferences prefs = getSharedPreferences("UserDetails",
                                    Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("id", id);
                            editor.commit();
                            mProgressBar.setVisibility(View.INVISIBLE);


                        } catch (Exception e) {
                            mProgressBar.setVisibility(View.INVISIBLE);


                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mProgressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private boolean checkPlayServices() {
        final int status = GooglePlayServicesUtil

                .isGooglePlayServicesAvailable(applicationContext);

        if (status != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {

                final Dialog d = GooglePlayServicesUtil.getErrorDialog(status,

                        this, status);

                d.setCancelable(false);

                d.show();

            }

            return false;

        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}


