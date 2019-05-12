package za.co.moxomo.model;

import org.joda.time.DateTime;
import org.parceler.ParcelPropertyConverter;

import java.util.Base64;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import za.co.moxomo.helpers.JodaDateTimeConverter;

@Entity( tableName = "notification")
@Data
@EqualsAndHashCode(callSuper=false)
public class Notification  extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String url;
    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime timestamp;


    public Notification() {
    }

    public static DiffUtil.ItemCallback<Notification> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.id.equals(newItem.id);
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


    public String getDescription() {
        return description;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public String getUrl() {
        return url;
    }


    public DateTime getTimestamp() {
        return timestamp;
    }
}
