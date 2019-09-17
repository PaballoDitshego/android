package za.co.moxomo.v2.model;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.Objects;

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


@Parcel
@Entity(tableName = "vacancy")
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  Vacancy  extends BaseObservable {

    @PrimaryKey(autoGenerate=true)
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
    private String distance;

    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime advertDate;
    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    @TypeConverters(JodaDateTimeConverter.class)
    public DateTime closingDate;
    public String url;
    public String imageUrl;

    public static DiffUtil.ItemCallback<Vacancy> DIFF_CALLBACK = new DiffUtil.ItemCallback<Vacancy>() {
        @Override
        public boolean areItemsTheSame(@NonNull Vacancy oldItem, @NonNull Vacancy newItem) {
            return oldItem._id == newItem._id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Vacancy oldItem, @NonNull Vacancy newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacancy)) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(jobTitle, vacancy.jobTitle) &&
                Objects.equals(description, vacancy.description) &&
                Objects.equals(location, vacancy.location) &&
                Objects.equals(imageUrl, vacancy.imageUrl);
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

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    @Bindable
    public String getDistance() {
        return distance;
    }

    @Bindable
    public void setDistance(String distance) {
        this.distance = distance;
    }
}
