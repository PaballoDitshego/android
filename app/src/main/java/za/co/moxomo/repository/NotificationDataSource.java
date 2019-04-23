package za.co.moxomo.repository;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import io.reactivex.disposables.CompositeDisposable;
import za.co.moxomo.helpers.ApplicationConstants;
import za.co.moxomo.model.Notification;

public class NotificationDataSource extends PageKeyedDataSource<Integer, Notification> {


    private MutableLiveData<String> progressLiveStatus;
    private CompositeDisposable compositeDisposable;
    private Repository repository;

    NotificationDataSource(Repository repository, CompositeDisposable compositeDisposable) {
        this.repository = repository;
        this.compositeDisposable = compositeDisposable;
        progressLiveStatus = new MutableLiveData<>();


    }

    public MutableLiveData<String> getProgressLiveStatus() {
        return progressLiveStatus;
    }




    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Notification> callback) {

        repository.fetchNotifications(0, params.requestedLoadSize).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);

        }).subscribe(result ->
                {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    callback.onResult(result,0, result.size());
                },
                throwable -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    throwable.printStackTrace();

                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Notification> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Notification> callback) {

        repository.fetchNotifications(0, params.requestedLoadSize).doOnSubscribe(disposable -> {
            compositeDisposable.add(disposable);
            progressLiveStatus.postValue(ApplicationConstants.LOADING);

        }).subscribe(result ->
                {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    int nextKey = params.key+result.size();
                    callback.onResult(result,nextKey);
                },
                throwable -> {
                    progressLiveStatus.postValue(ApplicationConstants.LOADED);
                    throwable.printStackTrace();

                });
    }


}
