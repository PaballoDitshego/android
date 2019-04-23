package za.co.moxomo.model;


import androidx.databinding.BaseObservable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity( tableName = "alert")
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class Alert  extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String alertId;
    private String location;
    private String tags;
    private String sms;
    private String push;
    private String title;
    private String mobileNumber;
    private String gcmToken;




}
