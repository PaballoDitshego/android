package za.co.moxomo.v2.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.v2.enums.FragmentEnum;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.adapters.AutoSuggestAdapter;
import za.co.moxomo.v2.databinding.FragmentCreateAlertBinding;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.ApiResponse;
import za.co.moxomo.v2.service.Status;
import za.co.moxomo.v2.viewmodel.AlertActivityViewModel;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;

public class CreateAlertFragment extends Fragment {

    private static final String TAG = CreateAlertFragment.class.getSimpleName();

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    CompositeDisposable compositeDisposable;

    private FragmentCreateAlertBinding binding;
    private AlertActivityViewModel alertActivityViewModel;
    private NavController navController;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler locationsHandler;
    private Handler keywordsHandler;
    private AutoSuggestAdapter locationsSuggestAdapter;
    private AutoSuggestAdapter keywordSuggestAdapter;

    public CreateAlertFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_alert, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        alertActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(AlertActivityViewModel.class);
        navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        locationsSuggestAdapter = new AutoSuggestAdapter(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        keywordSuggestAdapter = new AutoSuggestAdapter(getActivity(),
                android.R.layout.simple_dropdown_item_1line);

        alertActivityViewModel.getProgressLiveStatus().observe(getViewLifecycleOwner(), status -> {
            if (Objects.requireNonNull(status).equalsIgnoreCase(ApplicationConstants.LOADING)) {
                binding.progress.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase(ApplicationConstants.LOADED)) {
                binding.progress.setVisibility(View.INVISIBLE);
            }
        });
        alertActivityViewModel.getAlertServiceResponse().observe(getActivity(), result -> {
            if (!result.isHasBeenHandled()) {
                processAlertCreationResponse(result.getContentIfNotHandled());
            }
        });
        alertActivityViewModel.getLocationSuggestions().observe(getActivity(), result -> {
            locationsSuggestAdapter.setData(result);
            locationsSuggestAdapter.notifyDataSetChanged();
        });
        alertActivityViewModel.getKeywordSuggestions().observe(getActivity(), result -> {
            keywordSuggestAdapter.setData(result);
            keywordSuggestAdapter.notifyDataSetChanged();
        });
        setHasOptionsMenu(true);
        initBinding();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_edit_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_fragment) {
            Utility.hideKeyboard(getActivity());
            createAlert();
        }
        return super.onOptionsItemSelected(item);
    }


    private void createAlert() {
        Alert.AlertBuilder alertBuilder = Alert.builder();
        if (binding.keyword.getText().length() > 0) {
            alertBuilder.keyword(binding.keyword.getText().toString());
        }
        if (binding.location.getText().length() > 0) {
            alertBuilder.location(binding.location.getText().toString());
        }
        if (binding.location.getText().length() == 0) {
            alertBuilder.location(getString(R.string.default_location));
        }
        if (binding.mobileNumber.getText().length() > 0) {
            alertBuilder.mobileNumber(binding.mobileNumber.getText().toString());
        }
        alertBuilder.push(binding.push.isChecked());
        alertBuilder.sms(binding.sms.isChecked());
        String gcmToken = Utility.getFcmTokenInSharedPref(getContext());
        alertBuilder.gcmToken(gcmToken);

        if (validateInput()) {
            Utility.storeMobileNumberInSharedPref(getContext(), binding.mobileNumber.getText().toString());
            Log.d(TAG, "alert {} " + alertBuilder.build().toString());
            Alert alert = alertBuilder.build();
            alertActivityViewModel.createAlert(alert);
        }
    }

    private void processAlertCreationResponse(ApiResponse apiResponse) {
        if (null != apiResponse.status && apiResponse.status.equals(Status.SUCCESS)) {
            navController.navigate(FragmentEnum.VIEW_ALERT.getFragmentId());
        } else {
            final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Error creating alert, try again later.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", v -> snackbar.dismiss());
            snackbar.show();
        }
    }

    private boolean validateInput() {
        if (binding.keyword.getText().toString().isEmpty()) {
            binding.titleInputLayout.setError("Please enter  valid job keyword,company or skill");
            return false;
        }
        if (binding.sms.isChecked() && binding.mobileNumber.getText().toString().isEmpty()) {
            binding.mobileNumberInputLayout.setError("Enter a  valid number or disable sms notifications");
            return false;
        }
        if (!binding.mobileNumber.getText().toString().isEmpty() && !Utility.validateMsisdn(binding.mobileNumber.getText().toString())) {
            binding.mobileNumberInputLayout.setError("Enter a  valid number or disable sms notifications");
            return false;
        }
        return true;
    }

    private void initBinding() {
        Alert alert = new Alert();
        alert.setMobileNumber(Utility.getMobileNumberInSharedPref(getContext()));
        binding.setAlert(alert);
        binding.keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.titleInputLayout.setError(null);
                keywordsHandler.removeMessages(TRIGGER_AUTO_COMPLETE);
                keywordsHandler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.keyword.setAdapter(keywordSuggestAdapter);
        binding.keyword.setThreshold(2);

        binding.keyword.setOnItemClickListener((parent, view, position, id) -> {
            binding.keyword.setText(keywordSuggestAdapter.getObject(position));
        });

        binding.location.setAdapter(locationsSuggestAdapter);
        binding.location.setThreshold(2);


        binding.location.setOnItemClickListener((parent, view, position, id) -> {
            binding.location.setText(locationsSuggestAdapter.getObject(position));
        });
        binding.location.addTextChangedListener(new TextWatcher() {
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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        locationsHandler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (binding.location.hasFocus() && !TextUtils.isEmpty(binding.location.getText())) {
                    alertActivityViewModel.getLocationSuggestion(binding.location.getText().toString());
                }
            }
            return false;
        });

        keywordsHandler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (binding.keyword.hasFocus() && !TextUtils.isEmpty(binding.keyword.getText())) {
                    alertActivityViewModel.getKeywordSuggestion(binding.keyword.getText().toString());
                }
            }
            return false;

        });


        binding.mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.mobileNumberInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.sms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                binding.push.setChecked(true);
            }
        });
        binding.push.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                binding.sms.setChecked(true);
            }
        });
    }

}
