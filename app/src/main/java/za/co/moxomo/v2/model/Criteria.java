
package za.co.moxomo.v2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Criteria {

    @SerializedName("jobTitle")
    @Expose
    private String jobTitle;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("tags")
    @Expose
    private Object tags;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getTags() {
        return tags;
    }

    public void setTags(Object tags) {
        this.tags = tags;
    }

}
