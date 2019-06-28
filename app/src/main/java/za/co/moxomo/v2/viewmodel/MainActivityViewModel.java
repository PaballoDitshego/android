package za.co.moxomo.v2.viewmodel;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import za.co.moxomo.v2.model.Notification;
import za.co.moxomo.v2.model.Vacancy;
import za.co.moxomo.v2.repository.Repository;
import za.co.moxomo.v2.repository.VacancyClassDatasourceFactory;
import za.co.moxomo.v2.repository.VacancyDataSource;

@Getter
public class MainActivityViewModel extends ViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private Executor executor;
    private VacancyClassDatasourceFactory vacancyClassDatasourceFactory;
    private LiveData<PagedList<Vacancy>> vacancies;
    private LiveData<PagedList<Notification>> notifications;
    private LiveData<PagedList<Vacancy>> pagedDBVacancies;
    private LiveData<String> progressLoadStatus = new MutableLiveData<>();
    private LiveData<String> resultSetSize = new MutableLiveData<>();
    private MutableLiveData<String> searchString = new MutableLiveData<>();
    private MutableLiveData<List<String>> keywordSuggestions = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MediatorLiveData liveDataMerger = new MediatorLiveData();
    private Gson gson;
    private Repository repository;

    public MainActivityViewModel(Repository repository) {
        this.repository =repository;
        vacancyClassDatasourceFactory = new VacancyClassDatasourceFactory(repository, compositeDisposable);
        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.setLenient().create();
        initializePaging();
    }

    public static void deleteNotification(Notification notification) {
    }

    private void initializePaging() {
        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(15)
                        .setPageSize(5).build();
        executor = Executors.newFixedThreadPool(5);
        vacancies = (new LivePagedListBuilder(vacancyClassDatasourceFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
        notifications = (new LivePagedListBuilder(repository.fetchNotifications(), pagedListConfig))
                .setFetchExecutor(executor)
                .build();
        pagedDBVacancies = (new LivePagedListBuilder(repository.fetchDBVacancies(), pagedListConfig))
                .setFetchExecutor(executor)
                .build();


        progressLoadStatus = Transformations.switchMap(vacancyClassDatasourceFactory.getMutableLiveData(), VacancyDataSource::getProgressLiveStatus);
        resultSetSize = Transformations.switchMap(vacancyClassDatasourceFactory.getMutableLiveData(), VacancyDataSource::getResultSize);

    }

   public  void sendFCMToken(String newToken, String oldToken){
       repository.sendFCMTokenToServer(newToken,oldToken).doOnSubscribe(disposable -> {
           compositeDisposable.add(disposable);
       }).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(result -> {
                           Log.d(TAG, "token result " +result.toString());
                       }
                       , throwable -> {
                           Log.d(TAG,"token error "+ throwable.getLocalizedMessage());

                       });

   }
    public void getKeywordSuggestion(String keyword) {
        repository.getKeywordSuggestion(keyword).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            List<String> suggestions = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                            keywordSuggestions.setValue(suggestions);
                        }
                        , throwable -> {

                        });

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    private PagedList.BoundaryCallback<Vacancy> boundaryCallback = new PagedList.BoundaryCallback<Vacancy>() {
        @Override
        public void onZeroItemsLoaded() {
            super.onZeroItemsLoaded();
            liveDataMerger.addSource(pagedDBVacancies, value -> {
                liveDataMerger.setValue(value);
                liveDataMerger.removeSource(pagedDBVacancies);
            });
        }
    };


}
