package com.harsha.hotvpnpro.utils;
/*Made By Harsha*/
import android.content.Context;
import android.content.SharedPreferences;

public class BillConfig {
    private static final String PREF_NAME = "snow-intro-slider";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    public static final String INAPPSKUUNIT = "inappskuunit";
    public static final String PURCHASETIME = "purchasetime";
    public static final String PRIMIUM_STATE = "primium_state";//boolean

    public static final String COUNTRY_DATA = "Country_data";
    public static final String BUNDLE = "Bundle";
    public static final String SELECTED_COUNTRY = "selected_country";

    public static final String IN_PURCHASE_KEY = "put_your_google_in_app_key_here";
    public static final String One_Month_Sub = "put_your_onemonth_subscription_id";
    public static final String Six_Month_Sub = "put_your_sixmonth_subscription_id";
    public static final String One_Year_Sub = "put_your_one_year_subscription_id";


    public BillConfig(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

}