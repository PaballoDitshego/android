package za.co.moxomo.v2.dagger;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.moxomo.v2.repository.MoxomoDB;
import za.co.moxomo.v2.repository.Repository;
import za.co.moxomo.v2.service.RestApiService;
import za.co.moxomo.v2.viewmodel.ViewModelFactory;

@Module
public class InjectionModule {

    private static String BASE_URL = "https://moxomo.co.za";
    private final Context context;

    public InjectionModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public CompositeDisposable getCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        GsonBuilder builder =
                new GsonBuilder();
        return builder.setLenient().create();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .build();
                    return chain.proceed(request);
                }).connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);
        return httpClient.build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    public RestApiService getRestApi(Retrofit retrofit) {
        return retrofit.create(RestApiService.class);
    }

    @Provides
    @Singleton
    public Repository getRepository(RestApiService restAPIService, MoxomoDB moxomoDB) {
        return new Repository(restAPIService, moxomoDB);
    }

    @Provides
    @Singleton
    public ViewModelProvider.Factory getViewModelFactory(Repository repository) {
        return new ViewModelFactory(repository);
    }

    @Provides
    @Singleton
    public MoxomoDB getMoxomoDB(Context context) {
        return Room.databaseBuilder(context,
                MoxomoDB.class, "Moxomo Database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public Context context() {
        return context;
    }


}
