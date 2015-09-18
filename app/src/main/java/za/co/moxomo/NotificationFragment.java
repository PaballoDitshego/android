package za.co.moxomo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import za.co.moxomo.adapters.NotificationsCursorAdapter;
import za.co.moxomo.events.DetailViewEvent;


public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private EventBus bus = EventBus.getDefault();
    // private SQLiteDatabase db;
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
        TextView tv = (TextView) view.findViewById(R.id.notification_text);
        Typeface tf = FontCache.get("Roboto-Bold.ttf", getActivity());

        tv.setTypeface(tf);
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
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Uri deleteUri = ContentUris.withAppendedId(NotificationsContentProvider.CONTENT_URI, id);

                getActivity().getContentResolver().delete(deleteUri, "_id=" + id, null);
                Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
