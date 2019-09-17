package za.co.moxomo.v2.repository;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.model.Vacancy;

public class DBVacancyDataSource extends PageKeyedDataSource<Integer, Vacancy> {

    private Repository repository;
    private Gson gson;
    private MutableLiveData<String> progressLiveStatus;
    private MutableLiveData<String> resultSize;
    private ReplaySubject<Vacancy> vacancyReplaySubject;
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

    public void pullToRefresh() {
        invalidate();
    }


    DBVacancyDataSource(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        progressLiveStatus = new MutableLiveData<>();
        resultSize = new MutableLiveData<>();
        vacancyReplaySubject = ReplaySubject.create();

        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = builder.setLenient().create();
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Vacancy> callback) {
        List<Vacancy> vacancies =  repository.fetchDBVacancies();
        if(vacancies.size() !=0){
            callback.onResult(vacancies, 0, 1);
        }

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Vacancy> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Vacancy> callback) {

    }

    public ReplaySubject<Vacancy> getVacancies() {
        return vacancyReplaySubject;
    }
}
