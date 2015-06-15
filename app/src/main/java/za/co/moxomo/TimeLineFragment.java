package za.co.moxomo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import za.co.moxomo.events.DetailViewInitEvent;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSearchInteractionListener}
 * interface.
 */
public class TimeLineFragment extends Fragment implements AbsListView.OnItemClickListener, AdapterView.OnItemSelectedListener,
        AbsListView.OnScrollListener {

    ///service url
    private static String URL = "https://moxomoapp.appspot.com/_ah/api/vacancyEndpoint/v1.1/list";


    private String mCategory;
    private String mNext_Cursor;
    private View mView;
    private int threshold = 10;


    private OnSearchInteractionListener mListener;
    private boolean restoreMode = false;

    /**
     * The fragment's ListView
     */
    private ListView mListView;
    private EventBus bus = EventBus.getDefault();


    /**
     * The Adapter which will be used to populate the ListView/ with
     * Views.
     */
    private MoxomoListAdapter mAdapter;
    private Spinner spinner;

    // TODO: Rename and change types of parameters
    public static TimeLineFragment newInstance() {
        return new TimeLineFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimeLineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (mListView != null) {
                if (mAdapter != null) {
                    List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("list_values"));
                    if (values != null) {
                        mAdapter.updateList(values);
                    }
                }

                mListView.onRestoreInstanceState(savedInstanceState.getParcelable("list_state"));
                mNext_Cursor = savedInstanceState.getString("cursor");
            }
        }
        mAdapter = new MoxomoListAdapter(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        List<Vacancy> list = mAdapter.getList();
        savedState.putParcelable("list_state", mListView.onSaveInstanceState());
        savedState.putString("cursor", mNext_Cursor);
        savedState.putParcelable("list_values", Parcels.wrap(list));



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (mAdapter != null && mAdapter.getCount() == 0) {
                List<Vacancy> values = (ArrayList<Vacancy>) Parcels.unwrap(savedInstanceState.getParcelable("list_values"));
                if (values != null) {
                    restoreMode = true; //prevent app from calling network operations
                    mAdapter.updateList(values);

                }
            }

            mListView.onRestoreInstanceState(savedInstanceState.getParcelable("list_state"));
            mNext_Cursor = savedInstanceState.getString("cursor");
            restoreMode = false;

        }
        setRetainInstance(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_timeline, container, false);


        mListView = (ListView) mView.findViewById(R.id.list);
        mListView.setOnScrollListener(this);
        mListView.setEmptyView(mView.findViewById(R.id.loading));
        mView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        spinner = (Spinner) mView.findViewById(R.id.categories);
        ArrayAdapter<CharSequence> sAdapter = MyArrayAdapter.createFromResource(getActivity(), R.array.categories_array, R.layout.custom_spinner);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(sAdapter);
        spinner.setOnItemSelectedListener(this);

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);


        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchInteractionListener) activity;
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
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            mListener.onFragmentInteraction(mAdapter.getItemId(position));
        }
    }


    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //spinner item selection
        mCategory = (String) parent.getSelectedItem();
        mNext_Cursor = null;
        if (mCategory.equals(("All Categories"))) {
            mCategory = null;
        }

        if (!restoreMode) {
            fetch(mCategory);
        }
    }


    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - threshold) {

                fetchMore(mCategory, mNext_Cursor);

            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchInteractionListener {

        public void onFragmentInteraction(Long id);
    }

    /**
     * Calls webservice, retrieves a json response and parses it into a list,
     * this method is called initially when the application loads and when the category changes
     *
     * @param category The category to fetch.
     */
    private void fetch(String category) {

        final MainActivity activity = (MainActivity) getActivity();
        activity.getmProgressBar().setVisibility(View.VISIBLE);
        String url = null;

        if (category == null) {
            url = URL;

        } else {
            try {
                category = URLEncoder.encode(category, "utf-8");
            } catch (UnsupportedEncodingException e) {

            }

            url = URL + "?category=" + category;
        }


        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            List<Vacancy> results = parse(jsonObject);

                            mAdapter.updateList(results);
                            mListView.getRootView().findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                            if (!results.isEmpty()) {
                                Vacancy temp = results.get(0); //get first element
                                bus.post(new DetailViewInitEvent(temp.getId())); //updates detailview but does not show it

                            }
                        } catch (JSONException e) {
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
                            mListView.getRootView().findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                            mListView.setEmptyView(mListView.getRootView().findViewById(R.id.empty));

                        }
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private void fetchMore(String category, String cursor) {

        final MainActivity activity = (MainActivity) getActivity();
        activity.getmProgressBar().setVisibility(View.VISIBLE);
        String url = null;
        if (cursor != null & category == null) {

            url = URL + "?cursor=" + cursor;
        }
        if (cursor != null & category != null) {
            try {
                category = URLEncoder.encode(category, "utf-8");
            } catch (UnsupportedEncodingException e) {

            }
            url = URL + "?category=" + category + "&cursor=" + mNext_Cursor;
        }
        if (cursor == null & category != null) {
            try {
                category = URLEncoder.encode(category, "utf-8");
            } catch (UnsupportedEncodingException e) {

            }
            url = URL + "?category=" + category;
        }
        if (cursor == null & category == null) {
            url = URL;

        }


        JsonObjectRequest request = new JsonObjectRequest(
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                            mAdapter.addMore(parse(jsonObject));

                        } catch (JSONException e) {
                            activity.getmProgressBar().setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        activity.getmProgressBar().setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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
        mNext_Cursor = json.getString("nextPageToken"); //next results cursor
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
            DateTimeFormatter dtf = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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


    private class MyArrayAdapter extends ArrayAdapter {

        public MyArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public TextView getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getView(position, convertView, parent);
            v.setTypeface(Typeface.SANS_SERIF);
            return v;
        }

        public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getView(position, convertView, parent);
            v.setTypeface(Typeface.DEFAULT_BOLD);
            return v;
        }

    }


}

