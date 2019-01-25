package za.co.moxomo.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.model.Vacancy;

public class VacancyClassDatasourceFactory extends DataSource.Factory<Integer, Vacancy>  {

    private MutableLiveData<VacancyDataSource> liveData;
    private Repository repository;
    private CompositeDisposable compositeDisposable;

    public VacancyClassDatasourceFactory(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        liveData = new MutableLiveData<>();

    }

    public MutableLiveData<VacancyDataSource> getMutableLiveData() {
        return liveData;
    }


    @Override
    public DataSource<Integer, Vacancy> create() {
        VacancyDataSource dataSourceClass = new VacancyDataSource(repository, compositeDisposable);
        liveData.postValue(dataSourceClass);

        return dataSourceClass;
    }



}
