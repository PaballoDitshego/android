package za.co.moxomo;

import lombok.Getter;

@Getter
public enum FragmentEnum {

    CREATE_ALERT(R.id.createAlertFragment),
    VIEW_ALERT(R.id.viewAlertsFragment);

    private int fragmentId;

    FragmentEnum(int fragmentId){
        this.fragmentId=fragmentId;

    }
}
