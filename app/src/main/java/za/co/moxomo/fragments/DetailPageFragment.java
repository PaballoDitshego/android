package za.co.moxomo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.ProgressBar;
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
import za.co.moxomo.R;
import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.activities.NotificationActivity;
import za.co.moxomo.events.DetailViewEvent;
import za.co.moxomo.events.DetailViewInitEvent;
import za.co.moxomo.helpers.FontCache;
import za.co.moxomo.helpers.VolleyApplication;
import za.co.moxomo.model.Vacancy;


public class DetailPageFragment extends Fragment {

    public static Long id = null;
    private static String URL = "https://moxomoapp.appspot.com/_ah/api/vacancyEndpoint/v1.1/vacancy/";
    private OnApplyButtonInteractionListener mListener;
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
    private ProgressBar progressBar;


    public DetailPageFragment() {
        // Required empty public constructor
    }

    public static DetailPageFragment newInstance() {

        return new DetailPageFragment();
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

        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            progressBar = activity.getProgressBar();

        } else {
            NotificationActivity activity = (NotificationActivity) getActivity();
            progressBar = activity.getProgressBar();
        }


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

    public void onButtonPressed(String url) {
        if (mListener != null) {
            mListener.onApplyButtonInteraction(url);
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

    public void getEntry(final Long id) {



        String url = URL + id;
        
        if (getActivity() instanceof MainActivity) {
            progressBar.setVisibility(View.VISIBLE);

        }


        JsonObjectRequest request = new JsonObjectRequest(

                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            progressBar.setVisibility(View.INVISIBLE);
                            vacancy = parse(jsonObject);
                            if (vacancy != null) {
                                updateUI(vacancy);
                                if (getActivity() instanceof MainActivity) {
                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.getPager().setCurrentItem(1);
                                }
                            }

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(getActivity(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("stupid error", id.toString());
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // progressBar.setVisibility(View.VISIBLE);
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
            Typeface regular = FontCache.get("Roboto-Regular.ttf", getActivity());
            description.setText(result.getDescription());
            description.setTypeface(regular);
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
            mListener = (OnApplyButtonInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnApplyButtonInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnApplyButtonInteractionListener {

        void onApplyButtonInteraction(String url);
    }


}