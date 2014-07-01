package com.thegames.therightnumber.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class FileHelper {

    public static String getFileContentFromAssets(Context context, String filename) {
        String content = null;
        if(context != null) {
            try {
                StringBuilder buf=new StringBuilder();
                InputStream json = context.getAssets().open(filename);
                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF8"));
                String str;

                while ((str = in.readLine()) != null) {
                    buf.append(str);
                }

                in.close();
                content = buf.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}
