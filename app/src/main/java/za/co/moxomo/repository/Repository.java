package za.co.moxomo.repository;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import za.co.moxomo.service.RestAPIService;

public class Repository {

    private RestAPIService restAPIService;

    public Repository(RestAPIService restAPIService) {
        this.restAPIService = restAPIService;
    }

    public Observable<JsonElement> fetchVacancies(String searchString, int pageNumber, int pageSize) {
        return restAPIService.fetchVacancies(searchString,pageNumber,pageSize);
    }


}
