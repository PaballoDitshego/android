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
import za.co.moxomo.viewmodel.MainActivityViewModel;
import za.co.moxomo.viewmodel.ProfileActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;


public class AlertFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    CompositeDisposable compositeDisposable;

    private FragmentAlertBinding binding;
    private InjectionComponent injectionComponent;
    private ProfileActivityViewModel profileActivityViewModel;

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

        binding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_alert);
        profileActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProfileActivityViewModel.class);
        NavController navController = Navigation.findNavController(binding.getRoot());


        // Inflate the layout for this fragment

        return binding.getRoot() ;

    }

}
