package za.co.moxomo.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String alertId;
    private String[] province;
    private String[] town;
    private String[] tags;
    private String message;
    private String sms;
    private String push;
    private String title;
    private String mobileNumber;
    private String gcmToken;

}
