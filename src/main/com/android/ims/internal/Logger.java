package com.android.ims.internal;

import android.text.TextUtils;
import android.util.Log;
/* loaded from: classes.dex */
public class Logger {
    private String mClassName;
    private static boolean VERBOSE = isLoggable(2);
    private static boolean DEBUG = isLoggable(3);
    private static boolean INFO = isLoggable(4);
    private static boolean WARN = isLoggable(5);
    private static boolean ERROR = isLoggable(6);
    private static boolean mRcsTestMode = false;
    private static String TAG = "rcs";

    private Logger(String tagName, String mClassName) {
        if (!TextUtils.isEmpty(tagName)) {
            TAG = tagName;
        }
        int index = mClassName.lastIndexOf(46);
        if (index != -1) {
            this.mClassName = mClassName.substring(index + 1);
        } else {
            this.mClassName = mClassName;
        }
    }

    public static void setRcsTestMode(boolean test) {
        mRcsTestMode = test;
        DEBUG = isLoggable(3);
        INFO = isLoggable(4);
        VERBOSE = isLoggable(2);
        WARN = isLoggable(5);
        ERROR = isLoggable(6);
    }

    private boolean isActivated() {
        return true;
    }

    public void verbose(String trace) {
        if (isActivated() && VERBOSE) {
            Log.d(TAG, "[" + this.mClassName + "] " + trace);
        }
    }

    public void debug(String trace) {
        if (isActivated() && DEBUG) {
            Log.d(TAG, "[" + this.mClassName + "] " + trace);
        }
    }

    public void debug(String trace, Throwable e) {
        if (isActivated() && DEBUG) {
            Log.d(TAG, "[" + this.mClassName + "] " + trace, e);
        }
    }

    public void info(String trace) {
        if (isActivated() && INFO) {
            Log.i(TAG, "[" + this.mClassName + "] " + trace);
        }
    }

    public void warn(String trace) {
        if (isActivated() && WARN) {
            Log.w(TAG, "[" + this.mClassName + "] " + trace);
        }
    }

    public void error(String trace) {
        if (isActivated() && ERROR) {
            Log.e(TAG, "[" + this.mClassName + "] " + trace);
        }
    }

    public void error(String trace, Throwable e) {
        if (isActivated() && ERROR) {
            Log.e(TAG, "[" + this.mClassName + "] " + trace, e);
        }
    }

    public void print(String trace) {
        Log.i(TAG, "[" + this.mClassName + "] " + trace);
    }

    public void print(String trace, Throwable e) {
        Log.i(TAG, "[" + this.mClassName + "] " + trace, e);
    }

    public static String hidePhoneNumberPii(String number) {
        if (TextUtils.isEmpty(number) || mRcsTestMode || number.length() <= 2) {
            return number;
        }
        StringBuilder sb = new StringBuilder(number.length());
        sb.append("...*");
        sb.append(number.substring(number.length() - 2, number.length()));
        return sb.toString();
    }

    private static boolean isLoggable(int level) {
        return mRcsTestMode || Log.isLoggable(TAG, level);
    }

    public static synchronized Logger getLogger(String tagName, String classname) {
        Logger logger;
        synchronized (Logger.class) {
            logger = new Logger(tagName, classname);
        }
        return logger;
    }

    public static synchronized Logger getLogger(String classname) {
        Logger logger;
        synchronized (Logger.class) {
            logger = new Logger(TAG, classname);
        }
        return logger;
    }
}
