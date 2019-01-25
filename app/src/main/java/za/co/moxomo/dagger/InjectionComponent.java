package za.co.moxomo.dagger;


import javax.inject.Singleton;

import dagger.Component;
import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.fragments.DetailPageFragment;
import za.co.moxomo.fragments.HomePageFragment;
import za.co.moxomo.fragments.NotificationFragment;

@Component(modules={InjectionModule.class})
@Singleton
public interface InjectionComponent {

    void inject(MainActivity activity);
    void inject(HomePageFragment homePageFragment);
    void inject(DetailPageFragment detailPageFragment);
    void inject (NotificationFragment notificationFragment);
}
