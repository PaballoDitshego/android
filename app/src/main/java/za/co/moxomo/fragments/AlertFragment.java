package za.co.moxomo.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

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
        alertActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(AlertActivityViewModel.class);
        NavController navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        binding.registerBtn.setOnClickListener(view -> {
            //  navController.navigate(R.);

        });

        return binding.getRoot();

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
        alertActivityViewModel.getAlertId().observe();



    }

}
