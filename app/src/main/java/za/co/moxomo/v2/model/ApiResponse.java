package za.co.moxomo.v2.model;

import com.google.gson.JsonElement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import za.co.moxomo.v2.service.Status;

import static za.co.moxomo.v2.service.Status.ERROR;
import static za.co.moxomo.v2.service.Status.SUCCESS;

public class ApiResponse {

    public final Status status;

    @Nullable
    public final JsonElement data;

    @Nullable
    public final Throwable error;

    private ApiResponse(Status status, @Nullable JsonElement data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }


    public static ApiResponse success(@NonNull JsonElement data) {
        return new ApiResponse(SUCCESS, data, null);
    }

    public static ApiResponse error(@NonNull Throwable error) {
        return new ApiResponse(ERROR, null, error);
    }

}