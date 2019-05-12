package za.co.moxomo.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.MoxomoApplication;
import za.co.moxomo.R;
import za.co.moxomo.adapters.AlertListAdapter;
import za.co.moxomo.helpers.SwipeToDeleteCallback;
import za.co.moxomo.databinding.FragmentViewAlertsBinding;
import za.co.moxomo.model.Alert;
import za.co.moxomo.viewmodel.AlertActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;

public class ViewAlertsFragment extends Fragment {

    private static final String TAG = ViewAlertsFragment.class.getSimpleName();
    private AlertActivityViewModel alertActivityViewModel;
    private FragmentViewAlertsBinding binding;
    private AlertListAdapter alertListAdapter;

    @Inject
    CompositeDisposable compositeDisposable;
    @Inject
    ViewModelFactory viewModelFactory;

    public ViewAlertsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
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
        setHasOptionsMenu(true);
        NavController navController = Navigation.findNavController(view);
        alertListAdapter = new AlertListAdapter(item -> {
            alertActivityViewModel.getAlertObjectHolder().setValue(item);
            navController.navigate(R.id.editAlertFragment);
        });

        alertListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                checkEmpty();
                if (positionStart == 0) {
                    layoutManager.scrollToPosition(0);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                binding.alertsEmpty.setVisibility(alertListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });
        /**/
        alertActivityViewModel.getAlerts().observe(getActivity(), alerts -> {
            alertListAdapter.submitList(alerts);
        });

        binding.alertsList.setLayoutAnimation(animation);
        binding.alertsList.setLayoutManager(layoutManager);
        binding.alertsList.setItemViewCacheSize(20);
        binding.alertsList.setLayoutAnimation(animation);
        binding.alertsList.setAdapter(alertListAdapter);
        enableSwipeToDelete();

        binding.alertsList.setItemAnimator(new DefaultItemAnimator());


        binding.alertsList.addItemDecoration(new DividerItemDecoration(binding.alertsList.getContext(), DividerItemDecoration.VERTICAL));
        binding.fab.setOnMenuButtonClickListener(fab -> {
            navController.navigate(R.id.createAlertFragment);
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view_alert_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            alertActivityViewModel.deleteAllAlerts();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                AlertListAdapter.ViewHolder alertListViewHolder = (AlertListAdapter.ViewHolder) viewHolder;
                Alert alert = alertListViewHolder.getBinding().getAlert();
                alertActivityViewModel.deleteAlert(alert);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.alertsList);
    }


}
