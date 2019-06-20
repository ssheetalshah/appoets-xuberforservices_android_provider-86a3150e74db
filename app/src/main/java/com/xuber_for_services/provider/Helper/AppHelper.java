package com.xuber_for_services.provider.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.xuber_for_services.provider.Constant.URLHelper;

import java.io.ByteArrayOutputStream;

public class AppHelper {
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Drawable drawable) {
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            return byteArrayOutputStream.toByteArray();
        }

    }


    public static String getImageUrl(String imgUrl) {
        if (imgUrl != null && imgUrl.length() > 0) {
            if (imgUrl.startsWith("http")) {
                return imgUrl;
            } else {
                return URLHelper.BASE_IMAGE_LOAD_URL_WITH_STORAGE + imgUrl;
            }
        } else
            return "";
    }

}
