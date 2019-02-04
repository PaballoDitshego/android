package za.co.moxomo.repository;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import za.co.moxomo.service.RestAPIService;

public class Repository {

    private RestAPIService restAPIService;
    public static String SEARCH_STRING = null;

    public Repository(RestAPIService restAPIService) {
        this.restAPIService = restAPIService;
    }

    public Observable<JsonElement> fetchVacancies(String searchString, int pageNumber, int pageSize) {
        return restAPIService.fetchVacancies(searchString,pageNumber,pageSize);
    }


    public static void setSearchString(String searchString) {
        SEARCH_STRING = searchString;
    }
}
