package com.harsha.hotvpnpro.ipaddressapi;
/*Made By Harsha*/
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance(String base) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
