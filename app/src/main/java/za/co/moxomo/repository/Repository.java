package za.co.moxomo.repository;

import com.google.gson.JsonElement;

import java.util.List;

import androidx.paging.DataSource;
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

    public Observable<JsonElement> getLocationSuggestion(String location) {
        return restAPIService.getLocationSuggestions(location);
    }

    public void insertAlert(Alert alert) {
        moxomoDB.alertDao().insertAlert(alert);
    }

    public void updateAlertDBRecord(Alert alert) {
        moxomoDB.alertDao().update(alert);
    }

    public Observable<JsonElement> updateAlert(Alert alert) {
        return restAPIService.updateAlert(alert);
    }

    public DataSource.Factory<Integer, Alert> fetchAlerts() {
        return moxomoDB.alertDao().getAllAlerts();
    }

    public DataSource.Factory<Integer, Notification> fetchNotifications() {
        return moxomoDB.notificationDao().getAllNotifications();
    }

    public void insertNotification(Notification notification){
        moxomoDB.notificationDao().insertNotification(notification);
    }

    public void deleteAlert(Alert alert) {
        moxomoDB.alertDao().delete(alert);
    }

    public void deleteAllAlerts() {
        moxomoDB.alertDao().deleteAll();
    }

    public Observable<JsonElement> sendFCMTokenToServer(String newToken, String oldToken) {
        return restAPIService.sendToken(newToken, oldToken);
    }


    public static void setSearchString(String searchString) {
        SEARCH_STRING = searchString;
    }
}
