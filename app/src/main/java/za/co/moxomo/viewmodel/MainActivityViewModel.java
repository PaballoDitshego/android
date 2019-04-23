package za.co.moxomo.viewmodel;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;
import lombok.Getter;
import za.co.moxomo.model.Notification;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.repository.NotificationClassDatasourceFactory;
import za.co.moxomo.repository.Repository;
import za.co.moxomo.repository.VacancyClassDatasourceFactory;
import za.co.moxomo.repository.VacancyDataSource;

@Getter
public class MainActivityViewModel extends ViewModel {

    private Executor executor;
    private VacancyClassDatasourceFactory vacancyClassDatasourceFactory;
    private NotificationClassDatasourceFactory notificationClassDatasourceFactory;
    private LiveData<PagedList<Vacancy>> vacancies;
    private LiveData<PagedList<Notification>> notifications;
    private LiveData<String> progressLoadStatus = new MutableLiveData<>();
    private LiveData<String> resultSetSize = new MutableLiveData<>();
    private MutableLiveData<String> searchString = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainActivityViewModel(Repository repository) {
        vacancyClassDatasourceFactory = new VacancyClassDatasourceFactory(repository, compositeDisposable);
        notificationClassDatasourceFactory = new NotificationClassDatasourceFactory(repository, compositeDisposable);
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
        notifications = (new LivePagedListBuilder(notificationClassDatasourceFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();

        progressLoadStatus = Transformations.switchMap(vacancyClassDatasourceFactory.getMutableLiveData(), VacancyDataSource::getProgressLiveStatus);
        resultSetSize = Transformations.switchMap(vacancyClassDatasourceFactory.getMutableLiveData(), VacancyDataSource::getResultSize);

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
