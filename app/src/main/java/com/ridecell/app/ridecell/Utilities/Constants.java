package com.ridecell.app.ridecell.Utilities;


import com.ridecell.app.ridecell.DataModels.SpotData;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String BASE_URL = "http://ridecellparking.herokuapp.com" ;


    public static final String GOOGLE_STREET_VIEW_API_KEY = "AIzaSyAj1V4NSywma1H9eKi_zeCG8f-punlpgNc";
    public static final String ADDR_NAME = "addrName";
    public static final String THE_ADDRESS = "theAddress";
    public static final String LOCATION = "location";
    public static final String DISTANCE = "distance";
    public static final String SEEKBAR_MINUTES = "minutes";
    public static final String DATE_TIME_STRING = "dateTimeStr";
    public static SpotData SPOTDATA;
    public static final String MINUTES_KEY = "minutesKey";
    public static final String SHARED_PREFS = "sharedPrefs";


    public static final double MILES_IN_METER = 0.000621371;

    public static final Map<String, String> STATE_MAP;
    static {
        STATE_MAP = new HashMap<String, String>();
        STATE_MAP.put("Alabama", "AL");
        STATE_MAP.put("Alaska", "AK");
        STATE_MAP.put("Alberta", "AB");
        STATE_MAP.put("Arizona", "AZ");
        STATE_MAP.put("Arkansas", "AR");
        STATE_MAP.put("British Columbia", "BC");
        STATE_MAP.put("California", "CA");
        STATE_MAP.put("Colorado", "CO");
        STATE_MAP.put("Connecticut", "CT");
        STATE_MAP.put("Delaware", "DE");
        STATE_MAP.put("District Of Columbia", "DC");
        STATE_MAP.put("Florida", "FL");
        STATE_MAP.put("Georgia", "GA");
        STATE_MAP.put("Guam", "GU");
        STATE_MAP.put("Hawaii", "HI");
        STATE_MAP.put("Idaho", "ID");
        STATE_MAP.put("Illinois", "IL");
        STATE_MAP.put("Indiana", "IN");
        STATE_MAP.put("Iowa", "IA");
        STATE_MAP.put("Kansas", "KS");
        STATE_MAP.put("Kentucky", "KY");
        STATE_MAP.put("Louisiana", "LA");
        STATE_MAP.put("Maine", "ME");
        STATE_MAP.put("Manitoba", "MB");
        STATE_MAP.put("Maryland", "MD");
        STATE_MAP.put("Massachusetts", "MA");
        STATE_MAP.put("Michigan", "MI");
        STATE_MAP.put("Minnesota", "MN");
        STATE_MAP.put("Mississippi", "MS");
        STATE_MAP.put("Missouri", "MO");
        STATE_MAP.put("Montana", "MT");
        STATE_MAP.put("Nebraska", "NE");
        STATE_MAP.put("Nevada", "NV");
        STATE_MAP.put("New Brunswick", "NB");
        STATE_MAP.put("New Hampshire", "NH");
        STATE_MAP.put("New Jersey", "NJ");
        STATE_MAP.put("New Mexico", "NM");
        STATE_MAP.put("New York", "NY");
        STATE_MAP.put("Newfoundland", "NF");
        STATE_MAP.put("North Carolina", "NC");
        STATE_MAP.put("North Dakota", "ND");
        STATE_MAP.put("Northwest Territories", "NT");
        STATE_MAP.put("Nova Scotia", "NS");
        STATE_MAP.put("Nunavut", "NU");
        STATE_MAP.put("Ohio", "OH");
        STATE_MAP.put("Oklahoma", "OK");
        STATE_MAP.put("Ontario", "ON");
        STATE_MAP.put("Oregon", "OR");
        STATE_MAP.put("Pennsylvania", "PA");
        STATE_MAP.put("Prince Edward Island", "PE");
        STATE_MAP.put("Puerto Rico", "PR");
        STATE_MAP.put("Quebec", "QC");
        STATE_MAP.put("Rhode Island", "RI");
        STATE_MAP.put("Saskatchewan", "SK");
        STATE_MAP.put("South Carolina", "SC");
        STATE_MAP.put("South Dakota", "SD");
        STATE_MAP.put("Tennessee", "TN");
        STATE_MAP.put("Texas", "TX");
        STATE_MAP.put("Utah", "UT");
        STATE_MAP.put("Vermont", "VT");
        STATE_MAP.put("Virgin Islands", "VI");
        STATE_MAP.put("Virginia", "VA");
        STATE_MAP.put("Washington", "WA");
        STATE_MAP.put("West Virginia", "WV");
        STATE_MAP.put("Wisconsin", "WI");
        STATE_MAP.put("Wyoming", "WY");
        STATE_MAP.put("Yukon Territory", "YT");
    }

    public static String getStateAbbr(String state) {
        if (STATE_MAP.containsKey(state)) {
            return STATE_MAP.get(state);
        }else{
            return state;
        }
    }





}
