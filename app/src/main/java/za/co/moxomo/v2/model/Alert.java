package za.co.moxomo.v2.model;


import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
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


@Entity( tableName = "alert")
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alert  extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String alertId;
    private String location;
    private boolean sms;
    private boolean push;
    private String keyword;
    private String mobileNumber;
    private String gcmToken;
    @TypeConverters(JodaDateTimeConverter.class)
    private DateTime timestamp;

    public static DiffUtil.ItemCallback<Alert> DIFF_CALLBACK = new DiffUtil.ItemCallback<Alert>() {
        @Override
        public boolean areItemsTheSame(@NonNull Alert oldItem, @NonNull Alert newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Alert oldItem, @NonNull Alert newItem) {
            return oldItem.equals(newItem);
        }
    };


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }
}
