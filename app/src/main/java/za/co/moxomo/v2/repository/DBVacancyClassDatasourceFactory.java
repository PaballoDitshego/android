package za.co.moxomo.v2.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;
import za.co.moxomo.v2.model.Vacancy;


public class DBVacancyClassDatasourceFactory extends DataSource.Factory<Integer, Vacancy>  {

    private MutableLiveData<VacancyDataSource> liveData;
    private Repository repository;
    private CompositeDisposable compositeDisposable;
    private DBVacancyDataSource dataSourceClass;

    public DBVacancyClassDatasourceFactory(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;


    }

    @Override
    public DataSource<Integer, Vacancy> create() {
        dataSourceClass = new DBVacancyDataSource(repository, compositeDisposable);;
        return dataSourceClass;
    }

    public ReplaySubject<Vacancy> getVacancies() {
        return dataSourceClass.getVacancies();
    }



}
