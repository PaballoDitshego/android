package za.co.moxomo.viewmodel;


import com.google.gson.Gson;

import java.util.concurrent.Executor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.AlertDTO;
import za.co.moxomo.repository.Repository;
import za.co.moxomo.service.ApiResponse;

@Getter
public class AlertActivityViewModel extends ViewModel {
    private Executor executor;
    private Repository repository;
    private Gson gson;
    private MutableLiveData<String> progressLiveStatus;
    private MutableLiveData<String> resultSize;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ApiResponse> alertCreationResponse = new MutableLiveData<>();

    AlertActivityViewModel(Repository repository, Gson gson) {
        this.repository = repository;
        this.gson = gson;

    }


    public void createAlert(AlertDTO alertDTO) {
        repository.createAlert(alertDTO).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((d) -> progressLiveStatus.setValue(ApplicationConstants.LOADING)).subscribe(result -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    alertCreationResponse.setValue(ApiResponse.success(result));
                }
                , throwable -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    alertCreationResponse.setValue(ApiResponse.error(throwable));
                });

    }


}