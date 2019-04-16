package za.co.moxomo.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {

    private String alertId;
    private String location;
    private String tags;
    private String sms;
    private String push;
    private String title;
    private String mobileNumber;
    private String gcmToken;




}
