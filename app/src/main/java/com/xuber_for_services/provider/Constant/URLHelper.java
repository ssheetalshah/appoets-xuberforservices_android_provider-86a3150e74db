package com.xuber_for_services.provider.Constant;

public class URLHelper {

    //Application WEB Connectivity Details
    public static final String BASE_URL = "http://erapidservice.com/public";
//    public static final String BASE_URL = "http://ihisaab.in/toolplus/public";
    //    public static final String BASE_URL = "http://103.120.178.229:2082";
    //  public static final String BASE_URL = "http://service.tranxit.co";
    public static final int CLIENT_ID = 2;
    public static final String CLIENT_SECRET_KEY = "HzMMk2fbmxUx8nCEsrTawxHCHXGfdIHmMubq6QyI";
    public static final String APP_LINK = "https://play.google.com/store/apps/details?id=com.xuber_for_services.provider";

    //Help
    public static final String HELP_REDIRECT_URL = BASE_URL + "";

    //Image load options URL
    public static final String BASE_IMAGE_LOAD_URL_WITH_STORAGE = BASE_URL + "/storage/";


    //WEB API LIST
    public static final String LOGIN = BASE_URL + "/api/provider/oauth/token";
    public static final String REGISTER = BASE_URL + "/api/provider/register";
    public static final String PROVIDER_PROFILE = BASE_URL + "/api/provider/profile";
    public static final String PROVIDER_PROFILE_UPDATE = BASE_URL + "/api/provider/profile";
    public static final String CANCEL_REQUEST_API = BASE_URL + "/api/provider/cancel";
    public static final String GET_HISTORY_API = BASE_URL + "/api/provider/trips";
    public static final String GET_HISTORY_DETAILS_API = BASE_URL + "/api/provider/requests/history/details";
    public static final String CHANGE_PASSWORD_API = BASE_URL + "/api/provider/profile/password";
    public static final String UPCOMING_TRIP_DETAILS = BASE_URL + "/api/provider/requests/upcoming/details";
    public static final String UPCOMING_TRIPS = BASE_URL + "/api/provider/requests/upcoming";
    public static final String GET_HISTORY_REQUEST = BASE_URL + "/api/provider/requests/history";
    public static final String UPDATE_SERVICE = BASE_URL + "/api/provider/update/service";
    public static final String UPDATE_SUB_SERVICE = BASE_URL + "/api/provider/update/sub_service";
    public static final String GET_SERVICES = BASE_URL + "/api/provider/services";
    public static final String GET_SUBSERVICES = BASE_URL + "/api/provider/sub_services?service_id=";
    public static final String UPDATE_AVAILABILITY_API = BASE_URL + "/api/provider/profile/available";
    public static final String RESET_PASSWORD = BASE_URL + "/api/provider/reset/password";
    public static final String FORGET_PASSWORD = BASE_URL + "/api/provider/forgot/password";
    public static final String KYC_REGISTER = BASE_URL + "/api/provider/kyc_reg";
    public static final String SHOW_PROFILE = BASE_URL + "/api/provider/user";
    public static final String LOGOUT = BASE_URL + "/api/provider/logout";
    public static final String SUMMARY = BASE_URL + "/api/provider/summary";
    public static final String GET_HELP_DETAILS = BASE_URL + "/api/provider/help";
    public static final String GET_TARGET_FOR_SUMMARY = BASE_URL + "/api/provider/target";

}
