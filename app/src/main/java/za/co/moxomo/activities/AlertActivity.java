package za.co.moxomo.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import za.co.moxomo.R;
import za.co.moxomo.databinding.ActivityAlertBinding;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.helpers.ApplicationConstants;


public class AlertActivity extends AppCompatActivity {

    private ActivityAlertBinding binding;
    private InjectionComponent injectionComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_alert );

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.str_create_alert));
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




    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID, String fullNames, String area, String keywords) {

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sentTokenToServer", false).apply();
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
      /*  editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.putString(FULL_NAME, fullNames);
        editor.putString(PROVINCE, area);
        editor.putString(KEYWORDS, keywords);
        editor.putBoolean(PUSH_STATE, true);*/

        editor.commit();
       //// storeRegIdinServer();

    }



    @Override
    protected void onResume() {
        super.onResume();
      //  checkPlayServices();
    }
}


