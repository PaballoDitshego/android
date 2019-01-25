package za.co.moxomo.adapters;

import android.databinding.BindingAdapter;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

public class ViewBindingAdapter {

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl.isEmpty()
                ) {
            imageUrl="http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png";
        }
        Picasso.with(view.getContext()).load(imageUrl).into(view);

    }


    @BindingAdapter({"android:advertDate"})
    public static void setRelativeTime(TextView view, DateTime advertDate) {
        view.setText(DateUtils.getRelativeDateTimeString(view.getContext(),
                advertDate.getMillis(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));

    }

}
