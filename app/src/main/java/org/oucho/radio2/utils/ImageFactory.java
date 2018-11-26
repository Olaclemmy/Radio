/*
 * Radio - Internet radio for android
 * Copyright (C) 2017  Old-Geek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oucho.radio2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import org.oucho.radio2.R;
import org.oucho.radio2.RadioApplication;

import java.io.ByteArrayOutputStream;


public class ImageFactory {

    private static final String TAG = "ImageFactory";

    public static Bitmap resize(Bitmap image) {

        int logoSize = RadioApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.import_logo);

        int newWidth;
        int newHeight;

        if (image.getWidth() > image.getHeight()) {
            float aspectRatio = image.getWidth() / (float) image.getHeight();
            newWidth = logoSize;
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            float aspectRatio =  image.getHeight() / (float) image.getWidth();
            newHeight = logoSize;
            newWidth = Math.round(newHeight / aspectRatio);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

        image.recycle();
        return resizedBitmap;
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
            return stream.toByteArray();
        } catch (NullPointerException e) {
            Log.w(TAG, "Bitmap to byte[] conversion: " + e);
            return null;
        }
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {

        try {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } catch (NullPointerException e) {
            Log.w(TAG, "byte[] to bitmap conversion: " + e);
            return null;
        }
    }

    // convert from string to bitmap
    public static Bitmap stringToBitmap(String image) {
        byte[] img = Base64.decode(image, Base64.DEFAULT);
        return getImage(img);
    }


    // convert from byte array to string
    public static String byteToString(byte[] logo) {
        return bitmapToString(getImage(logo));
    }

    // convert from byte string to byte
    public static byte[] stringToByte(String logo) {
        return Base64.decode(logo, Base64.DEFAULT);
    }

    // convert from bitmap to string
    private static String bitmapToString(Bitmap image) {

        byte[] two = getBytes(image);

        try {
            return Base64.encodeToString(two, Base64.DEFAULT);
        } catch (NullPointerException e) {
            Log.w(TAG, "Bitmap to String conversion: " + e);
            return null;
        }
    }

    // convert from drawable to string
    @SuppressWarnings("SameParameterValue")
    public static String drawableResourceToBitmap(Context context, int drawable) {

        Bitmap one = BitmapFactory.decodeResource(context.getResources(), drawable);
        byte[] two = getBytes(one);
        return Base64.encodeToString(two, Base64.DEFAULT);
    }

}
