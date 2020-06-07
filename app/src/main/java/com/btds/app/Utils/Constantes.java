package com.btds.app.Utils;

/**
 * @author Alejandro Molina Louchnikov
 */
public class Constantes {

    public static final String default_history_image = "https://www.staffcreativa.pe/blog/wp-content/uploads/logos9.gif";
    public static final String  default_image_profile = "https://res.cloudinary.com/teepublic/image/private/s--6vDtUIZ---/t_Resized%20Artwork/c_fit,g_north_west,h_1054,w_1054/co_ffffff,e_outline:53/co_ffffff,e_outline:inner_fill:53/co_bbbbbb,e_outline:3:1000/c_mpad,g_center,h_1260,w_1260/b_rgb:eeeeee/c_limit,f_jpg,h_630,q_90,w_630/v1570281377/production/designs/6215195_0.jpg";
    static String APP_ID;
    static String AUTH_KEY;
    static String AUTH_SECRET;
    static String ACCOUNT_KEY;
    public static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

//



    public Constantes(){

    }

    public Constantes(String appID, String AUTH_KEY, String AUTH_SECRET, String ACCOUNT_KEY){

        APP_ID = appID;
        Constantes.AUTH_KEY = AUTH_KEY;
        Constantes.AUTH_SECRET = AUTH_SECRET;
        Constantes.ACCOUNT_KEY = ACCOUNT_KEY;

        //QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        //QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static void setAppId(String appId) {
        APP_ID = appId;
    }

    public static String getAuthKey() {
        return AUTH_KEY;
    }

    public static void setAuthKey(String authKey) {
        AUTH_KEY = authKey;
    }

    public static String getAuthSecret() {
        return AUTH_SECRET;
    }

    public static void setAuthSecret(String authSecret) {
        AUTH_SECRET = authSecret;
    }

    public static String getAccountKey() {
        return ACCOUNT_KEY;
    }

    public static void setAccountKey(String accountKey) {
        ACCOUNT_KEY = accountKey;
    }
}
