package za.co.moxomo.v2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import za.co.moxomo.v2.enums.FragmentEnum;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.databinding.FragmentEditAlertBinding;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.ApiResponse;
import za.co.moxomo.v2.service.Status;
import za.co.moxomo.v2.viewmodel.AlertActivityViewModel;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;

public class EditAlertFragment extends Fragment {

    private static final String TAG = EditAlertFragment.class.getSimpleName();
    @Inject
    ViewModelFactory viewModelFactory;

    private NavController navController;
    private FragmentEditAlertBinding binding;
    private AlertActivityViewModel alertActivityViewModel;

    public EditAlertFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_alert, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        alertActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(AlertActivityViewModel.class);
        navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        alertActivityViewModel.getProgressLiveStatus().observe(getViewLifecycleOwner(), status->{
            if (Objects.requireNonNull(status).equalsIgnoreCase(ApplicationConstants.LOADING )) {
                binding.progress.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase(ApplicationConstants.LOADED)) {
                binding.progress.setVisibility(View.INVISIBLE);
            }
        });
        alertActivityViewModel.getAlertObjectHolder().observe(getViewLifecycleOwner(), alert -> {

            binding.setAlert(alert);
        });
        alertActivityViewModel.getAlertServiceResponse().observe(getViewLifecycleOwner(), result -> {
            if(!result.isHasBeenHandled()) {
                processAlertUpdateResponse(result.getContentIfNotHandled());
            }
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
            updateAlert();
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateAlert() {
        Alert.AlertBuilder alertDTOBuilder = Alert.builder();
        if (null != binding.keyword.getText() || binding.keyword.getText().length() > 0) {
            alertDTOBuilder.keyword(binding.keyword.getText().toString());
        }
        if (null != binding.location.getText() || binding.location.getText().length() > 0) {
            alertDTOBuilder.location(binding.location.getText().toString());
        }
        if (null != binding.mobileNumber.getText() || binding.mobileNumber.getText().length() > 0) {
            alertDTOBuilder.mobileNumber(binding.mobileNumber.getText().toString());
        }
        if (validateInput()) {
            Utility.storeMobileNumberInSharedPref(getContext(),binding.mobileNumber.toString());
            alertActivityViewModel.updateAlert(alertDTOBuilder.build());
        }

    }

    private void processAlertUpdateResponse(ApiResponse apiResponse) {
        if (null != apiResponse.status && apiResponse.status.equals(Status.SUCCESS)) {
            Snackbar.make(binding.getRoot(), "Alert Updated.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> navController.navigate(FragmentEnum.VIEW_ALERT.getFragmentId())).show();
        }else {
            final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Error updating alert, try again later.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", v -> snackbar.dismiss());
            snackbar.show();
        }
            alertActivityViewModel.getAlertServiceResponse().setValue(null);
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
        binding.keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.titleInputLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) { }
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
