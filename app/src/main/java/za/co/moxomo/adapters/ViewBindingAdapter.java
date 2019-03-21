package za.co.moxomo.adapters;

import android.databinding.BindingAdapter;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.Objects;

public class ViewBindingAdapter {

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (!imageUrl.isEmpty() && imageUrl.endsWith("png.jpeg")) {
            imageUrl = imageUrl.replace("png.jpeg", "png");
        }
        Glide.with(view.getContext())
                .load(imageUrl)
                .override(100, 100)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(view);

    }


    @BindingAdapter({"android:advertDate"})
    public static void setRelativeTime(TextView view, DateTime advertDate) {
        view.setText(DateUtils.getRelativeDateTimeString(view.getContext(),
                advertDate.getMillis(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));

    }

}
