package za.co.moxomo.v2.repository;

;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;
import za.co.moxomo.v2.model.Vacancy;

public class VacancyClassDatasourceFactory extends DataSource.Factory<Integer, Vacancy> {

    private MutableLiveData<VacancyDataSource> liveData;
    private Repository repository;
    private CompositeDisposable compositeDisposable;
    private VacancyDataSource vacancyDataSource;

    public VacancyClassDatasourceFactory(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        liveData = new MutableLiveData<>();
        vacancyDataSource = new VacancyDataSource(repository, compositeDisposable);

    }

    public MutableLiveData<VacancyDataSource> getMutableLiveData() {
        return liveData;
    }


    @Override
    public DataSource<Integer, Vacancy> create() {
        vacancyDataSource = new VacancyDataSource(repository, compositeDisposable);
        liveData.postValue(vacancyDataSource);
        return vacancyDataSource;
    }

    public ReplaySubject<Vacancy> getVacancies() {
        return vacancyDataSource.getVacancies();
    }


}
