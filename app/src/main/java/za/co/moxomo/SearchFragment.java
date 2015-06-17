package za.co.moxomo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import za.co.moxomo.events.SearchEvent;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSearchItemInteractionListener}
 * interface.
 */
public class SearchFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {

    //service url
    private static String URL = "https://moxomoapp.appspot.com/_ah/api/vacancyEndpoint/v1.1/search";


    private String mQuery;
    private String mNext_Cursor;
    private OnSearchItemInteractionListener mListener;
    private ListView mListView;
    private EventBus mEventBus = EventBus.getDefault();
    private TextView mSearchText;
    private  boolean restoreMode =false;
    private int threshold =10;


    /**
     * The Adapter which will be used to populate the ListView with
     * Views.
     */
    private MoxomoListAdapter mAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }


    public static SearchFragment newInstance() {


        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);


        if (savedInstanceState != null) {
            if (mListView != null) {
                if (mAdapter != null) {
                    List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("values"));
                    if (values != null) {
                        mAdapter.updateList(values);
                    }
                }
                //    mListView.onRestoreInstanceState(savedInstanceState.getParcelable("list_state"));
                mNext_Cursor = savedInstanceState.getString("next_cursor");
            }
        }
        mAdapter = new MoxomoListAdapter(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        List<Vacancy> list = mAdapter.getList();
        //  savedState.putParcelable("list_state", mListView.onSaveInstanceState());
        savedState.putString("next_cursor", mNext_Cursor);
        savedState.putParcelable("values", Parcels.wrap(list));



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (mAdapter != null && mAdapter.getCount() == 0) {
                List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("values"));
                if (values != null) {
                    restoreMode =true; //prevent app from calling network operations
                    mAdapter.updateList(values);
                }
            }
            //   mListView.onRestoreInstanceState(savedInstanceState.getParcelable("list_state"));
            mNext_Cursor = savedInstanceState.getString("next_cursor");
            restoreMode =false;
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mListView = (ListView) view.findViewById(R.id.search_list);
        mListView.setOnScrollListener(this);
        view.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        mSearchText = (TextView) view.findViewById(R.id.search_text);
        mSearchText.setVisibility(View.VISIBLE);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchItemInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onSearchInteraction(mAdapter.getItemId(position));
        }
    }




    public void onEvent(SearchEvent event) {
       //
        MainActivity activity = (MainActivity) getActivity();
        activity.getPager().setCurrentItem(3);
        queryString(event.getQuery());
    }


    public void queryString(String query) {

        mQuery = query;
        mNext_Cursor = null;

       if(!restoreMode) {
           fetch(mQuery);
       }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - threshold) {
                if (mNext_Cursor != null && !mNext_Cursor.equals("EOR")) {
                    fetchMore(mQuery, mNext_Cursor);
                }
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void fetch(String query) {
        //initial fetch
        final MainActivity activity = (MainActivity) getActivity();
        activity.getmProgressBar().setVisibility(View.VISIBLE);
        mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
        mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.INVISIBLE);
        mSearchText.setText("Searching for: " + mQuery);
        String url;

            try {
                query = URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.d(this.getClass().getName(), e.getMessage());
            }
            url = URL + "?query=" + query;


        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            mSearchText.setText("Search results for: " + mQuery);
                            mAdapter.updateList(parse(jsonObject));
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mListView.setSelection(0);

                        } catch (JSONException e) {
                            mSearchText.setText("Search results for: " + mQuery);
                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mAdapter.clearList();
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.VISIBLE);

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        activity.getmProgressBar().setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        if (mListView.getCount() == 0) {
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.VISIBLE);

                        }
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private void fetchMore(String query, String cursor) {

        final MainActivity activity = (MainActivity) getActivity();
        activity.getmProgressBar().setVisibility(View.VISIBLE);

        String url;
        if (cursor != null) {

            try {
                query = URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.d(getClass().getName(), e.getMessage());
            }

            url = URL + "?query=" + query + "&cursor=" + mNext_Cursor;
        } else {
            try {
                query = URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.d(getClass().getName(), e.getMessage());
            }
            url = URL + "?query=" + query;
        }


        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mAdapter.addMore(parse(jsonObject));

                        } catch (JSONException e) {
                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        activity.getmProgressBar().setVisibility(View.INVISIBLE);
                        mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                        //  Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private List<Vacancy> parse(JSONObject json) throws JSONException {
        ArrayList<Vacancy> records = new ArrayList<>();


        if(json.has("nextPageToken")) {
            mNext_Cursor = json.getString("nextPageToken");
        }
        JSONArray vacancies = json.getJSONArray("items");

        for (int i = 0; i < vacancies.length(); i++) {
            JSONObject item = vacancies.getJSONObject(i);
            Long id = item.getLong("id");


            String imageUrl = item.getString("imageUrl");
            String location = null;
            if (item.has("location")) {
                location = (String) item.get("location");
            }
            String description = item.getString("description");
            String title = item.getString("job_title");

            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = null;
            if (item.has("advertDate")) {

                dateTime = dtf.parseDateTime(item.getString("advertDate"));

            }

            Vacancy record = new Vacancy();
            record.setId(id);
            record.setJob_title(title);
            record.setDescription(description);
            record.setLocation(location);
            record.setAdvertDate(dateTime);
            record.setImageUrl(imageUrl);

            records.add(record);
        }

        return records;
    }


    public interface OnSearchItemInteractionListener {

        void onSearchInteraction(Long id);
    }




}

