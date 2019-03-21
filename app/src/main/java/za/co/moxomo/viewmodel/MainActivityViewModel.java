package za.co.moxomo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.repository.Repository;
import za.co.moxomo.repository.VacancyClassDatasourceFactory;
import za.co.moxomo.repository.VacancyDataSource;

public class MainActivityViewModel extends ViewModel {

    private Executor executor;
    private VacancyClassDatasourceFactory vacancyClassDatasourceFactory;
    private LiveData<PagedList<Vacancy>> vacancies;
    private LiveData<String> progressLoadStatus = new MutableLiveData<>();
    private MutableLiveData<String> searchString = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainActivityViewModel(Repository repository) {
        vacancyClassDatasourceFactory = new VacancyClassDatasourceFactory(repository, compositeDisposable);
        initializePaging();
    }

    private void initializePaging() {

        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(5)
                        .setPageSize(5).build();


        executor = Executors.newFixedThreadPool(5);
        vacancies = (new LivePagedListBuilder(vacancyClassDatasourceFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
         progressLoadStatus = Transformations.switchMap(vacancyClassDatasourceFactory.getMutableLiveData(), VacancyDataSource::getProgressLiveStatus);

    }

    public VacancyClassDatasourceFactory getVacancyClassDatasourceFactory() {
        return vacancyClassDatasourceFactory;
    }

    public MutableLiveData<String> getSearchString() {
        return searchString;
    }

    public LiveData<String> getProgressLoadStatus() {
        return progressLoadStatus;
    }

    public LiveData<PagedList<Vacancy>> getVacancies() {
        return vacancies;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
