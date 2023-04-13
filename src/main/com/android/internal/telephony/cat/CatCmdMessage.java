package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.telephony.cat.AppInterface;
/* loaded from: classes.dex */
public class CatCmdMessage implements Parcelable {
    public static final Parcelable.Creator<CatCmdMessage> CREATOR = new Parcelable.Creator<CatCmdMessage>() { // from class: com.android.internal.telephony.cat.CatCmdMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CatCmdMessage createFromParcel(Parcel parcel) {
            return new CatCmdMessage(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CatCmdMessage[] newArray(int i) {
            return new CatCmdMessage[i];
        }
    };
    private BrowserSettings mBrowserSettings;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CallSettings mCallSettings;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    CommandDetails mCmdDet;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Input mInput;
    private boolean mLoadIconFailed;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Menu mMenu;
    private SMSSettings mSMSSettings;
    private SetupEventListSettings mSetupEventListSettings;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private TextMessage mTextMsg;
    private ToneSettings mToneSettings;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public class BrowserSettings {
        public LaunchBrowserMode mode;
        public String url;

        public BrowserSettings() {
        }
    }

    /* loaded from: classes.dex */
    public class CallSettings {
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public TextMessage callMsg;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public TextMessage confirmMsg;

        public CallSettings() {
        }
    }

    /* loaded from: classes.dex */
    public class SMSSettings {
        public TextMessage destAddr;
        public TextMessage smsText;

        public SMSSettings() {
        }
    }

    /* loaded from: classes.dex */
    public class SetupEventListSettings {
        @UnsupportedAppUsage
        public int[] eventList;

        public SetupEventListSettings() {
        }
    }

    /* loaded from: classes.dex */
    public final class SetupEventListConstants {
        public static final int BROWSER_TERMINATION_EVENT = 8;
        public static final int BROWSING_STATUS_EVENT = 15;
        public static final int IDLE_SCREEN_AVAILABLE_EVENT = 5;
        public static final int LANGUAGE_SELECTION_EVENT = 7;
        public static final int USER_ACTIVITY_EVENT = 4;

        public SetupEventListConstants() {
        }
    }

    /* loaded from: classes.dex */
    public final class BrowserTerminationCauses {
        public static final int ERROR_TERMINATION = 1;
        public static final int USER_TERMINATION = 0;

        public BrowserTerminationCauses() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CatCmdMessage(CommandParams commandParams) {
        this.mBrowserSettings = null;
        this.mToneSettings = null;
        this.mCallSettings = null;
        this.mSMSSettings = null;
        this.mSetupEventListSettings = null;
        this.mLoadIconFailed = false;
        this.mCmdDet = commandParams.mCmdDet;
        this.mLoadIconFailed = commandParams.mLoadIconFailed;
        switch (C00812.f1xca33cf42[getCmdType().ordinal()]) {
            case 1:
            case 2:
                this.mMenu = ((SelectItemParams) commandParams).mMenu;
                return;
            case 3:
                if (commandParams instanceof SendSMSParams) {
                    SMSSettings sMSSettings = new SMSSettings();
                    this.mSMSSettings = sMSSettings;
                    SendSMSParams sendSMSParams = (SendSMSParams) commandParams;
                    sMSSettings.smsText = sendSMSParams.mTextSmsMsg;
                    sMSSettings.destAddr = sendSMSParams.mDestAddress;
                    this.mTextMsg = sendSMSParams.mDisplayText.mTextMsg;
                    return;
                }
                this.mTextMsg = ((DisplayTextParams) commandParams).mTextMsg;
                return;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                this.mTextMsg = ((DisplayTextParams) commandParams).mTextMsg;
                return;
            case 11:
            case 12:
                this.mInput = ((GetInputParams) commandParams).mInput;
                return;
            case 13:
                LaunchBrowserParams launchBrowserParams = (LaunchBrowserParams) commandParams;
                this.mTextMsg = launchBrowserParams.mConfirmMsg;
                BrowserSettings browserSettings = new BrowserSettings();
                this.mBrowserSettings = browserSettings;
                browserSettings.url = launchBrowserParams.mUrl;
                browserSettings.mode = launchBrowserParams.mMode;
                return;
            case 14:
                PlayToneParams playToneParams = (PlayToneParams) commandParams;
                this.mToneSettings = playToneParams.mSettings;
                this.mTextMsg = playToneParams.mTextMsg;
                return;
            case 15:
                this.mTextMsg = ((CallSetupParams) commandParams).mConfirmMsg;
                return;
            case 16:
                CallSettings callSettings = new CallSettings();
                this.mCallSettings = callSettings;
                CallSetupParams callSetupParams = (CallSetupParams) commandParams;
                callSettings.confirmMsg = callSetupParams.mConfirmMsg;
                callSettings.callMsg = callSetupParams.mCallMsg;
                return;
            case 17:
            case 18:
            case 19:
            case 20:
                this.mTextMsg = ((BIPClientParams) commandParams).mTextMsg;
                return;
            case 21:
                SetupEventListSettings setupEventListSettings = new SetupEventListSettings();
                this.mSetupEventListSettings = setupEventListSettings;
                setupEventListSettings.eventList = ((SetEventListParams) commandParams).mEventInfo;
                return;
            default:
                return;
        }
    }

    /* renamed from: com.android.internal.telephony.cat.CatCmdMessage$2 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00812 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$cat$AppInterface$CommandType */
        static final /* synthetic */ int[] f1xca33cf42;

        static {
            int[] iArr = new int[AppInterface.CommandType.values().length];
            f1xca33cf42 = iArr;
            try {
                iArr[AppInterface.CommandType.SET_UP_MENU.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SELECT_ITEM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SEND_SMS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.DISPLAY_TEXT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SET_UP_IDLE_MODE_TEXT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SEND_DTMF.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.REFRESH.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.RUN_AT.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SEND_SS.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SEND_USSD.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.GET_INPUT.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.GET_INKEY.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.LAUNCH_BROWSER.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.PLAY_TONE.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.GET_CHANNEL_STATUS.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SET_UP_CALL.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.OPEN_CHANNEL.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.CLOSE_CHANNEL.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.RECEIVE_DATA.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SEND_DATA.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.SET_UP_EVENT_LIST.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                f1xca33cf42[AppInterface.CommandType.PROVIDE_LOCAL_INFORMATION.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
        }
    }

    public CatCmdMessage(Parcel parcel) {
        this.mBrowserSettings = null;
        this.mToneSettings = null;
        this.mCallSettings = null;
        this.mSMSSettings = null;
        this.mSetupEventListSettings = null;
        this.mLoadIconFailed = false;
        this.mCmdDet = (CommandDetails) parcel.readParcelable(CommandDetails.class.getClassLoader());
        this.mTextMsg = (TextMessage) parcel.readParcelable(TextMessage.class.getClassLoader());
        this.mMenu = (Menu) parcel.readParcelable(Menu.class.getClassLoader());
        this.mInput = (Input) parcel.readParcelable(Input.class.getClassLoader());
        this.mLoadIconFailed = parcel.readByte() == 1;
        int i = C00812.f1xca33cf42[getCmdType().ordinal()];
        if (i == 3) {
            SMSSettings sMSSettings = new SMSSettings();
            this.mSMSSettings = sMSSettings;
            sMSSettings.smsText = (TextMessage) parcel.readParcelable(SendSMSParams.class.getClassLoader());
            this.mSMSSettings.destAddr = (TextMessage) parcel.readParcelable(SendSMSParams.class.getClassLoader());
        } else if (i == 16) {
            CallSettings callSettings = new CallSettings();
            this.mCallSettings = callSettings;
            callSettings.confirmMsg = (TextMessage) parcel.readParcelable(TextMessage.class.getClassLoader());
            this.mCallSettings.callMsg = (TextMessage) parcel.readParcelable(TextMessage.class.getClassLoader());
        } else if (i == 21) {
            this.mSetupEventListSettings = new SetupEventListSettings();
            int readInt = parcel.readInt();
            this.mSetupEventListSettings.eventList = new int[readInt];
            for (int i2 = 0; i2 < readInt; i2++) {
                this.mSetupEventListSettings.eventList[i2] = parcel.readInt();
            }
        } else if (i == 13) {
            BrowserSettings browserSettings = new BrowserSettings();
            this.mBrowserSettings = browserSettings;
            browserSettings.url = parcel.readString();
            this.mBrowserSettings.mode = LaunchBrowserMode.values()[parcel.readInt()];
        } else {
            if (i != 14) {
                return;
            }
            this.mToneSettings = (ToneSettings) parcel.readParcelable(ToneSettings.class.getClassLoader());
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mCmdDet, 0);
        parcel.writeParcelable(this.mTextMsg, 0);
        parcel.writeParcelable(this.mMenu, 0);
        parcel.writeParcelable(this.mInput, 0);
        parcel.writeByte(this.mLoadIconFailed ? (byte) 1 : (byte) 0);
        int i2 = C00812.f1xca33cf42[getCmdType().ordinal()];
        if (i2 == 3) {
            SMSSettings sMSSettings = this.mSMSSettings;
            if (sMSSettings != null) {
                parcel.writeParcelable(sMSSettings.smsText, 0);
                parcel.writeParcelable(this.mSMSSettings.destAddr, 0);
            }
        } else if (i2 == 16) {
            parcel.writeParcelable(this.mCallSettings.confirmMsg, 0);
            parcel.writeParcelable(this.mCallSettings.callMsg, 0);
        } else if (i2 == 21) {
            parcel.writeIntArray(this.mSetupEventListSettings.eventList);
        } else if (i2 == 13) {
            parcel.writeString(this.mBrowserSettings.url);
            parcel.writeInt(this.mBrowserSettings.mode.ordinal());
        } else if (i2 != 14) {
        } else {
            parcel.writeParcelable(this.mToneSettings, 0);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AppInterface.CommandType getCmdType() {
        return AppInterface.CommandType.fromInt(this.mCmdDet.typeOfCommand);
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public Input geInput() {
        return this.mInput;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public TextMessage geTextMessage() {
        return this.mTextMsg;
    }

    public BrowserSettings getBrowserSettings() {
        return this.mBrowserSettings;
    }

    public ToneSettings getToneSettings() {
        return this.mToneSettings;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public CallSettings getCallSettings() {
        return this.mCallSettings;
    }

    @UnsupportedAppUsage
    public SetupEventListSettings getSetEventList() {
        return this.mSetupEventListSettings;
    }

    @UnsupportedAppUsage
    public boolean hasIconLoadFailed() {
        return this.mLoadIconFailed;
    }
}
