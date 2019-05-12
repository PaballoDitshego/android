package za.co.moxomo;


import androidx.multidex.MultiDexApplication;
import za.co.moxomo.dagger.DaggerInjectionComponent;
import za.co.moxomo.dagger.InjectionComponent;
import za.co.moxomo.dagger.InjectionModule;

public class MoxomoApplication extends MultiDexApplication {
   private  static MoxomoApplication moxomoApplication;
   private InjectionComponent injectionComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        moxomoApplication = this;

       injectionComponent = DaggerInjectionComponent.builder()
                .injectionModule(new InjectionModule(getApplicationContext())).build();
    }

    public static MoxomoApplication moxomoApplication(){
        return moxomoApplication;
    }

    public InjectionComponent injectionComponent(){
        return injectionComponent;
    }


}
