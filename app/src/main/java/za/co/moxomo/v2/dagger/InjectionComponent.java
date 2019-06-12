package za.co.moxomo.v2.dagger;


import javax.inject.Singleton;

import dagger.Component;
import za.co.moxomo.v2.activities.AlertActivity;
import za.co.moxomo.v2.activities.MainActivity;
import za.co.moxomo.v2.fragments.CreateAlertFragment;
import za.co.moxomo.v2.fragments.EditAlertFragment;
import za.co.moxomo.v2.fragments.HomePageFragment;
import za.co.moxomo.v2.fragments.NotificationFragment;
import za.co.moxomo.v2.fragments.ViewAlertsFragment;
import za.co.moxomo.v2.fcm.FCMListenerService;

@Component(modules = {InjectionModule.class})
@Singleton
public interface InjectionComponent {

    void inject(MainActivity activity);

    void inject(AlertActivity alertActivity);

    void inject(HomePageFragment homePageFragment);

    void inject(NotificationFragment notificationFragment);

    void inject(CreateAlertFragment createAlertFragment);

    void inject(ViewAlertsFragment viewAlertsFragment);

    void inject(FCMListenerService fcmListenerService);

    void inject (EditAlertFragment editAlertFragment);


}
