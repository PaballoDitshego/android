package za.co.moxomo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import de.greenrobot.event.EventBus;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.events.DetailViewInitEvent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailViewFragment extends Fragment {

    public static Long id = null;
    private static String URL = "https://moxomoapp.appspot.com/_ah/api/vacancyEndpoint/v1.1/vacancy/";
    private OnFragmentInteractionListener mListener;
    private Vacancy vacancy = null;
    private TextView title;
    private ImageView image;
    private TextView location;
    private TextView closingDate;
    private TextView description;
    private TextView qualifications;
    private TextView responsibilities;
    private TextView company;
    private ScrollView mScrollView;
    private EventBus bus = EventBus.getDefault();
    private Button apply;
    private ShareActionProvider shareAction;
    private Intent shareIntent;


    public DetailViewFragment() {
        // Required empty public constructor
    }

    public static DetailViewFragment newInstance() {


        return new DetailViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        shareIntent = new Intent(Intent.ACTION_SEND)

                .setType("text/plain");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_view, container, false);


        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        title = (TextView) view.findViewById(R.id.title);
        image = (ImageView) view.findViewById(R.id.thumbnail);
        location = (TextView) view.findViewById(R.id.location);
        description = (TextView) view.findViewById(R.id.description);
        qualifications = (TextView) view.findViewById(R.id.qualifications);
        responsibilities = (TextView) view.findViewById(R.id.responsibilites);
        company = (TextView) view.findViewById(R.id.company);
        closingDate = (TextView) view.findViewById(R.id.time);
        apply = (Button) view.findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vacancy != null) {

                    onButtonPressed(vacancy.getWebsite());
                }

            }
        });


        bus.register(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        if (vacancy != null) {
            savedState.putParcelable("values", Parcels.wrap(vacancy));

        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            vacancy = Parcels.unwrap(savedInstanceState.getParcelable("values"));
            if (vacancy != null) {
                updateUI(vacancy);
            }

        }
        setRetainInstance(true);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String url) {
        if (mListener != null) {
            mListener.onFragmentInteraction(url);
        }
    }

    @Override
    public void onDestroy() {
        // Unregister this class from the eventbus on destroy
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detailview, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareAction.setShareIntent(shareIntent);

    }

    public void getEntry(Long id) {


        String url = URL + id;
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading .....");
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            progressDialog.dismiss();
                            vacancy = parse(jsonObject);
                            if (vacancy != null) {
                                updateUI(vacancy);
                                MainActivity activity = (MainActivity) getActivity();
                                activity.getPager().setCurrentItem(1);
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    public void init(Long id) {

        String url = URL + id;

        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            vacancy = parse(jsonObject);
                            if (vacancy != null) {
                                updateUI(vacancy);
                                //  bus.post(new BrowserViewInitEvent(vacancy.getWebsite()));

                            }


                        } catch (JSONException e) {
                            Log.d(getClass().getName(), e.getMessage());
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private void updateUI(Vacancy result) {
        if (result.getJob_title() != null) {
            mScrollView.scrollTo(0, 0);
            title.setText(result.getJob_title());
            Picasso.with(getActivity()).load(result.getImageUrl()).into(image);
            location.setText(result.getLocation());
            description.setText(result.getDescription());
            qualifications.setText(result.getMin_qual());
            responsibilities.setText(result.getDuties());
            company.setText(result.getCompany_name());
            closingDate.setText("Closes on: " + result.getClosingDate().toLocalDate());
            apply.setVisibility(View.VISIBLE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, vacancy.getWebsite());


        }
    }


    public void onEvent(DetailViewEvent event) {
        getEntry(event.getId());
    }

    public void onEvent(DetailViewInitEvent event) {
        init(event.getId());
    }

    private Vacancy parse(JSONObject json) throws JSONException {

        Long id = json.getLong("id");


        String imageUrl = json.getString("imageUrl");

        String location = null;
        if (json.has("location")) {
            location = (String) json.get("location");
        }
        String description = json.getString("description");
        String title = json.getString("job_title");
        Log.d("title", title);

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        DateTime advertDate = null;
        DateTime closingDate = null;
        if (json.has("advertDate")) {

            advertDate = dtf.parseDateTime(json.getString("advertDate"));
        }
        if (json.has("closingDate")) {

            closingDate = dtf.parseDateTime(json.getString("closingDate"));
        }

        String min_qual = null;
        if (json.has("min_qual")) {
            min_qual = json.getString("min_qual");
        }
        String website = json.getString("website");
        String duties = null;
        if (json.has("duties")) {
            duties = json.getString("duties");
        }
        String category = json.getString("category");
        String company = "";
        if (json.has("company_name")) {
            company = json.getString("company_name");
        }


        Vacancy record = new Vacancy();
        record.setId(id);

        record.setJob_title(title);
        record.setDescription(description);
        record.setLocation(location);
        record.setAdvertDate(advertDate);
        record.setImageUrl(imageUrl);
        record.setClosingDate(closingDate);
        record.setDuties(duties);
        record.setWebsite(website);
        record.setCompany_name(company);

        record.setMin_qual(min_qual);


        return record;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String url);
    }


}