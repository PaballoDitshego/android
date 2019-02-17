package za.co.moxomo.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.helpers.Utility;
import za.co.moxomo.model.Vacancy;

public class VacancyDataSource extends PageKeyedDataSource<Integer, Vacancy> {

    private Repository repository;
    private Gson gson;
    private MutableLiveData<String> progressLiveStatus;
    private CompositeDisposable compositeDisposable;
    private int pageNumber = 1;


    public MutableLiveData<String> getProgressLiveStatus() {
        return progressLiveStatus;
    }

    public void setSearchString(String searchString) {
        Repository.setSearchString(searchString);
        invalidate();

    }


    VacancyDataSource(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        progressLiveStatus = new MutableLiveData<>();

        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.setLenient().create();
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Vacancy> callback) {

        repository.fetchVacancies(Repository.SEARCH_STRING, pageNumber, params.requestedLoadSize).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);

        }).subscribe((JsonElement result) ->
                {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    JSONObject object = new JSONObject(gson.toJson(result));
                    List<Vacancy> arrayList = Utility.parse(object);
                    pageNumber++;
                    callback.onResult(arrayList, -1, pageNumber);
                },
                throwable -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    throwable.printStackTrace();

                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Vacancy> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Vacancy> callback) {
        repository.fetchVacancies(Repository.SEARCH_STRING, params.key, params.requestedLoadSize).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);

        }).subscribe((JsonElement result) ->
                {
                    JSONObject object = new JSONObject(gson.toJson(result));
                    List<Vacancy> arrayList = Utility.parse(object);
                    callback.onResult(arrayList, params.key + 1);
                },
                throwable -> {
                    throwable.printStackTrace();
                });
    }


}
