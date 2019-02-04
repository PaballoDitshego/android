package za.co.moxomo.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import de.greenrobot.event.EventBus;
import za.co.moxomo.R;
import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.activities.NotificationActivity;
import za.co.moxomo.events.BrowserViewEvent;
import za.co.moxomo.events.PageBackEvent;


public class WebViewFragment extends Fragment {


    private static String url;
    private WebView webView;
    private ProgressBar progressBar;
    private EventBus bus = EventBus.getDefault();


    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String address) {
        url = address;

        return new WebViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (webView != null) {
                webView.restoreState(savedInstanceState);
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_web, container, false);
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            progressBar = activity.getProgressBar();
        } else {
            NotificationActivity activity = (NotificationActivity) getActivity();
            progressBar = activity.getProgressBar();
        }


        webView = (WebView) view.findViewById(R.id.webView);
        progressBar.setMax(0);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // getActivity().setProgress(0);
            }
        });

        webView.setWebViewClient(new ViewClient());

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }

                return false;
            }
        });
        webView.loadUrl(url);
        webView.pageUp(true);


        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
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


    public void onEvent(BrowserViewEvent event) {

        apply(event.getUrl());
    }


    public void onEvent(PageBackEvent event) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            final MainActivity activity = (MainActivity) getActivity();
           /* int previousItem = activity.getBackStack().pop();
            activity.getPager().setCurrentItem(previousItem);*/
        }
    }

    public void apply(String url) {
        if (webView.canGoBack()) {
            webView.clearHistory();
        }
        webView.loadUrl(url);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public WebView getWebView() {
        return webView;
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
            view.scrollTo(0, 0);
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
