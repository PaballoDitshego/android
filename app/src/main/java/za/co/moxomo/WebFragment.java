package za.co.moxomo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import de.greenrobot.event.EventBus;
import za.co.moxomo.events.BrowserViewEvent;
import za.co.moxomo.events.BrowserViewInitEvent;
import za.co.moxomo.events.PageBackEvent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment {



    private WebView webView;
    private ProgressBar progressBar;

    private EventBus bus = EventBus.getDefault();



    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebFragment newInstance() {
        WebFragment fragment = new WebFragment();

        return fragment;
    }

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            if(webView!=null){
                webView.restoreState(savedInstanceState);
            }
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_web, container, false);
        final MainActivity activity = (MainActivity)getActivity();
        progressBar = activity.getmProgressBar();
        webView = (WebView) view.findViewById(R.id.webView);
        progressBar.setMax(0);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(0);
            }
        });
        webView.setWebViewClient(new ViewClient());

   if(!bus.isRegistered(this))
        bus.register(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        webView.saveState(savedState);


    }
    @Override
    public void onDestroy() {
        // Unregister this class from the eventbus on destroy
        bus.unregister(this);
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void onEvent(BrowserViewEvent event) {

        apply(event.getUrl());
    }

    public void onEvent(BrowserViewInitEvent event) {

        init(event.getUrl());
    }
    public void onEvent(PageBackEvent event) {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        else{
            final MainActivity activity = (MainActivity)getActivity();
            int previousItem = activity.getBackStack().pop();
            activity.getPager().setCurrentItem(previousItem);
        }
    }
    public void apply(String url) {
        MainActivity activity = (MainActivity) getActivity();
        activity.getBackStack().add(activity.getPager().getCurrentItem());
        activity.getPager().setCurrentItem(2);
        if(webView.canGoBack()){
            webView.clearHistory();
        }
        webView.loadUrl(url);

    }
    public void init(String url) {
        MainActivity activity = (MainActivity) getActivity();


        if(webView.canGoBack()){
            webView.clearHistory();
        }
        webView.loadUrl(url);

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

    public WebView getWebView() {
        return webView;
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

        public void onFragmentInteraction(Uri uri);
    }

    private class ViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }

    }

}
