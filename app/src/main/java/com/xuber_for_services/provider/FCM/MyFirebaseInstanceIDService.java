package com.xuber_for_services.provider.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.xuber_for_services.provider.Helper.SharedHelper;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(),"device_token",""+refreshedToken);
        Log.e(TAG,""+refreshedToken);
    }
}