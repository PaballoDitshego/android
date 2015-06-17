package za.co.moxomo;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;


@Parcel
public class Vacancy {


    private Long id;
    private String job_title;
    private String description;
    private String company_name;
    private String location;
    private String min_qual;
    private String duties;

    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    private DateTime advertDate;
    @ParcelPropertyConverter(JodaDateTimeConverter.class)
    private DateTime closingDate;
    private String website;
    private String imageUrl;

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getMin_qual() {
        return min_qual;
    }

    public void setMin_qual(String min_qual) {
        this.min_qual = min_qual;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties;
    }


    public DateTime getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(DateTime closingDate) {
        this.closingDate = closingDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public DateTime getAdvertDate() {
        return advertDate;
    }

    public void setAdvertDate(DateTime advertDate) {
        this.advertDate = advertDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
