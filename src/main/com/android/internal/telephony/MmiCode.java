package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.ResultReceiver;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public interface MmiCode {
    public static final int MATCH_GROUP_CDMA_MMI_CODE_NUMBER = 3;
    public static final int MATCH_GROUP_CDMA_MMI_CODE_NUMBER_PREFIX = 2;
    public static final int MATCH_GROUP_CDMA_MMI_CODE_SERVICE_CODE = 1;
    public static final Pattern sPatternCdmaMmiCodeWhileRoaming = Pattern.compile("\\*(\\d{2})(\\+{0,1})(\\d{0,})");

    /* loaded from: classes.dex */
    public enum State {
        PENDING,
        CANCELLED,
        COMPLETE,
        FAILED
    }

    void cancel();

    String getDialString();

    CharSequence getMessage();

    @UnsupportedAppUsage
    Phone getPhone();

    State getState();

    ResultReceiver getUssdCallbackReceiver();

    boolean isCancelable();

    boolean isNetworkInitiatedUssd();

    boolean isPinPukCommand();

    boolean isUssdRequest();

    void processCode() throws CallStateException;
}
