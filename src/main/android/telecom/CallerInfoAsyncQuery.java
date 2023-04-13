package android.telecom;

import android.app.ActivityManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class CallerInfoAsyncQuery {
    private static final boolean DBG = false;
    private static final boolean ENABLE_UNKNOWN_NUMBER_GEO_DESCRIPTION = true;
    private static final int EVENT_ADD_LISTENER = 2;
    private static final int EVENT_EMERGENCY_NUMBER = 4;
    private static final int EVENT_END_OF_QUEUE = 3;
    private static final int EVENT_GET_GEO_DESCRIPTION = 6;
    private static final int EVENT_NEW_QUERY = 1;
    private static final int EVENT_VOICEMAIL_NUMBER = 5;
    private static final String LOG_TAG = "CallerInfoAsyncQuery";
    private CallerInfoAsyncQueryHandler mHandler;

    /* loaded from: classes3.dex */
    public interface OnQueryCompleteListener {
        void onQueryComplete(int i, Object obj, CallerInfo callerInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class CookieWrapper {
        public Object cookie;
        public int event;
        public String geoDescription;
        public OnQueryCompleteListener listener;
        public String number;
        public int subId;

        private CookieWrapper() {
        }
    }

    /* loaded from: classes3.dex */
    public static class QueryPoolException extends SQLException {
        public QueryPoolException(String error) {
            super(error);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ContentResolver getCurrentProfileContentResolver(Context context) {
        int currentUser = ActivityManager.getCurrentUser();
        int myUser = UserManager.get(context).getProcessUserId();
        if (myUser != currentUser) {
            try {
                Context otherContext = context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.m145of(currentUser));
                return otherContext.getContentResolver();
            } catch (PackageManager.NameNotFoundException e) {
                Log.m136e(LOG_TAG, (Throwable) e, "Can't find self package", new Object[0]);
            }
        }
        return context.getContentResolver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CallerInfoAsyncQueryHandler extends AsyncQueryHandler {
        private CallerInfo mCallerInfo;
        private Context mContext;
        private List<Runnable> mPendingListenerCallbacks;
        private Uri mQueryUri;

        /* loaded from: classes3.dex */
        protected class CallerInfoWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CallerInfoWorkerHandler(Looper looper) {
                super(looper);
            }

            @Override // android.content.AsyncQueryHandler.WorkerHandler, android.p008os.Handler
            public void handleMessage(Message msg) {
                AsyncQueryHandler.WorkerArgs args = (AsyncQueryHandler.WorkerArgs) msg.obj;
                CookieWrapper cw = (CookieWrapper) args.cookie;
                if (cw == null) {
                    Log.m134i(CallerInfoAsyncQuery.LOG_TAG, "Unexpected command (CookieWrapper is null): " + msg.what + " ignored by CallerInfoWorkerHandler, passing onto parent.", new Object[0]);
                    super.handleMessage(msg);
                    return;
                }
                Log.m138d(CallerInfoAsyncQuery.LOG_TAG, "Processing event: " + cw.event + " token (arg1): " + msg.arg1 + " command: " + msg.what + " query URI: " + CallerInfoAsyncQuery.sanitizeUriToString(args.uri), new Object[0]);
                switch (cw.event) {
                    case 1:
                        super.handleMessage(msg);
                        return;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        Message reply = args.handler.obtainMessage(msg.what);
                        reply.obj = args;
                        reply.arg1 = msg.arg1;
                        reply.sendToTarget();
                        return;
                    case 6:
                        handleGeoDescription(msg);
                        return;
                    default:
                        return;
                }
            }

            private void handleGeoDescription(Message msg) {
                AsyncQueryHandler.WorkerArgs args = (AsyncQueryHandler.WorkerArgs) msg.obj;
                CookieWrapper cw = (CookieWrapper) args.cookie;
                if (!TextUtils.isEmpty(cw.number) && cw.cookie != null && CallerInfoAsyncQueryHandler.this.mContext != null) {
                    long startTimeMillis = SystemClock.elapsedRealtime();
                    cw.geoDescription = CallerInfo.getGeoDescription(CallerInfoAsyncQueryHandler.this.mContext, cw.number);
                    long elapsedRealtime = SystemClock.elapsedRealtime() - startTimeMillis;
                }
                Message reply = args.handler.obtainMessage(msg.what);
                reply.obj = args;
                reply.arg1 = msg.arg1;
                reply.sendToTarget();
            }
        }

        private CallerInfoAsyncQueryHandler(Context context) {
            super(CallerInfoAsyncQuery.getCurrentProfileContentResolver(context));
            this.mPendingListenerCallbacks = new ArrayList();
            this.mContext = context;
        }

        @Override // android.content.AsyncQueryHandler
        protected Handler createHandler(Looper looper) {
            return new CallerInfoWorkerHandler(looper);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(final int token, Object cookie, Cursor cursor) {
            int i;
            Log.m138d(CallerInfoAsyncQuery.LOG_TAG, "##### onQueryComplete() #####   query complete for token: " + token, new Object[0]);
            final CookieWrapper cw = (CookieWrapper) cookie;
            if (cw == null) {
                Log.m134i(CallerInfoAsyncQuery.LOG_TAG, "Cookie is null, ignoring onQueryComplete() request.", new Object[0]);
                if (cursor != null) {
                    cursor.close();
                }
            } else if (cw.event == 3) {
                for (Runnable r : this.mPendingListenerCallbacks) {
                    r.run();
                }
                this.mPendingListenerCallbacks.clear();
                CallerInfoAsyncQuery.this.release();
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                if (cw.event != 6) {
                    i = 6;
                } else {
                    CallerInfo callerInfo = this.mCallerInfo;
                    if (callerInfo != null) {
                        callerInfo.geoDescription = cw.geoDescription;
                    }
                    CookieWrapper endMarker = new CookieWrapper();
                    endMarker.event = 3;
                    i = 6;
                    startQuery(token, endMarker, null, null, null, null, null);
                }
                if (this.mCallerInfo == null) {
                    if (this.mContext == null || this.mQueryUri == null) {
                        throw new QueryPoolException("Bad context or query uri, or CallerInfoAsyncQuery already released.");
                    }
                    if (cw.event == 4) {
                        this.mCallerInfo = new CallerInfo().markAsEmergency(this.mContext);
                    } else if (cw.event == 5) {
                        this.mCallerInfo = new CallerInfo().markAsVoiceMail(this.mContext, cw.subId);
                    } else {
                        this.mCallerInfo = CallerInfo.getCallerInfo(this.mContext, this.mQueryUri, cursor);
                        CallerInfo newCallerInfo = CallerInfo.doSecondaryLookupIfNecessary(this.mContext, cw.number, this.mCallerInfo);
                        if (newCallerInfo != this.mCallerInfo) {
                            this.mCallerInfo = newCallerInfo;
                        }
                        if (!TextUtils.isEmpty(cw.number)) {
                            this.mCallerInfo.setPhoneNumber(PhoneNumberUtils.formatNumber(cw.number, this.mCallerInfo.normalizedNumber, CallerInfo.getCurrentCountryIso(this.mContext)));
                        }
                        if (TextUtils.isEmpty(this.mCallerInfo.getName())) {
                            cw.event = i;
                            startQuery(token, cw, null, null, null, null, null);
                            return;
                        }
                    }
                    CookieWrapper endMarker2 = new CookieWrapper();
                    endMarker2.event = 3;
                    startQuery(token, endMarker2, null, null, null, null, null);
                }
                if (cw.listener != null) {
                    this.mPendingListenerCallbacks.add(new Runnable() { // from class: android.telecom.CallerInfoAsyncQuery.CallerInfoAsyncQueryHandler.1
                        @Override // java.lang.Runnable
                        public void run() {
                            cw.listener.onQueryComplete(token, cw.cookie, CallerInfoAsyncQueryHandler.this.mCallerInfo);
                        }
                    });
                } else {
                    Log.m130w(CallerInfoAsyncQuery.LOG_TAG, "There is no listener to notify for this query.", new Object[0]);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private CallerInfoAsyncQuery() {
    }

    public static CallerInfoAsyncQuery startQuery(int token, Context context, Uri contactRef, OnQueryCompleteListener listener, Object cookie) {
        CallerInfoAsyncQuery c = new CallerInfoAsyncQuery();
        c.allocate(context, contactRef);
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.event = 1;
        c.mHandler.startQuery(token, cw, contactRef, null, null, null, null);
        return c;
    }

    public static CallerInfoAsyncQuery startQuery(int token, Context context, String number, OnQueryCompleteListener listener, Object cookie) {
        int subId = SubscriptionManager.getDefaultSubscriptionId();
        return startQuery(token, context, number, listener, cookie, subId);
    }

    public static CallerInfoAsyncQuery startQuery(int token, Context context, String number, OnQueryCompleteListener listener, Object cookie, int subId) {
        boolean isEmergencyNumber;
        Uri contactRef = ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI.buildUpon().appendPath(number).appendQueryParameter("sip", String.valueOf(PhoneNumberUtils.isUriNumber(number))).build();
        CallerInfoAsyncQuery c = new CallerInfoAsyncQuery();
        c.allocate(context, contactRef);
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.number = number;
        cw.subId = subId;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        try {
            isEmergencyNumber = tm.isEmergencyNumber(number);
        } catch (IllegalStateException e) {
            boolean isEmergencyNumber2 = PhoneNumberUtils.isLocalEmergencyNumber(context, number);
            isEmergencyNumber = isEmergencyNumber2;
        }
        if (isEmergencyNumber) {
            cw.event = 4;
        } else if (PhoneNumberUtils.isVoiceMailNumber(context, subId, number)) {
            cw.event = 5;
        } else {
            cw.event = 1;
        }
        c.mHandler.startQuery(token, cw, contactRef, null, null, null, null);
        return c;
    }

    public void addQueryListener(int token, OnQueryCompleteListener listener, Object cookie) {
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.event = 2;
        this.mHandler.startQuery(token, cw, null, null, null, null, null);
    }

    private void allocate(Context context, Uri contactRef) {
        if (context == null || contactRef == null) {
            throw new QueryPoolException("Bad context or query uri.");
        }
        CallerInfoAsyncQueryHandler callerInfoAsyncQueryHandler = new CallerInfoAsyncQueryHandler(context);
        this.mHandler = callerInfoAsyncQueryHandler;
        callerInfoAsyncQueryHandler.mQueryUri = contactRef;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release() {
        this.mHandler.mContext = null;
        this.mHandler.mQueryUri = null;
        this.mHandler.mCallerInfo = null;
        this.mHandler = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String sanitizeUriToString(Uri uri) {
        if (uri != null) {
            String uriString = uri.toString();
            int indexOfLastSlash = uriString.lastIndexOf(47);
            if (indexOfLastSlash > 0) {
                return uriString.substring(0, indexOfLastSlash) + "/xxxxxxx";
            }
            return uriString;
        }
        return "";
    }
}
