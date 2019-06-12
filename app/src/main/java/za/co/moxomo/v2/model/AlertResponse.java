
package za.co.moxomo.v2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlertResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("keyword")
    @Expose
    private String title;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("gcmToken")
    @Expose
    private String gcmToken;
    @SerializedName("smsAlert")
    @Expose
    private Boolean smsAlert;
    @SerializedName("pushAlert")
    @Expose
    private Boolean pushAlert;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getSmsAlert() {
        return smsAlert;
    }

    public void setSmsAlert(Boolean smsAlert) {
        this.smsAlert = smsAlert;
    }

    public Boolean getPushAlert() {
        return pushAlert;
    }

    public void setPushAlert(Boolean pushAlert) {
        this.pushAlert = pushAlert;
    }


}
