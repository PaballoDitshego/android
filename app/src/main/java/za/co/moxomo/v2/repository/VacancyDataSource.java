package za.co.moxomo.v2.repository;



import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.model.Vacancy;

public class VacancyDataSource extends PageKeyedDataSource<Integer, Vacancy> {

    private Repository repository;
    private Gson gson;
    private MutableLiveData<String> progressLiveStatus;
    private MutableLiveData<String> resultSize;
    private CompositeDisposable compositeDisposable;



    public MutableLiveData<String> getProgressLiveStatus() {
        return progressLiveStatus;
    }

    public MutableLiveData<String> getResultSize() {
        return resultSize;
    }

    public void setSearchString(String searchString) {
        Repository.setSearchString(searchString);
        invalidate();

    }

    public void pullToRefresh(){
        invalidate();
    }


    VacancyDataSource(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        progressLiveStatus = new MutableLiveData<>();
        resultSize = new MutableLiveData<>();

        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.setLenient().create();
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Vacancy> callback) {

        repository.fetchVacancies(Repository.SEARCH_STRING, 1, params.requestedLoadSize).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);

        }).subscribe((JsonElement result) ->
                {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    JSONObject object = new JSONObject(gson.toJson(result));
                    long resultSetSize = Utility.getResultSetSize(object);
                    resultSize.postValue(String.valueOf(resultSetSize));
                    List<Vacancy> arrayList = Utility.parse(object);
                    callback.onResult(arrayList, -1, 2);
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
            progressLiveStatus.postValue(ApplicationConstants.LOADING);
        }).subscribe((JsonElement result) ->
                {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    JSONObject object = new JSONObject(gson.toJson(result));
                    List<Vacancy> arrayList = Utility.parse(object);
                    callback.onResult(arrayList, params.key + 1);

                },
                throwable -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    throwable.printStackTrace();
                });
    }


}
