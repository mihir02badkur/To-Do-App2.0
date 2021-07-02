package com.example.to_do_app20;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST("auth/login/")
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);

    @POST("auth/register/")
    Call<UserResponse>  saveUser(@Body UserRequest userRequest);


    @GET("todo/")
    Call<List<GetToDoUserResponse>> getAllResponse(@Header("Authorization") String token);

    @POST("todo/create/")
    Call<ResponseBody> addNewTask(@Header("Authorization") String token, @Body NewTaskAdded newTaskAdded);

    @DELETE("todo/{id}/")
    Call<Void> deleteTask(@Header("Authorization") String token, @Path("id") int id);

    @PATCH("todo/{id}/")
    Call<EditResponse> editTask(@Header("Authorization") String token, @Path("id") int id, @Body EditRequest editRequest);

    @GET("auth/profile/")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);
}
