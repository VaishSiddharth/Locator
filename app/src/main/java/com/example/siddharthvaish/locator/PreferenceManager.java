package com.example.siddharthvaish.locator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;



import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

public class PreferenceManager {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static final String JWT = "jwt_Token";
    public static final String FIRST_NAME = "first name";
    public static final String LAST_NAME = "last name";
    public static final String GENDER = "gender";
    public static final String EMAIL = "email_id";
    public static final String PHONE_NUMBER = "phone number";
    public static final String PROFILE_PIC = "profile pic";
    public static final String PROFILE_PIC_LOCAL = "profile pic local";
    public static final String USER_ID = "user id";


    public static final String PROFILE_ID = "profile id";


    private PreferenceManager() {

    }



    public static void init(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void clear(Context context) {
        preferences.edit()
                .clear()
                .commit();
    }

    public static String getStringValue(String key) {
        return preferences.getString(key, "");
    }

    public static void setStringValue(String key, String value) {
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static boolean getBoolValue(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void setBoolValue(String key, boolean value) {
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Integer getIntValue(String key) {
        return preferences.getInt(key, 0);
    }

    public static void setIntValue(String key, Integer value) {
        editor = preferences.edit();
        editor.putInt(key, value).apply();
    }

    public static void setProfileImage(String key, Bitmap value) {
        editor.putString(PROFILE_PIC_LOCAL, encodeTobase64(value));
        editor.commit();
    }
    public static Bitmap getProfileImage(String key) {
        return decodeBase64(getStringValue(key));

    }





    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }



    /*public static Userdata getSavedUser() {
        Userdata user = null;

        String first_name =getStringValue(FIRST_NAME);
        String last_name =getStringValue(LAST_NAME);
        String name = new StringWriter()
                .append(first_name)
                .append(" ")
                .append(last_name)
                .toString();
        String mobile = getStringValue(PHONE_NUMBER);
        String gender =getStringValue(GENDER);
        String email =getStringValue(EMAIL);
        String user_id= getStringValue(USER_ID);
        String profile_pic_local_base64 = getStringValue(PROFILE_PIC_LOCAL);



        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile)) {
            user = new Userdata();
            user.setFirstName(getStringValue(FIRST_NAME));
            user.setName(name);
            user.setFirstName(first_name);
            user.setLastName(last_name);
            user.setMobile(mobile);
            user.setGender(gender);
            user.setEmailId(email);
            user.setId(user_id);

        }

        return user;
    }

    public static GetSwipeLogResponse getlastswipe() {
        GetSwipeLogResponse lastswipe = null;

        String merchant_id =getStringValue(LAST_SWIPE_MERCHANT_ID);
        String url_swiped =getStringValue(LAST_SWIPE_URL_SWIPED);
        String purpose =getStringValue(LAST_SWIPE_PURPOSE);
        String createdAt = getStringValue(LAST_SWIPE_TIME);

            lastswipe = new GetSwipeLogResponse();
            lastswipe.setMerchantId(merchant_id);
            lastswipe.setUrlSwiped(url_swiped);
            lastswipe.setPurpose(purpose);
            lastswipe.setCreatedAt(createdAt);


        return lastswipe;
    }
*/

}
