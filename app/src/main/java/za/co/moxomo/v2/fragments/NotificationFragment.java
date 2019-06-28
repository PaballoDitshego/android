package za.co.moxomo.v2.fragments;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.adapters.AlertListAdapter;
import za.co.moxomo.v2.adapters.NotificationsListAdapter;
import za.co.moxomo.v2.contentproviders.NotificationsContentProvider;
import za.co.moxomo.v2.databinding.FragmentNotificationBinding;
import za.co.moxomo.v2.helpers.SwipeToDeleteCallback;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.Notification;
import za.co.moxomo.v2.viewmodel.MainActivityViewModel;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;


public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private FragmentNotificationBinding binding;
    private NotificationsListAdapter notificationListAdapter;
    private MainActivityViewModel mainActivityViewModel;
    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent customTabsIntent;

    @Inject
    ViewModelFactory viewModelFactory;

    public NotificationFragment() { }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();

        return fragment;
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

        CustomTabsClient.bindCustomTabsService(getContext(), CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(ContextCompat.getColor(getContext(), R.color.action_color))
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);

        binding.notificationsList.setLayoutAnimation(animation);
        binding.notificationsList.setLayoutManager(layoutManager);
        binding.notificationsList.setItemViewCacheSize(20);
        binding.notificationsList.setLayoutAnimation(animation);

        binding.notificationsList.setItemAnimator(new DefaultItemAnimator());
        binding.notificationsList.addItemDecoration(new DividerItemDecoration(binding.notificationsList.getContext(), DividerItemDecoration.VERTICAL));
        notificationListAdapter = new NotificationsListAdapter(item -> {
            openUrlInBrowser(item);
        });

        notificationListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                binding.notificationsEmpty.setVisibility(notificationListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
        binding.notificationsList.setAdapter(notificationListAdapter);
        Log.d(TAG, "notifications is null" + (null == mainActivityViewModel.getNotifications()));
        mainActivityViewModel.getNotifications().observe(getActivity(), notifications -> {
            Log.d(TAG, "notifications " + notifications.toString());
            notificationListAdapter.submitList(notifications);
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.notifications_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Notifications");
            String[] menuItems = getResources().getStringArray(R.array.context_menu_items);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Uri deleteUri = ContentUris.withAppendedId(NotificationsContentProvider.CONTENT_URI, info.id);
        return true;
    }


    private void openUrlInBrowser(Notification notification) {
        customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,
                Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + getContext().getPackageName()));
        customTabsIntent.launchUrl(getContext(), Uri.parse(notification.getUrl()));
    }


    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                NotificationsListAdapter.ViewHolder alertListViewHolder = (NotificationsListAdapter.ViewHolder) viewHolder;
                Notification notification = alertListViewHolder.getBinding().getNotification();
                MainActivityViewModel.deleteNotification(notification);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.notificationsList);
    }


}
