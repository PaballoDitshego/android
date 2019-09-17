package za.co.moxomo.v2.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.Objects;

import za.co.moxomo.v2.helpers.JodaDateTimeConverter;


@Parcel
@Entity(tableName = "savedvacancy")
public class SavedVacancy extends BaseObservable {


    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String id;
    public String jobTitle;
    public String description;
    public String company;
    public String location;
    public String min_qual;
    public String duties;
    public boolean liked;
    public boolean webViewViewable;

    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime advertDate;
    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime closingDate;
    public String url;
    public String imageUrl;

    public static DiffUtil.ItemCallback<SavedVacancy> DIFF_CALLBACK = new DiffUtil.ItemCallback<SavedVacancy>() {
        @Override
        public boolean areItemsTheSame(@NonNull SavedVacancy oldItem, @NonNull SavedVacancy newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SavedVacancy oldItem, @NonNull SavedVacancy newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedVacancy)) return false;
        SavedVacancy vacancy = (SavedVacancy) o;
        return Objects.equals(jobTitle, vacancy.jobTitle) &&
                Objects.equals(description, vacancy.description) &&
                Objects.equals(location, vacancy.location) &&
                Objects.equals(advertDate, vacancy.advertDate) &&
                Objects.equals(imageUrl, vacancy.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, description, location, advertDate, imageUrl);
    }

    @Bindable
    public String getCompany() {
        return company;
    }

    @Bindable
    public void setCompany(String company_name) {
        this.company = company_name;
    }

    @Bindable
    public String getMin_qual() {
        return min_qual;
    }

    @Bindable
    public void setMinQual(String minQual) {
        this.min_qual = min_qual;
    }

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    @Bindable
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    @Bindable
    public void setLocation(String location) {
        this.location = location;
    }

    @Bindable
    public String getDuties() {
        return duties;
    }

    @Bindable
    public void setDuties(String duties) {
        this.duties = duties;
    }

    @Bindable
    public DateTime getClosingDate() {
        return closingDate;
    }

    @Bindable
    public void setClosingDate(DateTime closingDate) {
        this.closingDate = closingDate;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    @Bindable
    public void setWebViewViewable(boolean webViewViewable) {
        this.webViewViewable = webViewViewable;
    }

    public boolean isLiked() {
        return liked;
    }

    @Bindable
    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public DateTime getAdvertDate() {
        return advertDate;
    }

    @Bindable
    public void setAdvertDate(DateTime advertDate) {
        this.advertDate = advertDate;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @Bindable
    public void setDescription(String description) {
        this.description = description;
    }

    @Bindable
    public boolean getLiked() {
        return liked;
    }

    @Bindable
    public boolean getWebViewable() {
        return webViewViewable;
    }


    @Bindable
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
