package za.co.moxomo.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.MoxomoApplication;
import za.co.moxomo.R;
import za.co.moxomo.adapters.VacancyListAdapter;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.databinding.FragmentHomepageBinding;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.helpers.Utility;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.viewmodel.MainActivityViewModel;
import za.co.moxomo.viewmodel.ViewModelFactory;


public class HomePageFragment extends Fragment  {

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    CompositeDisposable compositeDisposable;


    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static final String TAG = HomePageFragment.class.getSimpleName();

    private FragmentHomepageBinding binding;
    private VacancyListAdapter vacancyListAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent customTabsIntent;
    private Bitmap actionBack;


    public HomePageFragment() {
    }

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;
                mClient.warmup(0L);
                mCustomTabsSession = mClient.newSession(null);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };

        decodeBitmap(R.drawable.ic_action_back_24dp);
        CustomTabsClient.bindCustomTabsService(getContext(), CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(getContext(), R.color.action_color))
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setCloseButtonIcon(actionBack)
                .setShowTitle(true)
                .build();
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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.list.setLayoutAnimation(animation);
        binding.list.setLayoutManager(layoutManager);
        binding.list.setItemViewCacheSize(20);
        binding.list.setLayoutAnimation(animation);
        binding.list.setItemAnimator(new DefaultItemAnimator());

        vacancyListAdapter = new VacancyListAdapter(item ->
            openUrlInBrowser(item));

        vacancyListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                if (positionStart == 0) {
                    layoutManager.scrollToPosition(0);
                }
            }
        });
        binding.list.setAdapter(vacancyListAdapter);
        mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            mainActivityViewModel.getVacancyClassDatasourceFactory().getMutableLiveData().getValue().pullToRefresh();
        });

        mainActivityViewModel.getVacancies().observe(getActivity(), vacancies->{
            vacancyListAdapter.submitList(vacancies);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.list.addItemDecoration(new DividerItemDecoration(binding.list.getContext(), DividerItemDecoration.VERTICAL));

        });
        mainActivityViewModel.getSearchString().observe(getActivity(), searchString -> {
            mainActivityViewModel.getVacancyClassDatasourceFactory()
                    .getMutableLiveData()
                    .getValue()
                    .setSearchString(searchString);
        });

        mainActivityViewModel.getProgressLoadStatus().observe(getActivity(), status -> {
            vacancyListAdapter.setNetworkState(status);
            if (Objects.requireNonNull(status).equalsIgnoreCase(ApplicationConstants.LOADING)) {
            } else if (status.equalsIgnoreCase(ApplicationConstants.LOADED)) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        mainActivityViewModel.getResultSetSize().observe(getActivity(), results ->{
            Toast toast=  Toast.makeText(getContext(),results + " Vacancies",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 350);
            toast.show();
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

    private void openUrlInBrowser(Vacancy vacancy) {
        customTabsIntent.launchUrl(getContext(), Uri.parse(vacancy.getUrl()));
    }

    private void decodeBitmap(final int resource) {
        Utility.decodeBitmap(getContext(), resource).doOnSubscribe(disposable -> {
                    compositeDisposable.add(disposable);
                }
        ).subscribe((Bitmap result) -> {
            if (resource == R.drawable.ic_action_back_24dp) {
                actionBack = result;
            }

        });


    }
}

