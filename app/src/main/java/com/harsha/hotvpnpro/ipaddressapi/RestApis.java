package com.harsha.hotvpnpro.ipaddressapi;

/*Made By Harsha*/
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApis {
    @GET("/")
    Call<ApiResponse> requestip(@Query("format") String formate);


}
