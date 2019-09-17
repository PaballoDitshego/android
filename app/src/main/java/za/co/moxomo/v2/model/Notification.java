package za.co.moxomo.v2.model;

import org.joda.time.DateTime;
import org.parceler.ParcelPropertyConverter;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import za.co.moxomo.v2.helpers.JodaDateTimeConverter;

@Entity( tableName = "notification")
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification  extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String url;
    private String location;
    private String type;
    private String alertTitle;
    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime timestamp;




    public static DiffUtil.ItemCallback<Notification> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.id==newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.equals(newItem);
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
                return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Bindable
    public String getDescription() {
        return description;
    }


    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }


    public String getUrl() {
        return url;
    }

    @Bindable
    public DateTime getTimestamp() {
        return timestamp;
    }

    @Bindable
    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }
}
