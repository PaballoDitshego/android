package za.co.moxomo.fragments;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.R;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.databinding.FragmentAlertBinding;
import za.co.moxomo.model.AlertDTO;
import za.co.moxomo.service.ApiResponse;
import za.co.moxomo.service.Status;
import za.co.moxomo.viewmodel.AlertActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;


public class AlertFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    CompositeDisposable compositeDisposable;

    private FragmentAlertBinding binding;
    private InjectionComponent injectionComponent;
    private AlertActivityViewModel alertActivityViewModel;
    private NavController navController;

    public AlertFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alert, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        alertActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(AlertActivityViewModel.class);
        navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        binding.registerBtn.setOnClickListener(btn -> {
            navController.navigate(R.id.mainActivity);
            // createAlert();
        });
        alertActivityViewModel.getAlertCreationResponse().observe(getActivity(), result -> {
            processAlerCreationResponse(result);
        });
    }


    private void createAlert() {
        AlertDTO.AlertDTOBuilder alertDTOBuilder = AlertDTO.builder();
        if (null != binding.jobTitle.getText() || binding.jobTitle.getText().length() > 0) {
            alertDTOBuilder.tags(binding.jobTitle.getText().toString());
        }
        if (null != binding.company.getText() || binding.company.getText().length() > 0) {
            alertDTOBuilder.tags(binding.company.getText().toString());
        }
        if (null != binding.location.getText() || binding.location.getText().length() > 0) {
            alertDTOBuilder.tags(binding.location.getText().toString());
        }
        if (null != binding.mobileNumer.getText() || binding.mobileNumer.getText().length() > 0) {
            alertDTOBuilder.tags(binding.mobileNumer.getText().toString());
        }
        alertDTOBuilder.sms(binding.push.getText()
                .toString())
                .sms(binding.sms.getText().toString());
        alertActivityViewModel.createAlert(alertDTOBuilder.build());

    }

    private void processAlerCreationResponse(ApiResponse apiResponse) {
        if (apiResponse.status.equals(Status.SUCCESS)) {
            navController.navigate(R.id.mainActivity);
        }

    }

}
