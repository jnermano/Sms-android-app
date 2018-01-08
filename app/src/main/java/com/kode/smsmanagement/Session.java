package com.kode.smsmanagement;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ermano
 * on 1/7/2018.
 */

public class Session {

    private static Handler handler;

    public static Handler getHandler() {
        return handler;
    }

    public static void setHandler(Handler handler) {
        Session.handler = handler;
    }

    public static String getDateAndTime() {

        String[] months = new String[]{"Jan",  "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String dt = "";
        Calendar c = Calendar.getInstance();

        dt = String.format(Locale.US,
                "%02d %s %04d %02d:%02d:%02d",
                c.get(Calendar.DAY_OF_MONTH),
                (months[c.get(Calendar.MONTH)]),
                c.get(Calendar.YEAR),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND)
        );

        return dt;
    }

    public static String getOperatorName(Context context){
        // Get System TELEPHONY service reference
        TelephonyManager tManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tManager.getNetworkOperatorName();
    }

}
