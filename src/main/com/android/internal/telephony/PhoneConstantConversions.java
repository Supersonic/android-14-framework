package com.android.internal.telephony;

import com.android.internal.telephony.PhoneConstants;
/* loaded from: classes3.dex */
public class PhoneConstantConversions {
    public static int convertCallState(PhoneConstants.State state) {
        switch (C43691.$SwitchMap$com$android$internal$telephony$PhoneConstants$State[state.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    public static PhoneConstants.State convertCallState(int state) {
        switch (state) {
            case 1:
                return PhoneConstants.State.RINGING;
            case 2:
                return PhoneConstants.State.OFFHOOK;
            default:
                return PhoneConstants.State.IDLE;
        }
    }

    /* renamed from: com.android.internal.telephony.PhoneConstantConversions$1 */
    /* loaded from: classes3.dex */
    static /* synthetic */ class C43691 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$PhoneConstants$DataState */
        static final /* synthetic */ int[] f913x67a69abf;
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$PhoneConstants$State;

        static {
            int[] iArr = new int[PhoneConstants.DataState.values().length];
            f913x67a69abf = iArr;
            try {
                iArr[PhoneConstants.DataState.CONNECTING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f913x67a69abf[PhoneConstants.DataState.CONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f913x67a69abf[PhoneConstants.DataState.SUSPENDED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f913x67a69abf[PhoneConstants.DataState.DISCONNECTING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[PhoneConstants.State.values().length];
            $SwitchMap$com$android$internal$telephony$PhoneConstants$State = iArr2;
            try {
                iArr2[PhoneConstants.State.RINGING.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$PhoneConstants$State[PhoneConstants.State.OFFHOOK.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public static int convertDataState(PhoneConstants.DataState state) {
        switch (C43691.f913x67a69abf[state.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }

    public static PhoneConstants.DataState convertDataState(int state) {
        switch (state) {
            case 1:
                return PhoneConstants.DataState.CONNECTING;
            case 2:
                return PhoneConstants.DataState.CONNECTED;
            case 3:
                return PhoneConstants.DataState.SUSPENDED;
            case 4:
                return PhoneConstants.DataState.DISCONNECTING;
            default:
                return PhoneConstants.DataState.DISCONNECTED;
        }
    }
}
