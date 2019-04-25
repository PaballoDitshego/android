package za.co.moxomo.viewmodel;


import java.util.concurrent.Executor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.NoArgsConstructor;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.ApiResponse;
import za.co.moxomo.repository.Repository;


@Getter
@NoArgsConstructor
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