package za.co.moxomo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import za.co.moxomo.enums.FragmentEnum;
import za.co.moxomo.MoxomoApplication;
import za.co.moxomo.R;
import za.co.moxomo.databinding.ActivityAlertBinding;
import za.co.moxomo.helpers.Utility;


public class AlertActivity extends AppCompatActivity {

    private ActivityAlertBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_alert );
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utility.changeStatusBarColor(this);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R  .navigation.alert_activity_navigation);
        navController.addOnNavigatedListener((controller, destination) -> {
            switch (destination.getId()){
                case R.id.createAlertFragment:
                    toolbar.setTitle(FragmentEnum.CREATE_ALERT.getTitle());
                    break;
                case R.id.viewAlertsFragment:
                    toolbar.setTitle(FragmentEnum.VIEW_ALERT.getTitle());
                    break;
                case R.id.editAlertFragment:
                    toolbar.setTitle(FragmentEnum.EDIT_ALERT.getTitle());
                    break;
                default:
                    toolbar.setTitle(FragmentEnum.VIEW_ALERT.getTitle());

            }
        });

        if (getIntent().hasExtra(FragmentEnum.CREATE_ALERT.name())) {
            graph.setStartDestination(FragmentEnum.CREATE_ALERT.getFragmentId());
            toolbar.setTitle(getIntent().getStringExtra(getString(R.string.fragment_title)));
        } else {
            graph.setStartDestination(FragmentEnum.VIEW_ALERT.getFragmentId());
            toolbar.setTitle(getIntent().getStringExtra(getString(R.string.fragment_title)));
        }
        //navController.setGraph(graph, bundle)
        navController.setGraph(graph);


    }

/*    @Override
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
    }*/


    @Override
    public void onBackPressed() {
        Utility.hideKeyboard(this);
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.changeStatusBarColor(this);
      //  checkPlayServices();
    }
}


