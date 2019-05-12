package za.co.moxomo.helpers;

/**
 * Created by Paballo Ditshego on 7/29/15.
 */

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import za.co.moxomo.R;
import za.co.moxomo.model.Vacancy;

/**
 * Class which has Utility methods
 */
public class Utility {
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static String phoneNumberRegex = "^((?:\\+27|27))(6|7|8)(\\d{8})$";
    private static Pattern pattern;
    private static Matcher matcher;


    public static boolean validateMsisdn(String msisdn) throws RuntimeException {
        Objects.requireNonNull(msisdn);
        return msisdn.trim().matches(phoneNumberRegex);

    }

    public static void changeStatusBarColor(Activity activity ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.action_color));
        }
    }
    public static boolean validateEmail(String email) {

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static List<Vacancy> parse(JSONObject json) throws JSONException {

        ArrayList<Vacancy> arrayList = new ArrayList<>();
        JSONArray array = json.getJSONArray("vacancies");

        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);

            String id = item.getString("id");
            String imageUrl = item.getString("imageUrl");
            String location = item.optString("location");
            String url = item.optString("url");
            boolean webViewViewable = item.optBoolean("webViewViewable");


            String description = item.optString("description");
            if (description.length() >= 400) {
                description = description.substring(0, 399);
                if (description.contains(".")) {
                    description = description.substring(0, description.lastIndexOf(".") + 1);
                }
            }
            String title = item.optString("jobTitle");

            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateTime dateTime = null;
            if (item.has("advertDate")) {
                dateTime = dtf.parseDateTime(item.getString("advertDate"));
            }
            Vacancy record = new Vacancy();
            record.setId(id);
            record.setJobTitle(title.trim());
            record.setDescription(StringUtils.normalizeSpace(description.trim()));
            record.setLocation(location.trim());
            record.setAdvertDate(dateTime);
            record.setImageUrl(imageUrl);
            record.setWebViewViewable(webViewViewable);
            record.setUrl(url);

            arrayList.add(record);
        }

        return arrayList;
    }

    public static Long getResultSetSize(JSONObject json) throws JSONException {
        return json.optLong("numberOfResults");
    }

    public static Observable<Bitmap> decodeBitmap(final Context context, final int resource) {
        return Observable.create(e -> {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resource);
            if (icon != null) {
                e.onNext(icon);
            } else {
                e.onError(new NullPointerException());
            }
            e.onComplete();
        });

    }

    public static void storeFcmTokenInSharedPref(Context context, String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(context.getString(R.string.pref_fcm_key), token).apply();
    }

    public static void storeMobileNumberInSharedPref(Context context, String sms) {
        if(null == getMobileNumberInSharedPref(context) || !sms.equals(getMobileNumberInSharedPref(context))) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString(context.getString(R.string.pref_mobile_key), sms).apply();
        }
    }

    public static String getMobileNumberInSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_mobile_key), null);
    }


    public static String getFcmTokenInSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_fcm_key), null);
    }



    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
