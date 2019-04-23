package za.co.moxomo.service;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.UserDTO;

public interface RestApiService {

    @GET("/vacancies")
    Observable<JsonElement> fetchVacancies(@Query("searchString") String searchString,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit);
    @GET("/users/signup")
    Observable<JsonElement> signup(@Body UserDTO userDTO);

    @POST("/alert/create")
    Observable<JsonElement> createAlert(@Body Alert alert);

}
