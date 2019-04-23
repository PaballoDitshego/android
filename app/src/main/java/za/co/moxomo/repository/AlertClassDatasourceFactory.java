package za.co.moxomo.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.model.Alert;

;

public class AlertClassDatasourceFactory extends DataSource.Factory<Integer, Alert>  {

    private MutableLiveData<AlertDataSource> liveData;
    private Repository repository;
    private CompositeDisposable compositeDisposable;

    public AlertClassDatasourceFactory(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        liveData = new MutableLiveData<>();

    }

    public MutableLiveData<AlertDataSource> getMutableLiveData() {
        return liveData;
    }


    @Override
    public DataSource<Integer, Alert> create() {
        AlertDataSource dataSourceClass = new AlertDataSource(repository, compositeDisposable);
        liveData.postValue(dataSourceClass);

        return dataSourceClass;
    }



}
