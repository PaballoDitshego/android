package za.co.moxomo.v2.service;

import com.google.gson.JsonElement;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.UserDTO;

public interface RestApiService {

    @GET("/vacancies")
    Observable<JsonElement> fetchVacancies(@Query("searchString") String searchString,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit);
    @GET("/users/signup")
    Observable<JsonElement> signup(@Body UserDTO userDTO);

    @POST("/alerts/create")
    Observable<JsonElement> createAlert(@Body Alert alert);

    @PUT("/alerts/update")
    Observable<JsonElement> updateAlert(@Body Alert alerts);

    @DELETE("/alerts/delete/{id}")
    Observable<JsonElement> deleteAlert(@Path("id") String id);

    @POST("/alerts/delete-all")
    Observable<JsonElement> deleteAllAlerts(@Body List<String>ids);

    @POST("/alerts/fcmtoken")
    Observable<JsonElement> sendToken(@Query("newToken") String newToken,
                                  @Query("oldToken") String oldToken);

    @GET("/locations")
    Observable<JsonElement> getLocationSuggestions(@Query("location") String location);

    @GET("/keywords")
    Observable<JsonElement> getKeywords(@Query("term") String location);


}
