package za.co.moxomo.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import za.co.moxomo.R;
import za.co.moxomo.activities.NotificationActivity;
import za.co.moxomo.adapters.NotificationsCursorAdapter;
import za.co.moxomo.contentproviders.NotificationsContentProvider;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.helpers.FontCache;


public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private EventBus bus = EventBus.getDefault();
    private SQLiteDatabase db;
    private NotificationsCursorAdapter notificationsAdapter;
    private ListView mListView;
    private CursorLoader cursorLoader;
    private LoaderManager loadermanager;


    public NotificationFragment() {
        // Required empty public constructor
    }


    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadermanager = getLoaderManager();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        mListView = (ListView) view.findViewById(R.id.notification_list);
        mListView.setEmptyView(view.findViewById(R.id.notifications_empty));
        view.findViewById(R.id.notification_loading).setVisibility(View.INVISIBLE);

        notificationsAdapter = new NotificationsCursorAdapter(getActivity(),
                null,
                0);
        mListView.setAdapter(notificationsAdapter);
        loadermanager.initLoader(1, null, this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long arg) {

                Cursor cursor = getActivity().getContentResolver().query(
                        Uri.withAppendedPath(NotificationsContentProvider.CONTENT_URI,
                                String.valueOf(arg)), null, null, null, null);


                cursor.moveToPosition(position);
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String action_string = cursor.getString(cursor.getColumnIndexOrThrow("action_string"));
                cursor.close();

                TextView title = (TextView) view.findViewById(R.id.title);
                title.setTypeface(Typeface.DEFAULT);

                switch (type) {
                    case ApplicationConstants.ACTION_JOB_ALERT:
                        bus.post(new DetailViewEvent(Long.parseLong(action_string)));
                        break;
                    case ApplicationConstants.ACTION_NEWS_ALERT:
                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        intent.putExtra("url", action_string);
                        getActivity().startActivity(intent);

                        break;
                    default:
                        break;
                }


            }
        });

        registerForContextMenu(mListView);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(getActivity(),
                Uri.parse("content://za.co.moxomo.Notifications/notifications"),
                null, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.notification_list) {
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
        getActivity().getContentResolver().delete(deleteUri, "_id=" + info.id, null);
        Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (notificationsAdapter != null && data != null)
            notificationsAdapter.swapCursor(data); //swap the new cursor in.
        else
            Log.v("", "OnLoadFinished: notificationsAdapter is null");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (notificationsAdapter != null)
            notificationsAdapter.swapCursor(null);
        else
            Log.v("", "OnLoadFinished: notificationsAdapter is null");
    }


}
