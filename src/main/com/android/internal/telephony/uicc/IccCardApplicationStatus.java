package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class IccCardApplicationStatus {
    public String aid;
    public String app_label;
    public AppState app_state;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AppType app_type;
    public PersoSubState perso_substate;
    public IccCardStatus.PinState pin1;
    public boolean pin1_replaced;
    public IccCardStatus.PinState pin2;

    @UnsupportedAppUsage(implicitMember = "values()[Lcom/android/internal/telephony/uicc/IccCardApplicationStatus$AppState;")
    /* loaded from: classes.dex */
    public enum AppState {
        APPSTATE_UNKNOWN,
        APPSTATE_DETECTED,
        APPSTATE_PIN,
        APPSTATE_PUK,
        APPSTATE_SUBSCRIPTION_PERSO,
        APPSTATE_READY
    }

    @UnsupportedAppUsage(implicitMember = "values()[Lcom/android/internal/telephony/uicc/IccCardApplicationStatus$AppType;")
    /* loaded from: classes.dex */
    public enum AppType {
        APPTYPE_UNKNOWN,
        APPTYPE_SIM,
        APPTYPE_USIM,
        APPTYPE_RUIM,
        APPTYPE_CSIM,
        APPTYPE_ISIM
    }

    /* renamed from: com.android.internal.telephony.uicc.IccCardApplicationStatus$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C03261 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$uicc$IccCardApplicationStatus$PersoSubState */
        static final /* synthetic */ int[] f22x1c108904;

        static {
            int[] iArr = new int[PersoSubState.values().length];
            f22x1c108904 = iArr;
            try {
                iArr[PersoSubState.PERSOSUBSTATE_UNKNOWN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f22x1c108904[PersoSubState.PERSOSUBSTATE_IN_PROGRESS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f22x1c108904[PersoSubState.PERSOSUBSTATE_READY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @UnsupportedAppUsage(implicitMember = "values()[Lcom/android/internal/telephony/uicc/IccCardApplicationStatus$PersoSubState;")
    /* loaded from: classes.dex */
    public enum PersoSubState {
        PERSOSUBSTATE_UNKNOWN,
        PERSOSUBSTATE_IN_PROGRESS,
        PERSOSUBSTATE_READY,
        PERSOSUBSTATE_SIM_NETWORK,
        PERSOSUBSTATE_SIM_NETWORK_SUBSET,
        PERSOSUBSTATE_SIM_CORPORATE,
        PERSOSUBSTATE_SIM_SERVICE_PROVIDER,
        PERSOSUBSTATE_SIM_SIM,
        PERSOSUBSTATE_SIM_NETWORK_PUK,
        PERSOSUBSTATE_SIM_NETWORK_SUBSET_PUK,
        PERSOSUBSTATE_SIM_CORPORATE_PUK,
        PERSOSUBSTATE_SIM_SERVICE_PROVIDER_PUK,
        PERSOSUBSTATE_SIM_SIM_PUK,
        PERSOSUBSTATE_RUIM_NETWORK1,
        PERSOSUBSTATE_RUIM_NETWORK2,
        PERSOSUBSTATE_RUIM_HRPD,
        PERSOSUBSTATE_RUIM_CORPORATE,
        PERSOSUBSTATE_RUIM_SERVICE_PROVIDER,
        PERSOSUBSTATE_RUIM_RUIM,
        PERSOSUBSTATE_RUIM_NETWORK1_PUK,
        PERSOSUBSTATE_RUIM_NETWORK2_PUK,
        PERSOSUBSTATE_RUIM_HRPD_PUK,
        PERSOSUBSTATE_RUIM_CORPORATE_PUK,
        PERSOSUBSTATE_RUIM_SERVICE_PROVIDER_PUK,
        PERSOSUBSTATE_RUIM_RUIM_PUK,
        PERSOSUBSTATE_SIM_SPN,
        PERSOSUBSTATE_SIM_SPN_PUK,
        PERSOSUBSTATE_SIM_SP_EHPLMN,
        PERSOSUBSTATE_SIM_SP_EHPLMN_PUK,
        PERSOSUBSTATE_SIM_ICCID,
        PERSOSUBSTATE_SIM_ICCID_PUK,
        PERSOSUBSTATE_SIM_IMPI,
        PERSOSUBSTATE_SIM_IMPI_PUK,
        PERSOSUBSTATE_SIM_NS_SP,
        PERSOSUBSTATE_SIM_NS_SP_PUK;

        public static boolean isPersoLocked(PersoSubState persoSubState) {
            int i = C03261.f22x1c108904[persoSubState.ordinal()];
            return (i == 1 || i == 2 || i == 3) ? false : true;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AppType AppTypeFromRILInt(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return AppType.APPTYPE_ISIM;
                            }
                            AppType appType = AppType.APPTYPE_UNKNOWN;
                            loge("AppTypeFromRILInt: bad RIL_AppType: " + i + " use APPTYPE_UNKNOWN");
                            return appType;
                        }
                        return AppType.APPTYPE_CSIM;
                    }
                    return AppType.APPTYPE_RUIM;
                }
                return AppType.APPTYPE_USIM;
            }
            return AppType.APPTYPE_SIM;
        }
        return AppType.APPTYPE_UNKNOWN;
    }

    public AppState AppStateFromRILInt(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return AppState.APPSTATE_READY;
                            }
                            AppState appState = AppState.APPSTATE_UNKNOWN;
                            loge("AppStateFromRILInt: bad state: " + i + " use APPSTATE_UNKNOWN");
                            return appState;
                        }
                        return AppState.APPSTATE_SUBSCRIPTION_PERSO;
                    }
                    return AppState.APPSTATE_PUK;
                }
                return AppState.APPSTATE_PIN;
            }
            return AppState.APPSTATE_DETECTED;
        }
        return AppState.APPSTATE_UNKNOWN;
    }

    public PersoSubState PersoSubstateFromRILInt(int i) {
        switch (i) {
            case 0:
                return PersoSubState.PERSOSUBSTATE_UNKNOWN;
            case 1:
                return PersoSubState.PERSOSUBSTATE_IN_PROGRESS;
            case 2:
                return PersoSubState.PERSOSUBSTATE_READY;
            case 3:
                return PersoSubState.PERSOSUBSTATE_SIM_NETWORK;
            case 4:
                return PersoSubState.PERSOSUBSTATE_SIM_NETWORK_SUBSET;
            case 5:
                return PersoSubState.PERSOSUBSTATE_SIM_CORPORATE;
            case 6:
                return PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER;
            case 7:
                return PersoSubState.PERSOSUBSTATE_SIM_SIM;
            case 8:
                return PersoSubState.PERSOSUBSTATE_SIM_NETWORK_PUK;
            case 9:
                return PersoSubState.PERSOSUBSTATE_SIM_NETWORK_SUBSET_PUK;
            case 10:
                return PersoSubState.PERSOSUBSTATE_SIM_CORPORATE_PUK;
            case 11:
                return PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER_PUK;
            case 12:
                return PersoSubState.PERSOSUBSTATE_SIM_SIM_PUK;
            case 13:
                return PersoSubState.PERSOSUBSTATE_RUIM_NETWORK1;
            case 14:
                return PersoSubState.PERSOSUBSTATE_RUIM_NETWORK2;
            case 15:
                return PersoSubState.PERSOSUBSTATE_RUIM_HRPD;
            case 16:
                return PersoSubState.PERSOSUBSTATE_RUIM_CORPORATE;
            case 17:
                return PersoSubState.PERSOSUBSTATE_RUIM_SERVICE_PROVIDER;
            case 18:
                return PersoSubState.PERSOSUBSTATE_RUIM_RUIM;
            case 19:
                return PersoSubState.PERSOSUBSTATE_RUIM_NETWORK1_PUK;
            case 20:
                return PersoSubState.PERSOSUBSTATE_RUIM_NETWORK2_PUK;
            case 21:
                return PersoSubState.PERSOSUBSTATE_RUIM_HRPD_PUK;
            case 22:
                return PersoSubState.PERSOSUBSTATE_RUIM_CORPORATE_PUK;
            case 23:
                return PersoSubState.PERSOSUBSTATE_RUIM_SERVICE_PROVIDER_PUK;
            case 24:
                return PersoSubState.PERSOSUBSTATE_RUIM_RUIM_PUK;
            case 25:
                return PersoSubState.PERSOSUBSTATE_SIM_SPN;
            case 26:
                return PersoSubState.PERSOSUBSTATE_SIM_SPN_PUK;
            case 27:
                return PersoSubState.PERSOSUBSTATE_SIM_SP_EHPLMN;
            case 28:
                return PersoSubState.PERSOSUBSTATE_SIM_SP_EHPLMN_PUK;
            case 29:
                return PersoSubState.PERSOSUBSTATE_SIM_ICCID;
            case 30:
                return PersoSubState.PERSOSUBSTATE_SIM_ICCID_PUK;
            case 31:
                return PersoSubState.PERSOSUBSTATE_SIM_IMPI;
            case 32:
                return PersoSubState.PERSOSUBSTATE_SIM_IMPI_PUK;
            case 33:
                return PersoSubState.PERSOSUBSTATE_SIM_NS_SP;
            case 34:
                return PersoSubState.PERSOSUBSTATE_SIM_NS_SP_PUK;
            default:
                PersoSubState persoSubState = PersoSubState.PERSOSUBSTATE_UNKNOWN;
                loge("PersoSubstateFromRILInt: bad substate: " + i + " use PERSOSUBSTATE_UNKNOWN");
                return persoSubState;
        }
    }

    public IccCardStatus.PinState PinStateFromRILInt(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return IccCardStatus.PinState.PINSTATE_ENABLED_PERM_BLOCKED;
                            }
                            IccCardStatus.PinState pinState = IccCardStatus.PinState.PINSTATE_UNKNOWN;
                            loge("PinStateFromRILInt: bad pin state: " + i + " use PINSTATE_UNKNOWN");
                            return pinState;
                        }
                        return IccCardStatus.PinState.PINSTATE_ENABLED_BLOCKED;
                    }
                    return IccCardStatus.PinState.PINSTATE_DISABLED;
                }
                return IccCardStatus.PinState.PINSTATE_ENABLED_VERIFIED;
            }
            return IccCardStatus.PinState.PINSTATE_ENABLED_NOT_VERIFIED;
        }
        return IccCardStatus.PinState.PINSTATE_UNKNOWN;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(this.app_type);
        sb.append(",");
        sb.append(this.app_state);
        if (this.app_state == AppState.APPSTATE_SUBSCRIPTION_PERSO) {
            sb.append(",");
            sb.append(this.perso_substate);
        }
        AppType appType = this.app_type;
        if (appType == AppType.APPTYPE_CSIM || appType == AppType.APPTYPE_USIM || appType == AppType.APPTYPE_ISIM) {
            sb.append(",pin1=");
            sb.append(this.pin1);
            sb.append(",pin2=");
            sb.append(this.pin2);
        }
        sb.append("}");
        return sb.toString();
    }

    private void loge(String str) {
        Rlog.e("IccCardApplicationStatus", str);
    }
}
