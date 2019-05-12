package za.co.moxomo.viewmodel;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.AlertResponse;
import za.co.moxomo.model.ApiResponse;
import za.co.moxomo.repository.Repository;


@Getter
public class AlertActivityViewModel extends ViewModel {

    private static final String TAG = AlertActivityViewModel.class.getSimpleName();

    private Repository repository;
    private Gson gson;
    private Executor executor;

    private LiveData<PagedList<Alert>> alerts;
    private MutableLiveData<String> progressLiveStatus = new MutableLiveData<>();
    private LiveData<String> progressLoadStatus = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<ApiResponse> alertServiceResponse = new MutableLiveData<>();
    private MutableLiveData<List<String>> locationSuggestions = new MutableLiveData<>();

    private MutableLiveData<Alert> alertObjectHolder = new MutableLiveData<>();

    public AlertActivityViewModel(Repository repository) {
        this.repository = repository;
        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.setLenient().create();
        initializePaging();
    }

    private void initializePaging() {

        PagedList.Config pagedListConfig =
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(15)
                        .setPageSize(5).build();


        executor = Executors.newFixedThreadPool(5);
        alerts = new LivePagedListBuilder(repository.fetchAlerts(), pagedListConfig)
                .setFetchExecutor(executor)
                .build();


    }

    public void createAlert(Alert alert) {
        Log.d(TAG, alert.toString());
        repository.createAlert(alert).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((d) -> progressLiveStatus.setValue(ApplicationConstants.LOADING)).subscribe(result -> {
                    Log.d(TAG, "result " +result.toString());
                    AlertResponse alertResponse = gson.fromJson(result, AlertResponse.class);
                    alert.setAlertId(alertResponse.getId());
                    alert.setTimestamp(DateTime.now());
                    executor.execute(() -> repository.insertAlert(alert));
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertServiceResponse.setValue(ApiResponse.success(result));
                }
                , throwable -> {
                    throwable.printStackTrace();
                    Log.d(TAG,"error "+ throwable.getLocalizedMessage());
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertServiceResponse.postValue(ApiResponse.error(throwable));
                });

    }

    public void updateAlert(Alert alert) {
        repository.updateAlert(alert).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((d) -> progressLiveStatus.setValue(ApplicationConstants.LOADING)).subscribe(result -> {
                    AlertResponse alertResponse = gson.fromJson(result, AlertResponse.class);
                    Log.d(TAG, result.getAsString());
                    alert.setAlertId(alertResponse.getId());
                    executor.execute(() -> repository.updateAlertDBRecord(alert));
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertServiceResponse.setValue(ApiResponse.success(result));
                }
                , throwable -> {
                    progressLiveStatus.setValue(ApplicationConstants.LOADED);
                    alertServiceResponse.postValue(ApiResponse.error(throwable));
                });

    }

    public void getLocationSuggestion(String location) {
        repository.getLocationSuggestion(location).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    List<String> suggestions = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                    locationSuggestions.setValue(suggestions);
                }
                , throwable -> {
                    
                });

    }

    public void deleteAlert(Alert alert) {
        //network call to delete
        executor.execute(() -> repository.deleteAlert(alert));

    }

    public void deleteAllAlerts(){
        //network call to delete
        executor.execute(()-> repository.deleteAllAlerts());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}