package za.co.moxomo.v2.enums;

import lombok.Getter;
import za.co.moxomo.v2.R;

@Getter
public enum FragmentEnum {

    CREATE_ALERT(R.id.createAlertFragment, "Create Alarm"),
    VIEW_ALERT(R.id.viewAlertsFragment, "Alarms"),
    EDIT_ALERT(R.id.viewAlertsFragment, "Edit Alarm");

    private int fragmentId;
    private String title;

    FragmentEnum(int fragmentId, String title){
        this.fragmentId=fragmentId;
        this.title =title;

    }
}
