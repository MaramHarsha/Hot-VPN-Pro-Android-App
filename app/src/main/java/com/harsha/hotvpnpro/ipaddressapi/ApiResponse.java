package com.harsha.hotvpnpro.ipaddressapi;
/*Made By Harsha*/
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("ip")
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
