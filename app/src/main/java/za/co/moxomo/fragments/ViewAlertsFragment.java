package za.co.moxomo.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.R;
import za.co.moxomo.adapters.AlertListAdapter;
import za.co.moxomo.adapters.VacancyListAdapter;
import za.co.moxomo.databinding.FragmentViewAlertsBinding;
import za.co.moxomo.viewmodel.AlertActivityViewModel;
import za.co.moxomo.viewmodel.MainActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;

public class ViewAlertsFragment extends Fragment {


    private AlertActivityViewModel alertActivityViewModel;
    private FragmentViewAlertsBinding binding;

    @Inject
    CompositeDisposable compositeDisposable;
    @Inject
    ViewModelFactory viewModelFactory;


    public ViewAlertsFragment() {
    }


    public static ViewAlertsFragment newInstance() {
        return new ViewAlertsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_alerts, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        int resId = R.anim.layout_animation_fall_down;

        alertActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(AlertActivityViewModel.class);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        NavController navController = Navigation.findNavController(view);
        AlertListAdapter alertListAdapter = new AlertListAdapter(item -> {

        });

        alertListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                if (positionStart == 0) {
                    layoutManager.scrollToPosition(0);
                }
            }
        });

        binding.alertsList.setLayoutAnimation(animation);
        binding.alertsList.setLayoutManager(layoutManager);
        binding.alertsList.setItemViewCacheSize(20);
        binding.alertsList.setLayoutAnimation(animation);

        binding.alertsList.setItemAnimator(new DefaultItemAnimator());
        binding.alertsList.addItemDecoration(new DividerItemDecoration(binding.alertsList.getContext(), DividerItemDecoration.VERTICAL));
        binding.fab.setOnMenuButtonClickListener(fab -> {
            navController.navigate(R.id.createAlertFragment);
        });




    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
