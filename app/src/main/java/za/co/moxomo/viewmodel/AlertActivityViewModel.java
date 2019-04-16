package za.co.moxomo.viewmodel;


import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import lombok.Getter;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.AlertDTO;
import za.co.moxomo.repository.Repository;

@Getter
public class AlertActivityViewModel extends ViewModel {
    private Executor executor;
    private Repository repository;
    private Gson gson;
    private MutableLiveData<String> progressLiveStatus;
    private MutableLiveData<String> resultSize;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<String> alertId = new MutableLiveData<>();

    AlertActivityViewModel(Repository repository, Gson gson){
        this.repository =repository;
        this.gson=gson;

    }


    public void createAlert(AlertDTO alertDTO) {
        repository.createAlert(alertDTO).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribe((JsonElement result) -> {
            progressLiveStatus.postValue(ApplicationConstants.LOADED);
            JSONObject object = new JSONObject(gson.toJson(result));
            alertId.postValue(object.getString("alertId"));

        });

    }


}