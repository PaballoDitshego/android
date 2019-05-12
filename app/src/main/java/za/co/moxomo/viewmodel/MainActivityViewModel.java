package za.co.moxomo.viewmodel;


import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import za.co.moxomo.model.Notification;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.repository.Repository;
import za.co.moxomo.repository.VacancyClassDatasourceFactory;
import za.co.moxomo.repository.VacancyDataSource;

@Getter
public class MainActivityViewModel extends ViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private Executor executor;
    private VacancyClassDatasourceFactory vacancyClassDatasourceFactory;
    private LiveData<PagedList<Vacancy>> vacancies;
    private LiveData<PagedList<Notification>> notifications;
    private LiveData<String> progressLoadStatus = new MutableLiveData<>();
    private LiveData<String> resultSetSize = new MutableLiveData<>();
    private MutableLiveData<String> searchString = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Repository repository;

    public MainActivityViewModel(Repository repository) {
        this.repository =repository;
        vacancyClassDatasourceFactory = new VacancyClassDatasourceFactory(repository, compositeDisposable);
        initializePaging();
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


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
