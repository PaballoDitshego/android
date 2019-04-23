package za.co.moxomo.viewmodel;


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
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.repository.AlertClassDatasourceFactory;
import za.co.moxomo.repository.Repository;
import za.co.moxomo.model.ApiResponse;
import za.co.moxomo.repository.VacancyClassDatasourceFactory;
import za.co.moxomo.repository.VacancyDataSource;

@Getter
public class AlertActivityViewModel extends ViewModel {

    private Repository repository;

    private Executor executor;

    private MutableLiveData<String> progressLiveStatus = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<ApiResponse> alertCreationResponse = new MutableLiveData<>();

    AlertActivityViewModel(Repository repository) {
        this.repository = repository;

    }


    public void createAlert(Alert alert) {
        repository.createAlert(alert).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((d) -> progressLiveStatus.setValue(ApplicationConstants.LOADING)).subscribe(result -> {
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertCreationResponse.setValue(ApiResponse.success(result));
                }
                , throwable -> {
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertCreationResponse.setValue(ApiResponse.error(throwable));
                });

    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}