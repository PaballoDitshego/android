package za.co.moxomo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.Date;

import za.co.moxomo.R;
import za.co.moxomo.helpers.FontCache;

/**
 * Created by Paballo Ditshego on 7/31/15.
 */
public class NotificationsCursorAdapter extends CursorAdapter {

    private int lastPosition = -1;

    public NotificationsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.notifications_list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        tv_title.setTypeface(FontCache.get("Roboto_Thin.ttf", context));
        TextView tv_body = (TextView) view.findViewById(R.id.body);
        tv_body.setTypeface(FontCache.get("Roboto-Regular.ttf", context));
        TextView tv_timestamp = (TextView) view.findViewById(R.id.timestamp);
        tv_timestamp.setTypeface(FontCache.get("Roboto-Regular.ttf", context));
        ImageView image = (ImageView) view.findViewById(R.id.thumbnail);
        // Extract properties from cursor
        String image_url = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));

        if (cursor.getString(cursor.getColumnIndexOrThrow("status")).equals("read")) {
            tv_title.setTypeface(Typeface.DEFAULT);

        }

        Picasso.with(context).load(image_url).into(image);
        tv_title.setText(title);
        tv_body.setText(body);
        //  DateFormat formatter = android.text.format.DateFormat
        //        .getDateFormat(context);
        long date = cursor.getLong(cursor.getColumnIndexOrThrow("time"));
        Date dateObj = new Date(date * 1000);
        DateTime dateTime = new DateTime(dateObj);
        tv_timestamp.setText(DateUtils.getRelativeDateTimeString(context,
                dateTime.getMillis(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));


        Animation animation = AnimationUtils.loadAnimation(context, (cursor.getPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = cursor.getPosition();
    }
}
