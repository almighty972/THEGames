package com.thegames.therightnumber.helpers;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationHelper {

    /**
     * Converts the given object to a base64 string
     *
     * @param object a given object
     * @return the base64 string representation of the given object
     */
    public static String objectToBase64String(Object object) {
        if(object != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                new ObjectOutputStream(out).writeObject(object);
                byte[] data = out.toByteArray();
                out.close();

                out = new ByteArrayOutputStream();
                Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
                b64.write(data);
                b64.close();
                out.close();

                return new String(out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * Converts the given base64 string to and Object
     *
     * @param base64String the given base64 string
     * @return the deserialized object
     */
    public static Object base64StringToObject(String base64String) {
        try {
            Object obj = new ObjectInputStream(new Base64InputStream(new ByteArrayInputStream(base64String.getBytes()), Base64.DEFAULT)).readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
