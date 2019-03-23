package za.co.moxomo.helpers;

/**
 * Created by Paballo Ditshego on 7/29/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import za.co.moxomo.model.Vacancy;

/**
 * Class which has Utility methods
 */
public class Utility {
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern;
    private static Matcher matcher;

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
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
                    description = description.substring(0, description.lastIndexOf(".")+1);
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
}