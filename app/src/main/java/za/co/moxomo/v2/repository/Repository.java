package za.co.moxomo.v2.repository;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.MainThread;
import androidx.paging.DataSource;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.Notification;
import za.co.moxomo.v2.model.Vacancy;
import za.co.moxomo.v2.service.RestApiService;

public class Repository {

    public static String SEARCH_STRING = null;
    public static double LATITUDE = 0.0;
    public static double LONGITUDE =0.0;
    public static int PAGE_NUMBER=1;
    private RestApiService restAPIService;
    private MoxomoDB moxomoDB;


    public Repository(RestApiService restAPIService, MoxomoDB moxomoDB) {
        this.restAPIService = restAPIService;
        this.moxomoDB = moxomoDB;

    }


    public Observable<JsonElement> fetchVacancies(String searchString,double latitude, double longitude, int pageNumber, int pageSize) {
        return restAPIService.fetchVacancies(searchString, latitude, longitude, pageNumber, pageSize);
    }

    public Call<JsonElement> fetchSearchResults(String searchString, int pageNumber, int pageSize) {
        return restAPIService.fetchSearchResults(searchString, LATITUDE, LONGITUDE, pageNumber,pageSize);
    }

    public Observable<JsonElement> createAlert(Alert alert) {
        return restAPIService.createAlert(alert);
    }

    public Observable<JsonElement> getLocationSuggestion(String location) {
        return restAPIService.getLocationSuggestions(location);
    }

    public Observable<JsonElement> getKeywordSuggestion(String keywords) {
        return restAPIService.getKeywords(keywords);
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

    public List<Vacancy> fetchDBVacancies() {
        return moxomoDB.vacancyDao().getAllDBVacancies();
    }

    public DataSource.Factory<Integer, Vacancy> fetchVacancies() {
        return moxomoDB.vacancyDao().getAllVacancies();
    }


    public void insertNotification(Notification notification) {
        moxomoDB.notificationDao().insertNotification(notification);
    }

    public void insertVacancy(Vacancy... vacancy) {
        moxomoDB.vacancyDao().insertVacancy(vacancy);
    }

    public void deleteAlert(Alert alert) {
        restAPIService.deleteAlert(alert.getAlertId()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(s -> {
            moxomoDB.alertDao().delete(alert);
        });

    }

    public void deleteAllAlerts() {
        restAPIService.deleteAllAlerts(moxomoDB.alertDao().getAlertIds()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(s -> {
            moxomoDB.alertDao().deleteAll();
        });
    }

    public Observable<JsonElement> sendFCMTokenToServer(String newToken, String oldToken) {
        return restAPIService.sendToken(newToken, oldToken);
    }


    public static void setSearchString(String searchString) {
        SEARCH_STRING = searchString;
    }

    public static void setPageNumber(int pageNumber){
        PAGE_NUMBER=pageNumber;
    }
}
