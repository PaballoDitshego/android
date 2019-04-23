package za.co.moxomo.repository;

import com.google.gson.JsonElement;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.Notification;
import za.co.moxomo.service.RestApiService;

public class Repository {

    public static String SEARCH_STRING = null;
    private RestApiService restAPIService;
    private MoxomoDB moxomoDB;




    public Repository(RestApiService restAPIService, MoxomoDB moxomoDB) {
        this.restAPIService = restAPIService;
        this.moxomoDB = moxomoDB;
    }





    public Observable<JsonElement> fetchVacancies(String searchString, int pageNumber, int pageSize) {
        return restAPIService.fetchVacancies(searchString, pageNumber, pageSize);
    }

    public Observable<JsonElement> createAlert(Alert alert) {
        return restAPIService.createAlert(alert);
    }

    public Maybe<List<Notification>> fetchNotifications(int id, int size){
        return moxomoDB.notificationDao().getAllNotifications(id, size);
    }

/*

    public Maybe<List<Alert>> fetchAlerts(int id, int size){
        return moxomoDB.alertDao().getAllAlerts(id, size);
    }
*/




    public static void setSearchString(String searchString) {
        SEARCH_STRING = searchString;
    }
}
