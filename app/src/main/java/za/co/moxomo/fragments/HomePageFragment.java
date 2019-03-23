package za.co.moxomo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
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


public class HomePageFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    CompositeDisposable compositeDisposable;


    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    private FragmentHomepageBinding binding;
    private VacancyListAdapter vacancyListAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private InjectionComponent injectionComponent;
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
        injectionComponent = DaggerInjectionComponent.builder().build();
        injectionComponent.inject(this);

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
        final android.support.v7.widget.LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        binding.list.setLayoutAnimation(animation);
        binding.list.setLayoutManager(layoutManager);
        binding.list.setItemViewCacheSize(20);
        binding.list.setDrawingCacheEnabled(true);
        binding.list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.list.setLayoutAnimation(animation);

        binding.list.setItemAnimator(new DefaultItemAnimator());
        binding.list.addItemDecoration(new DividerItemDecoration(binding.list.getContext(), DividerItemDecoration.VERTICAL));

        vacancyListAdapter = new VacancyListAdapter(item -> {
            openUrlInBrowser(item);
        });

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
        binding.list.setAdapter(vacancyListAdapter);
        mainActivityViewModel.getVacancies().observe(getActivity(), vacancyListAdapter::submitList);
        mainActivityViewModel.getSearchString().observe(getActivity(), searchString -> {
            mainActivityViewModel.getVacancyClassDatasourceFactory()
                    .getMutableLiveData()
                    .getValue()
                    .setSearchString(searchString);
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

