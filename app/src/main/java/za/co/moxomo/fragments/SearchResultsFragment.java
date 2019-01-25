package za.co.moxomo.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import za.co.moxomo.R;
import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.adapters.MoxomoListAdapter;
import za.co.moxomo.events.SearchEvent;
import za.co.moxomo.helpers.FontCache;
import za.co.moxomo.model.Vacancy;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSearchListInteractionListener}
 * interface.
 */
public class SearchResultsFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {

    //service url
    private static String URL = "https://moxomoapp.appspot.com/_ah/api/vacancyEndpoint/v1.1/search";


    private String mQuery;
    private String mNext_Cursor;
    private OnSearchListInteractionListener mListener;
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
    public SearchResultsFragment() {
    }


    public static SearchResultsFragment newInstance() {


        return new SearchResultsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventBus.register(this);


        if (savedInstanceState != null) {
            if (mListView != null) {
                if (mAdapter != null) {
                    mQuery = savedInstanceState.getString("query");
                    mNext_Cursor = savedInstanceState.getString("next_cursor");
                    List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("values"));
                    if (values != null) {
                        mAdapter.updateList(values);
                    }
                }
            }
        }
        mAdapter = new MoxomoListAdapter(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        List<Vacancy> list = mAdapter.getList();
        savedState.putString("query", mQuery);
        savedState.putString("next_cursor", mNext_Cursor);
        savedState.putParcelable("values", Parcels.wrap(list));



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (mAdapter != null) {
                mNext_Cursor = savedInstanceState.getString("next_cursor");
                mQuery = savedInstanceState.getString("query");
                List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("values"));

                if (values != null) {
                    mAdapter.updateList(values);
                    mSearchText.setText("Search results: " + mQuery);
                    Typeface typeface = FontCache.get("Roboto-Bold.ttf", getActivity());
                    mSearchText.setTypeface(typeface);
                    mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                }
            }


        }


    }

    @Override
    public void onDestroy() {
        // Unregister this class from the eventbus on destroy
        mEventBus.unregister(this);
        super.onDestroy();
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
            mListener = (OnSearchListInteractionListener) activity;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onSearchListInteraction(mAdapter.getItemId(position));
        }
    }



    public void onEvent(SearchEvent event) {
       //
        MainActivity activity = (MainActivity) getActivity();
      //  activity.getPager().setCurrentItem(3);
        queryString(event.getQuery());
    }


    public void queryString(String query) {

        mQuery = query;
        mNext_Cursor = null;


         //  fetch(mQuery);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - threshold) {
                if (mNext_Cursor != null && !mNext_Cursor.equals("EOR")) {
                 //   fetchMore(mQuery, mNext_Cursor);
                }
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

   /* private void fetch(String query) {
        //initial fetch
        final MainActivity activity = (MainActivity) getActivity();
        activity.getProgressBar().setVisibility(View.VISIBLE);
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


                            activity.getProgressBar().setVisibility(View.INVISIBLE);
                            mSearchText.setText("Search results: " + mQuery);
                            mAdapter.updateList(parse(jsonObject));
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.INVISIBLE);
                            mListView.setSelection(0);

                        } catch (JSONException e) {
                            mSearchText.setText("Search results for: " + mQuery);
                            activity.getProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mAdapter.clearList();
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.VISIBLE);

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        activity.getProgressBar().setVisibility(View.INVISIBLE);
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

        DaggerApplication.getInstance().getRequestQueue().add(request);
    }

    private void fetchMore(String query, String cursor) {

        final MainActivity activity = (MainActivity) getActivity();
        activity.getProgressBar().setVisibility(View.VISIBLE);

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


                            activity.getProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.INVISIBLE);
                            mAdapter.addMore(parse(jsonObject));

                        } catch (JSONException e) {
                            activity.getProgressBar().setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                            mListView.getRootView().findViewById(R.id.search_empty).setVisibility(View.INVISIBLE);

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        activity.getProgressBar().setVisibility(View.INVISIBLE);
                        mListView.getRootView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
                        //  Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        DaggerApplication.getInstance().getRequestQueue().add(request);
    }
*/
    private List<Vacancy> parse(JSONObject json) throws JSONException {
        ArrayList<Vacancy> records = new ArrayList<>();


        if(json.has("nextPageToken")) {
            mNext_Cursor = json.getString("nextPageToken");
        }
        JSONArray vacancies = json.getJSONArray("items");

        for (int i = 0; i < vacancies.length(); i++) {
            JSONObject item = vacancies.getJSONObject(i);
            String id = item.getString("id");


            String imageUrl = item.getString("imageUrl");
            String location = null;
            if (item.has("location")) {
                location = (String) item.get("location");
            }
            String description = item.getString("description");
            if (description.length() > 400) {
                description = description.substring(0, 400) + "....";
            }
            String title = null;
            if (item.has("job_title")) {
                title = item.getString("job_title");
            }

            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = null;
            if (item.has("advertDate")) {

                dateTime = dtf.parseDateTime(item.getString("advertDate"));

            }

            Vacancy record = new Vacancy();
            record.setId(id);
            record.setJobTitle(title);
            record.setDescription(description);
            record.setLocation(location);
            record.setAdvertDate(dateTime);
            record.setImageUrl(imageUrl);
            if (title != null) {
            records.add(record);
            }
        }

        return records;
    }


    public interface OnSearchListInteractionListener {

        void onSearchListInteraction(Long id);
    }




}

