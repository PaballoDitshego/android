package za.co.moxomo.v2.adapters;


import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.joda.time.DateTime;

import androidx.databinding.BindingAdapter;

public class ViewBindingAdapter {

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl != null) {
            if (!imageUrl.isEmpty() && imageUrl.endsWith("png.jpeg")) {
                imageUrl = imageUrl.replace("png.jpeg", "png");
            }
            if (imageUrl.equals("http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png")) {
                imageUrl = "https://www.pnet.co.za/modules/duplo/resources/images/logo/pnet-logo-facebook.jpg";
            }
            if(imageUrl.contains("careers24")){
                if(imageUrl.contains("http://")){
                    imageUrl = imageUrl.replace("http", "https");
                }else{
                  imageUrl = "https://".concat(imageUrl);
                }
            }
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .override(100, 100)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .into(view);
        }

    }


    @BindingAdapter({"android:advertDate"})
    public static void setRelativeTime(TextView view, DateTime advertDate) {
        if (advertDate != null) {
            view.setText(DateUtils.getRelativeDateTimeString(view.getContext(),
                    advertDate.getMillis(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));

        }
    }

}
