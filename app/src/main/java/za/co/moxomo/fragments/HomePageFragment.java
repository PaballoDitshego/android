package za.co.moxomo.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import za.co.moxomo.R;
import za.co.moxomo.adapters.VacancyListAdapter;
import za.co.moxomo.databinding.FragmentHomepageBinding;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.viewmodel.ViewModelFactory;
import za.co.moxomo.viewmodel.MainActivityViewModel;


public class HomePageFragment extends Fragment {


    @Inject
    ViewModelFactory viewModelFactory;

    private OnHomeListInteractionListener mListener;
    private FragmentHomepageBinding binding;
    private VacancyListAdapter vacancyListAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private InjectionComponent injectionComponent;

    private EventBus bus = EventBus.getDefault();

    public HomePageFragment() {
    }

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final android.support.v7.widget.LinearLayoutManager layoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);

        binding.list.setLayoutManager(layoutManager);
        binding.list.setItemViewCacheSize(20);
        binding.list.setDrawingCacheEnabled(true);
        binding.list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.list.setItemAnimator(new DefaultItemAnimator());
        vacancyListAdapter = new VacancyListAdapter();
        vacancyListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                if (positionStart == 0) {
                    layoutManager.scrollToPosition(0);
                }
            }
        });
        mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
        mainActivityViewModel.getProgressLoadStatus().observe(getActivity(), status -> {
            if (Objects.requireNonNull(status).equalsIgnoreCase(ApplicationConstants.LOADING)) {

                binding.progress.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase(ApplicationConstants.LOADED)) {
                binding.progress.setVisibility(View.GONE);
            }
        });

        mainActivityViewModel.getVacancies().observe(getActivity(), vacancyListAdapter::submitList);
        binding.list.setAdapter(vacancyListAdapter);

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            mListener = (OnHomeListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHomeListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnHomeListInteractionListener {

        void onHomeListInteraction(Long id);
    }




}

