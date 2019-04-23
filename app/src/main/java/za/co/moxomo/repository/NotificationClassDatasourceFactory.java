package za.co.moxomo.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.model.Notification;
import za.co.moxomo.model.Vacancy;

;

public class NotificationClassDatasourceFactory extends DataSource.Factory<Integer, Notification>  {

    private MutableLiveData<NotificationDataSource> liveData;
    private Repository repository;
    private CompositeDisposable compositeDisposable;

    public NotificationClassDatasourceFactory(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        liveData = new MutableLiveData<>();

    }

    public MutableLiveData<NotificationDataSource> getMutableLiveData() {
        return liveData;
    }


    @Override
    public DataSource<Integer, Notification> create() {
        NotificationDataSource dataSourceClass = new NotificationDataSource(repository, compositeDisposable);
        liveData.postValue(dataSourceClass);

        return dataSourceClass;
    }



}
