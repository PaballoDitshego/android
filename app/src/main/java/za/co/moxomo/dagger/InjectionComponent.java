package za.co.moxomo.dagger;


import javax.inject.Singleton;

import dagger.Component;
import za.co.moxomo.activities.AlertActivity;
import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.fragments.HomePageFragment;
import za.co.moxomo.fragments.NotificationFragment;

@Component(modules={InjectionModule.class})
@Singleton
public interface InjectionComponent {

    void inject(MainActivity activity);
    void inject(HomePageFragment homePageFragment);
    void inject (NotificationFragment notificationFragment);
    void inject(AlertActivity alertActivity);
}
