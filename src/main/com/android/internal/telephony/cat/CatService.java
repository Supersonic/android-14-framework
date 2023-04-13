package com.android.internal.telephony.cat;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ProxyController;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.cat.AppInterface;
import com.android.internal.telephony.cat.Duration;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccRefreshResponse;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccProfile;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class CatService extends Handler implements AppInterface {
    protected static final int MSG_ID_ALPHA_NOTIFY = 9;
    protected static final int MSG_ID_CALL_SETUP = 4;
    protected static final int MSG_ID_EVENT_NOTIFY = 3;
    protected static final int MSG_ID_ICC_CHANGED = 8;
    protected static final int MSG_ID_PROACTIVE_COMMAND = 2;
    protected static final int MSG_ID_SESSION_END = 1;
    private static IccRecords mIccRecords;
    private static UiccCardApplication mUiccApplication;
    private static HandlerThread sCatServiceThread;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static CatService[] sInstance;
    @UnsupportedAppUsage
    private static final Object sInstanceLock = new Object();
    private IccCardStatus.CardState mCardState;
    @UnsupportedAppUsage
    private CommandsInterface mCmdIf;
    @UnsupportedAppUsage
    private Context mContext;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CatCmdMessage mCurrntCmd;
    @UnsupportedAppUsage
    private CatCmdMessage mMenuCmd;
    @UnsupportedAppUsage
    private RilMessageDecoder mMsgDecoder;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mSlotId;
    @VisibleForTesting
    public final BroadcastReceiver mSmsBroadcastReceiver;
    @UnsupportedAppUsage
    private boolean mStkAppInstalled;
    @UnsupportedAppUsage
    private UiccController mUiccController;

    private CatService(CommandsInterface commandsInterface, UiccCardApplication uiccCardApplication, IccRecords iccRecords, Context context, IccFileHandler iccFileHandler, UiccProfile uiccProfile, int i, Looper looper) {
        super(looper);
        this.mCurrntCmd = null;
        this.mMenuCmd = null;
        this.mMsgDecoder = null;
        this.mStkAppInstalled = false;
        this.mCardState = IccCardStatus.CardState.CARDSTATE_ABSENT;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.cat.CatService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                ResultCode resultCode;
                int intValue;
                CommandDetails commandDetails = (CommandDetails) intent.getExtra("cmdDetails");
                if (intent.getAction().equals("com.android.internal.telephony.cat.SMS_SENT_ACTION")) {
                    int resultCode2 = getResultCode();
                    ResultCode resultCode3 = ResultCode.NETWORK_CRNTLY_UNABLE_TO_PROCESS;
                    CatLog.m5d(this, "STK SMS errorCode : " + resultCode2);
                    if (resultCode2 != -1) {
                        if (intent.hasExtra("ims") && intent.getBooleanExtra("ims", false)) {
                            ResultCode resultCode4 = ResultCode.SMS_RP_ERROR;
                            if (intent.hasExtra("errorCode")) {
                                int intValue2 = ((Integer) intent.getExtra("errorCode")).intValue();
                                if ((intValue2 & 128) == 0) {
                                    resultCode = resultCode4;
                                    intValue = intValue2;
                                    CatLog.m5d(this, "Error delivering STK SMS errorCode : " + intValue + " terminalResponseResultCode = " + resultCode);
                                    CatService.this.sendTerminalResponse(commandDetails, resultCode, true, intValue, null);
                                }
                            }
                            resultCode = resultCode4;
                            intValue = 0;
                            CatLog.m5d(this, "Error delivering STK SMS errorCode : " + intValue + " terminalResponseResultCode = " + resultCode);
                            CatService.this.sendTerminalResponse(commandDetails, resultCode, true, intValue, null);
                        } else if (intent.hasExtra("errorCode")) {
                            intValue = ((Integer) intent.getExtra("errorCode")).intValue() | 128;
                            resultCode = resultCode3;
                            CatLog.m5d(this, "Error delivering STK SMS errorCode : " + intValue + " terminalResponseResultCode = " + resultCode);
                            CatService.this.sendTerminalResponse(commandDetails, resultCode, true, intValue, null);
                        } else {
                            resultCode = resultCode3;
                            intValue = 0;
                            CatLog.m5d(this, "Error delivering STK SMS errorCode : " + intValue + " terminalResponseResultCode = " + resultCode);
                            CatService.this.sendTerminalResponse(commandDetails, resultCode, true, intValue, null);
                        }
                    } else {
                        CatLog.m5d(this, " STK SMS sent successfully ");
                    }
                }
                if (intent.getAction().equals("com.android.internal.telephony.cat.SMS_DELIVERY_ACTION")) {
                    int resultCode5 = getResultCode();
                    if (resultCode5 == -1) {
                        CatService.this.sendTerminalResponse(commandDetails, ResultCode.OK, false, 0, null);
                        CatLog.m5d(this, " STK SMS delivered successfully ");
                        return;
                    }
                    CatLog.m5d(this, "Error delivering STK SMS : " + resultCode5);
                    CatService.this.sendTerminalResponse(commandDetails, ResultCode.TERMINAL_CRNTLY_UNABLE_TO_PROCESS, false, 0, null);
                }
            }
        };
        this.mSmsBroadcastReceiver = broadcastReceiver;
        if (commandsInterface == null || uiccCardApplication == null || iccRecords == null || context == null || iccFileHandler == null || uiccProfile == null) {
            throw new NullPointerException("Service: Input parameters must not be null");
        }
        this.mCmdIf = commandsInterface;
        this.mContext = context;
        this.mSlotId = i;
        RilMessageDecoder rilMessageDecoder = RilMessageDecoder.getInstance(this, iccFileHandler, context, i);
        this.mMsgDecoder = rilMessageDecoder;
        if (rilMessageDecoder == null) {
            CatLog.m5d(this, "Null RilMessageDecoder instance");
            return;
        }
        rilMessageDecoder.start();
        this.mCmdIf.setOnCatSessionEnd(this, 1, null);
        this.mCmdIf.setOnCatProactiveCmd(this, 2, null);
        this.mCmdIf.setOnCatEvent(this, 3, null);
        this.mCmdIf.setOnCatCallSetUp(this, 4, null);
        this.mCmdIf.registerForIccRefresh(this, 30, null);
        this.mCmdIf.setOnCatCcAlphaNotify(this, 9, null);
        mIccRecords = iccRecords;
        mUiccApplication = uiccCardApplication;
        iccRecords.registerForRecordsLoaded(this, 20, null);
        CatLog.m5d(this, "registerForRecordsLoaded slotid=" + this.mSlotId + " instance:" + this);
        UiccController uiccController = UiccController.getInstance();
        this.mUiccController = uiccController;
        uiccController.registerForIccChanged(this, 8, null);
        this.mStkAppInstalled = isStkAppInstalled();
        CatLog.m5d(this, "Running CAT service on Slotid: " + this.mSlotId + ". STK app installed:" + this.mStkAppInstalled);
        this.mContext.registerReceiver(broadcastReceiver, new IntentFilter("com.android.internal.telephony.cat.SMS_DELIVERY_ACTION"));
        this.mContext.registerReceiver(broadcastReceiver, new IntentFilter("com.android.internal.telephony.cat.SMS_SENT_ACTION"));
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x0031 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static CatService getInstance(CommandsInterface commandsInterface, Context context, UiccProfile uiccProfile, int i) {
        UiccCardApplication uiccCardApplication;
        IccRecords iccRecords;
        IccFileHandler iccFileHandler;
        IccRecords iccRecords2;
        if (sCatServiceThread == null) {
            HandlerThread handlerThread = new HandlerThread("CatServiceThread");
            sCatServiceThread = handlerThread;
            handlerThread.start();
        }
        if (uiccProfile != null) {
            uiccCardApplication = uiccProfile.getApplicationIndex(0);
            if (uiccCardApplication != null) {
                iccFileHandler = uiccCardApplication.getIccFileHandler();
                iccRecords = uiccCardApplication.getIccRecords();
                synchronized (sInstanceLock) {
                    if (sInstance == null) {
                        int supportedModemCount = TelephonyManager.getDefault().getSupportedModemCount();
                        sInstance = new CatService[supportedModemCount];
                        for (int i2 = 0; i2 < supportedModemCount; i2++) {
                            sInstance[i2] = null;
                        }
                    }
                    CatService[] catServiceArr = sInstance;
                    CatService catService = catServiceArr[i];
                    if (catService == null) {
                        if (commandsInterface != null && uiccCardApplication != null && iccRecords != null && context != null && iccFileHandler != null && uiccProfile != null) {
                            catServiceArr[i] = new CatService(commandsInterface, uiccCardApplication, iccRecords, context, iccFileHandler, uiccProfile, i, sCatServiceThread.getLooper());
                        }
                        return null;
                    } else if (iccRecords != null && (iccRecords2 = mIccRecords) != iccRecords) {
                        if (iccRecords2 != null) {
                            iccRecords2.unregisterForRecordsLoaded(catService);
                        }
                        mIccRecords = iccRecords;
                        mUiccApplication = uiccCardApplication;
                        iccRecords.registerForRecordsLoaded(sInstance[i], 20, null);
                        CatLog.m5d(sInstance[i], "registerForRecordsLoaded slotid=" + i + " instance:" + sInstance[i]);
                    }
                    return sInstance[i];
                }
            }
            iccRecords = null;
        } else {
            uiccCardApplication = null;
            iccRecords = null;
        }
        iccFileHandler = iccRecords;
        synchronized (sInstanceLock) {
        }
    }

    @Override // com.android.internal.telephony.cat.AppInterface
    @UnsupportedAppUsage
    public void dispose() {
        synchronized (sInstanceLock) {
            CatLog.m5d(this, "Disposing CatService object");
            mIccRecords.unregisterForRecordsLoaded(this);
            broadcastCardStateAndIccRefreshResp(IccCardStatus.CardState.CARDSTATE_ABSENT, null);
            this.mCmdIf.unSetOnCatSessionEnd(this);
            this.mCmdIf.unSetOnCatProactiveCmd(this);
            this.mCmdIf.unSetOnCatEvent(this);
            this.mCmdIf.unSetOnCatCallSetUp(this);
            this.mCmdIf.unSetOnCatCcAlphaNotify(this);
            this.mCmdIf.unregisterForIccRefresh(this);
            UiccController uiccController = this.mUiccController;
            if (uiccController != null) {
                uiccController.unregisterForIccChanged(this);
                this.mUiccController = null;
            }
            RilMessageDecoder rilMessageDecoder = this.mMsgDecoder;
            if (rilMessageDecoder != null) {
                rilMessageDecoder.dispose();
                this.mMsgDecoder = null;
            }
            removeCallbacksAndMessages(null);
            CatService[] catServiceArr = sInstance;
            if (catServiceArr != null) {
                int i = this.mSlotId;
                if (i >= 0 && i < catServiceArr.length) {
                    catServiceArr[i] = null;
                } else {
                    CatLog.m5d(this, "error: invaild slot id: " + this.mSlotId);
                }
            }
        }
    }

    protected void finalize() {
        CatLog.m5d(this, "Service finalized");
    }

    private void handleRilMsg(RilMessage rilMessage) {
        CommandParams commandParams;
        CommandParams commandParams2;
        if (rilMessage == null) {
            return;
        }
        int i = rilMessage.mId;
        if (i == 1) {
            handleSessionEnd();
        } else if (i != 2) {
            if (i != 3) {
                if (i == 5 && (commandParams2 = (CommandParams) rilMessage.mData) != null) {
                    handleCommand(commandParams2, false);
                }
            } else if (rilMessage.mResCode != ResultCode.OK || (commandParams = (CommandParams) rilMessage.mData) == null) {
            } else {
                handleCommand(commandParams, false);
            }
        } else {
            try {
                CommandParams commandParams3 = (CommandParams) rilMessage.mData;
                if (commandParams3 != null) {
                    ResultCode resultCode = rilMessage.mResCode;
                    if (resultCode == ResultCode.OK) {
                        handleCommand(commandParams3, true);
                    } else {
                        sendTerminalResponse(commandParams3.mCmdDet, resultCode, false, 0, null);
                    }
                }
            } catch (ClassCastException unused) {
                CatLog.m5d(this, "Fail to parse proactive command");
                CatCmdMessage catCmdMessage = this.mCurrntCmd;
                if (catCmdMessage != null) {
                    sendTerminalResponse(catCmdMessage.mCmdDet, ResultCode.CMD_DATA_NOT_UNDERSTOOD, false, 0, null);
                }
            }
        }
    }

    private boolean isSupportedSetupEventCommand(CatCmdMessage catCmdMessage) {
        int[] iArr;
        boolean z = true;
        for (int i : catCmdMessage.getSetEventList().eventList) {
            CatLog.m5d(this, "Event: " + i);
            if (i != 4 && i != 5 && i != 7) {
                z = false;
            }
        }
        return z;
    }

    private void handleCommand(CommandParams commandParams, boolean z) {
        ResultCode resultCode;
        boolean z2;
        CatLog.m5d(this, commandParams.getCommandType().name());
        if (z) {
            UiccController.addLocalLog("CatService[" + this.mSlotId + "]: ProactiveCommand  cmdParams=" + commandParams);
        }
        CatCmdMessage catCmdMessage = new CatCmdMessage(commandParams);
        switch (C00832.f2xca33cf42[commandParams.getCommandType().ordinal()]) {
            case 1:
                if (removeMenu(catCmdMessage.getMenu())) {
                    this.mMenuCmd = null;
                } else {
                    this.mMenuCmd = catCmdMessage;
                }
                sendTerminalResponse(commandParams.mCmdDet, commandParams.mLoadIconFailed ? ResultCode.PRFRMD_ICON_NOT_DISPLAYED : ResultCode.OK, false, 0, null);
                break;
            case 2:
            case 7:
            case 8:
            case 9:
            case 16:
                break;
            case 3:
                sendTerminalResponse(commandParams.mCmdDet, commandParams.mLoadIconFailed ? ResultCode.PRFRMD_ICON_NOT_DISPLAYED : ResultCode.OK, false, 0, null);
                break;
            case 4:
                if (isSupportedSetupEventCommand(catCmdMessage)) {
                    sendTerminalResponse(commandParams.mCmdDet, ResultCode.OK, false, 0, null);
                    break;
                } else {
                    sendTerminalResponse(commandParams.mCmdDet, ResultCode.BEYOND_TERMINAL_CAPABILITY, false, 0, null);
                    break;
                }
            case 5:
                CommandDetails commandDetails = commandParams.mCmdDet;
                int i = commandDetails.commandQualifier;
                if (i == 3) {
                    sendTerminalResponse(commandParams.mCmdDet, ResultCode.OK, false, 0, new DTTZResponseData(null));
                    return;
                } else if (i == 4) {
                    sendTerminalResponse(commandParams.mCmdDet, ResultCode.OK, false, 0, new LanguageResponseData(Locale.getDefault().getLanguage()));
                    return;
                } else {
                    sendTerminalResponse(commandDetails, ResultCode.OK, false, 0, null);
                    return;
                }
            case 6:
                LaunchBrowserParams launchBrowserParams = (LaunchBrowserParams) commandParams;
                String str = launchBrowserParams.mConfirmMsg.text;
                if (str != null && str.equals("Default Message")) {
                    launchBrowserParams.mConfirmMsg.text = this.mContext.getText(17040573).toString();
                    break;
                }
                break;
            case 10:
            case 11:
                DisplayTextParams displayTextParams = (DisplayTextParams) commandParams;
                if ("Default Message".equals(displayTextParams.mTextMsg.text)) {
                    displayTextParams.mTextMsg.text = null;
                    break;
                }
                break;
            case 12:
                if (commandParams instanceof SendSMSParams) {
                    SendSMSParams sendSMSParams = (SendSMSParams) commandParams;
                    TextMessage textMessage = sendSMSParams.mTextSmsMsg;
                    String str2 = textMessage != null ? textMessage.text : null;
                    TextMessage textMessage2 = sendSMSParams.mDestAddress;
                    String str3 = textMessage2 != null ? textMessage2.text : null;
                    if (str2 != null && str3 != null) {
                        ProxyController proxyController = ProxyController.getInstance(this.mContext);
                        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getActiveSubscriptionInfoForSimSlotIndex(this.mSlotId);
                        if (activeSubscriptionInfoForSimSlotIndex != null) {
                            sendStkSms(str2, str3, activeSubscriptionInfoForSimSlotIndex.getSubscriptionId(), commandParams, proxyController);
                            break;
                        } else {
                            sendTerminalResponse(commandParams.mCmdDet, ResultCode.CMD_DATA_NOT_UNDERSTOOD, false, 0, null);
                            CatLog.m5d(this, "Subscription info is null");
                            break;
                        }
                    } else {
                        sendTerminalResponse(commandParams.mCmdDet, ResultCode.CMD_DATA_NOT_UNDERSTOOD, false, 0, null);
                        CatLog.m5d(this, "Sms text or Destination Address is null");
                        break;
                    }
                } else {
                    DisplayTextParams displayTextParams2 = (DisplayTextParams) commandParams;
                    String str4 = displayTextParams2.mTextMsg.text;
                    if (str4 != null && str4.equals("Default Message")) {
                        displayTextParams2.mTextMsg.text = this.mContext.getText(17041492).toString();
                        break;
                    }
                }
                break;
            case 13:
            case 14:
            case 15:
                DisplayTextParams displayTextParams3 = (DisplayTextParams) commandParams;
                String str5 = displayTextParams3.mTextMsg.text;
                if (str5 != null && str5.equals("Default Message")) {
                    displayTextParams3.mTextMsg.text = this.mContext.getText(17041492).toString();
                    break;
                }
                break;
            case 17:
                CallSetupParams callSetupParams = (CallSetupParams) commandParams;
                String str6 = callSetupParams.mConfirmMsg.text;
                if (str6 != null && str6.equals("Default Message")) {
                    callSetupParams.mConfirmMsg.text = this.mContext.getText(17039567).toString();
                    break;
                }
                break;
            case 18:
                String str7 = ((LanguageParams) commandParams).mLanguage;
                ResultCode resultCode2 = ResultCode.OK;
                if (str7 != null && str7.length() > 0) {
                    try {
                        changeLanguage(str7);
                    } catch (RemoteException unused) {
                        resultCode = ResultCode.TERMINAL_CRNTLY_UNABLE_TO_PROCESS;
                    }
                }
                resultCode = resultCode2;
                sendTerminalResponse(commandParams.mCmdDet, resultCode, false, 0, null);
                return;
            case 19:
            case 20:
            case 21:
            case 22:
                BIPClientParams bIPClientParams = (BIPClientParams) commandParams;
                try {
                    z2 = this.mContext.getResources().getBoolean(17891811);
                } catch (Resources.NotFoundException unused2) {
                    z2 = false;
                }
                if (bIPClientParams.mTextMsg.text == null && (bIPClientParams.mHasAlphaId || z2)) {
                    CatLog.m5d(this, "cmd " + commandParams.getCommandType() + " with null alpha id");
                    if (z) {
                        sendTerminalResponse(commandParams.mCmdDet, ResultCode.OK, false, 0, null);
                        return;
                    } else if (commandParams.getCommandType() == AppInterface.CommandType.OPEN_CHANNEL) {
                        this.mCmdIf.handleCallSetupRequestFromSim(true, null);
                        return;
                    } else {
                        return;
                    }
                }
                if (!this.mStkAppInstalled) {
                    CatLog.m5d(this, "No STK application found.");
                    if (z) {
                        sendTerminalResponse(commandParams.mCmdDet, ResultCode.BEYOND_TERMINAL_CAPABILITY, false, 0, null);
                        return;
                    }
                }
                if (z && (commandParams.getCommandType() == AppInterface.CommandType.CLOSE_CHANNEL || commandParams.getCommandType() == AppInterface.CommandType.RECEIVE_DATA || commandParams.getCommandType() == AppInterface.CommandType.SEND_DATA)) {
                    sendTerminalResponse(commandParams.mCmdDet, ResultCode.OK, false, 0, null);
                    break;
                }
                break;
            default:
                CatLog.m5d(this, "Unsupported command");
                return;
        }
        this.mCurrntCmd = catCmdMessage;
        broadcastCatCmdIntent(catCmdMessage);
    }

    public void sendStkSms(String str, String str2, int i, CommandParams commandParams, ProxyController proxyController) {
        proxyController.getSmsController().sendTextForSubscriber(i, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), str2, null, str, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.internal.telephony.cat.SMS_SENT_ACTION").putExtra("cmdDetails", commandParams.mCmdDet).setPackage(this.mContext.getPackageName()), 33554432), PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.internal.telephony.cat.SMS_DELIVERY_ACTION").putExtra("cmdDetails", commandParams.mCmdDet).setPackage(this.mContext.getPackageName()), 33554432), false, 0L, true, true);
    }

    private void broadcastCatCmdIntent(CatCmdMessage catCmdMessage) {
        Intent intent = new Intent(AppInterface.CAT_CMD_ACTION);
        intent.putExtra("STK CMD", catCmdMessage);
        intent.putExtra("SLOT_ID", this.mSlotId);
        intent.setComponent(AppInterface.getDefaultSTKApplication());
        CatLog.m5d(this, "Sending CmdMsg: " + catCmdMessage + " on slotid:" + this.mSlotId);
        this.mContext.sendBroadcast(intent, AppInterface.STK_PERMISSION);
    }

    private void handleSessionEnd() {
        CatLog.m5d(this, "SESSION END on " + this.mSlotId);
        this.mCurrntCmd = this.mMenuCmd;
        Intent intent = new Intent(AppInterface.CAT_SESSION_END_ACTION);
        intent.putExtra("SLOT_ID", this.mSlotId);
        intent.setComponent(AppInterface.getDefaultSTKApplication());
        this.mContext.sendBroadcast(intent, AppInterface.STK_PERMISSION);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void sendTerminalResponse(CommandDetails commandDetails, ResultCode resultCode, boolean z, int i, ResponseData responseData) {
        if (commandDetails == null) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CatCmdMessage catCmdMessage = this.mCurrntCmd;
        Input geInput = catCmdMessage != null ? catCmdMessage.geInput() : null;
        int value = ComprehensionTlvTag.COMMAND_DETAILS.value();
        if (commandDetails.compRequired) {
            value |= 128;
        }
        byteArrayOutputStream.write(value);
        byteArrayOutputStream.write(3);
        byteArrayOutputStream.write(commandDetails.commandNumber);
        byteArrayOutputStream.write(commandDetails.typeOfCommand);
        byteArrayOutputStream.write(commandDetails.commandQualifier);
        byteArrayOutputStream.write(ComprehensionTlvTag.DEVICE_IDENTITIES.value());
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(130);
        byteArrayOutputStream.write(129);
        int value2 = ComprehensionTlvTag.RESULT.value();
        if (commandDetails.compRequired) {
            value2 |= 128;
        }
        byteArrayOutputStream.write(value2);
        byteArrayOutputStream.write(z ? 2 : 1);
        byteArrayOutputStream.write(resultCode.value());
        if (z) {
            byteArrayOutputStream.write(i);
        }
        if (responseData != null) {
            responseData.format(byteArrayOutputStream);
        } else {
            encodeOptionalTags(commandDetails, resultCode, geInput, byteArrayOutputStream);
        }
        this.mCmdIf.sendTerminalResponse(IccUtils.bytesToHexString(byteArrayOutputStream.toByteArray()), null);
    }

    private void encodeOptionalTags(CommandDetails commandDetails, ResultCode resultCode, Input input, ByteArrayOutputStream byteArrayOutputStream) {
        AppInterface.CommandType fromInt = AppInterface.CommandType.fromInt(commandDetails.typeOfCommand);
        if (fromInt != null) {
            int i = C00832.f2xca33cf42[fromInt.ordinal()];
            if (i == 5) {
                if (commandDetails.commandQualifier == 4 && resultCode.value() == ResultCode.OK.value()) {
                    getPliResponse(byteArrayOutputStream);
                    return;
                }
                return;
            } else if (i == 8 || i == 9) {
                if (resultCode.value() != ResultCode.NO_RESPONSE_FROM_USER.value() || input == null || input.duration == null) {
                    return;
                }
                getInKeyResponse(byteArrayOutputStream, input);
                return;
            } else {
                CatLog.m5d(this, "encodeOptionalTags() Unsupported Cmd details=" + commandDetails);
                return;
            }
        }
        CatLog.m5d(this, "encodeOptionalTags() bad Cmd details=" + commandDetails);
    }

    private void getInKeyResponse(ByteArrayOutputStream byteArrayOutputStream, Input input) {
        byteArrayOutputStream.write(ComprehensionTlvTag.DURATION.value());
        byteArrayOutputStream.write(2);
        Duration.TimeUnit timeUnit = input.duration.timeUnit;
        byteArrayOutputStream.write(Duration.TimeUnit.SECOND.value());
        byteArrayOutputStream.write(input.duration.timeInterval);
    }

    private void getPliResponse(ByteArrayOutputStream byteArrayOutputStream) {
        String language = Locale.getDefault().getLanguage();
        if (language != null) {
            byteArrayOutputStream.write(ComprehensionTlvTag.LANGUAGE.value());
            ResponseData.writeLength(byteArrayOutputStream, language.length());
            byteArrayOutputStream.write(language.getBytes(), 0, language.length());
        }
    }

    private void sendMenuSelection(int i, boolean z) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(211);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(ComprehensionTlvTag.DEVICE_IDENTITIES.value() | 128);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(129);
        byteArrayOutputStream.write(ComprehensionTlvTag.ITEM_ID.value() | 128);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(i);
        if (z) {
            byteArrayOutputStream.write(ComprehensionTlvTag.HELP_REQUEST.value());
            byteArrayOutputStream.write(0);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArray[1] = (byte) (byteArray.length - 2);
        this.mCmdIf.sendEnvelope(IccUtils.bytesToHexString(byteArray), null);
    }

    private void eventDownload(int i, int i2, int i3, byte[] bArr, boolean z) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(BerTlv.BER_EVENT_DOWNLOAD_TAG);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(ComprehensionTlvTag.EVENT_LIST.value() | 128);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(i);
        byteArrayOutputStream.write(ComprehensionTlvTag.DEVICE_IDENTITIES.value() | 128);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(i2);
        byteArrayOutputStream.write(i3);
        if (i == 5) {
            CatLog.m5d(sInstance, " Sending Idle Screen Available event download to ICC");
        } else if (i == 7) {
            CatLog.m5d(sInstance, " Sending Language Selection event download to ICC");
            byteArrayOutputStream.write(ComprehensionTlvTag.LANGUAGE.value() | 128);
            byteArrayOutputStream.write(2);
        }
        if (bArr != null) {
            for (byte b : bArr) {
                byteArrayOutputStream.write(b);
            }
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArray[1] = (byte) (byteArray.length - 2);
        String bytesToHexString = IccUtils.bytesToHexString(byteArray);
        CatLog.m5d(this, "ENVELOPE COMMAND: " + bytesToHexString);
        this.mCmdIf.sendEnvelope(bytesToHexString, null);
    }

    public static AppInterface getInstance() {
        int slotIndex;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            if (SubscriptionManagerService.getInstance() != null) {
                slotIndex = SubscriptionManagerService.getInstance().getSlotIndex(SubscriptionManagerService.getInstance().getDefaultSubId());
            }
            slotIndex = 0;
        } else {
            SubscriptionController subscriptionController = SubscriptionController.getInstance();
            if (subscriptionController != null) {
                slotIndex = subscriptionController.getSlotIndex(subscriptionController.getDefaultSubId());
            }
            slotIndex = 0;
        }
        return getInstance(null, null, null, slotIndex);
    }

    public static AppInterface getInstance(int i) {
        return getInstance(null, null, null, i);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        AsyncResult asyncResult;
        Object obj2;
        Object obj3;
        CatLog.m5d(this, "handleMessage[" + message.what + "]");
        int i = message.what;
        if (i != 20) {
            if (i != 30) {
                String str = null;
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        CatLog.m5d(this, "ril message arrived,slotid:" + this.mSlotId);
                        Object obj4 = message.obj;
                        if (obj4 != null && (asyncResult = (AsyncResult) obj4) != null && (obj2 = asyncResult.result) != null) {
                            try {
                                str = (String) obj2;
                            } catch (ClassCastException unused) {
                                return;
                            }
                        }
                        this.mMsgDecoder.sendStartDecodingMessageParams(new RilMessage(message.what, str));
                        return;
                    case 4:
                        this.mMsgDecoder.sendStartDecodingMessageParams(new RilMessage(i, null));
                        return;
                    case 6:
                        handleCmdResponse((CatResponseMessage) message.obj);
                        return;
                    default:
                        switch (i) {
                            case 8:
                                CatLog.m5d(this, "MSG_ID_ICC_CHANGED");
                                updateIccAvailability();
                                return;
                            case 9:
                                CatLog.m5d(this, "Received CAT CC Alpha message from card");
                                Object obj5 = message.obj;
                                if (obj5 != null) {
                                    AsyncResult asyncResult2 = (AsyncResult) obj5;
                                    if (asyncResult2 != null && (obj3 = asyncResult2.result) != null) {
                                        broadcastAlphaMessage((String) obj3);
                                        return;
                                    } else {
                                        CatLog.m5d(this, "CAT Alpha message: ar.result is null");
                                        return;
                                    }
                                }
                                CatLog.m5d(this, "CAT Alpha message: msg.obj is null");
                                return;
                            case 10:
                                handleRilMsg((RilMessage) message.obj);
                                return;
                            default:
                                throw new AssertionError("Unrecognized CAT command: " + message.what);
                        }
                }
            }
            Object obj6 = message.obj;
            if (obj6 != null) {
                AsyncResult asyncResult3 = (AsyncResult) obj6;
                if (asyncResult3 != null && (obj = asyncResult3.result) != null) {
                    broadcastCardStateAndIccRefreshResp(IccCardStatus.CardState.CARDSTATE_PRESENT, (IccRefreshResponse) obj);
                    return;
                }
                CatLog.m5d(this, "Icc REFRESH with exception: " + asyncResult3.exception);
                return;
            }
            CatLog.m5d(this, "IccRefresh Message is null");
        }
    }

    private void broadcastCardStateAndIccRefreshResp(IccCardStatus.CardState cardState, IccRefreshResponse iccRefreshResponse) {
        Intent intent = new Intent(AppInterface.CAT_ICC_STATUS_CHANGE);
        boolean z = cardState == IccCardStatus.CardState.CARDSTATE_PRESENT;
        if (iccRefreshResponse != null) {
            intent.putExtra(AppInterface.REFRESH_RESULT, iccRefreshResponse.refreshResult);
            CatLog.m5d(this, "Sending IccResult with Result: " + iccRefreshResponse.refreshResult);
        }
        intent.putExtra(AppInterface.CARD_STATUS, z);
        intent.setComponent(AppInterface.getDefaultSTKApplication());
        intent.putExtra("SLOT_ID", this.mSlotId);
        CatLog.m5d(this, "Sending Card Status: " + cardState + " cardPresent: " + z + "SLOT_ID: " + this.mSlotId);
        this.mContext.sendBroadcast(intent, AppInterface.STK_PERMISSION);
    }

    private void broadcastAlphaMessage(String str) {
        CatLog.m5d(this, "Broadcasting CAT Alpha message from card: " + str);
        Intent intent = new Intent(AppInterface.CAT_ALPHA_NOTIFY_ACTION);
        intent.addFlags(268435456);
        intent.putExtra(AppInterface.ALPHA_STRING, str);
        intent.putExtra("SLOT_ID", this.mSlotId);
        intent.setComponent(AppInterface.getDefaultSTKApplication());
        this.mContext.sendBroadcast(intent, AppInterface.STK_PERMISSION);
    }

    @Override // com.android.internal.telephony.cat.AppInterface
    public synchronized void onCmdResponse(CatResponseMessage catResponseMessage) {
        if (catResponseMessage == null) {
            return;
        }
        obtainMessage(6, catResponseMessage).sendToTarget();
    }

    private boolean validateResponse(CatResponseMessage catResponseMessage) {
        if (catResponseMessage.mCmdDet.typeOfCommand == AppInterface.CommandType.SET_UP_EVENT_LIST.value() || catResponseMessage.mCmdDet.typeOfCommand == AppInterface.CommandType.SET_UP_MENU.value()) {
            CatLog.m5d(this, "CmdType: " + catResponseMessage.mCmdDet.typeOfCommand);
            return true;
        }
        CatCmdMessage catCmdMessage = this.mCurrntCmd;
        if (catCmdMessage != null) {
            boolean compareTo = catResponseMessage.mCmdDet.compareTo(catCmdMessage.mCmdDet);
            CatLog.m5d(this, "isResponse for last valid cmd: " + compareTo);
            return compareTo;
        }
        return false;
    }

    private boolean removeMenu(Menu menu) {
        try {
            if (menu.items.size() == 1) {
                if (menu.items.get(0) == null) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException unused) {
            CatLog.m5d(this, "Unable to get Menu's items size");
            return true;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:23:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void handleCmdResponse(CatResponseMessage catResponseMessage) {
        boolean z;
        int i;
        ResponseData selectItemResponseData;
        GetInkeyInputResponseData getInkeyInputResponseData;
        if (validateResponse(catResponseMessage)) {
            CommandDetails cmdDetails = catResponseMessage.getCmdDetails();
            AppInterface.CommandType fromInt = AppInterface.CommandType.fromInt(cmdDetails.typeOfCommand);
            switch (C00832.$SwitchMap$com$android$internal$telephony$cat$ResultCode[catResponseMessage.mResCode.ordinal()]) {
                case 1:
                    z = true;
                    i = C00832.f2xca33cf42[fromInt.ordinal()];
                    if (i != 1) {
                        sendMenuSelection(catResponseMessage.mUsersMenuSelection, catResponseMessage.mResCode == ResultCode.HELP_INFO_REQUIRED);
                        return;
                    }
                    if (i != 2) {
                        if (i == 4) {
                            int i2 = catResponseMessage.mEventValue;
                            if (5 == i2) {
                                eventDownload(i2, 2, 129, catResponseMessage.mAddedInfo, false);
                                return;
                            } else {
                                eventDownload(i2, 130, 129, catResponseMessage.mAddedInfo, false);
                                return;
                            }
                        } else if (i != 17 && i != 19) {
                            switch (i) {
                                case 6:
                                    if (catResponseMessage.mResCode == ResultCode.LAUNCH_BROWSER_ERROR) {
                                        catResponseMessage.setAdditionalInfo(4);
                                        break;
                                    } else {
                                        catResponseMessage.mIncludeAdditionalInfo = false;
                                        catResponseMessage.mAdditionalInfo = 0;
                                        break;
                                    }
                                case 7:
                                    selectItemResponseData = new SelectItemResponseData(catResponseMessage.mUsersMenuSelection);
                                    getInkeyInputResponseData = selectItemResponseData;
                                    break;
                                case 8:
                                case 9:
                                    Input geInput = this.mCurrntCmd.geInput();
                                    if (!geInput.yesNo) {
                                        if (!z) {
                                            getInkeyInputResponseData = new GetInkeyInputResponseData(catResponseMessage.mUsersInput, geInput.ucs2, geInput.packed);
                                            break;
                                        }
                                    } else {
                                        selectItemResponseData = new GetInkeyInputResponseData(catResponseMessage.mUsersYesNoSelection);
                                        getInkeyInputResponseData = selectItemResponseData;
                                        break;
                                    }
                                    break;
                            }
                            sendTerminalResponse(cmdDetails, catResponseMessage.mResCode, catResponseMessage.mIncludeAdditionalInfo, catResponseMessage.mAdditionalInfo, getInkeyInputResponseData);
                            this.mCurrntCmd = null;
                            return;
                        } else {
                            this.mCmdIf.handleCallSetupRequestFromSim(catResponseMessage.mUsersConfirm, null);
                            this.mCurrntCmd = null;
                            return;
                        }
                    } else if (catResponseMessage.mResCode == ResultCode.TERMINAL_CRNTLY_UNABLE_TO_PROCESS) {
                        catResponseMessage.setAdditionalInfo(1);
                    } else {
                        catResponseMessage.mIncludeAdditionalInfo = false;
                        catResponseMessage.mAdditionalInfo = 0;
                    }
                    getInkeyInputResponseData = null;
                    sendTerminalResponse(cmdDetails, catResponseMessage.mResCode, catResponseMessage.mIncludeAdditionalInfo, catResponseMessage.mAdditionalInfo, getInkeyInputResponseData);
                    this.mCurrntCmd = null;
                    return;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    z = false;
                    i = C00832.f2xca33cf42[fromInt.ordinal()];
                    if (i != 1) {
                    }
                    break;
                case 14:
                case 15:
                    if (fromInt == AppInterface.CommandType.SET_UP_CALL || fromInt == AppInterface.CommandType.OPEN_CHANNEL) {
                        this.mCmdIf.handleCallSetupRequestFromSim(false, null);
                        this.mCurrntCmd = null;
                        return;
                    }
                    getInkeyInputResponseData = null;
                    sendTerminalResponse(cmdDetails, catResponseMessage.mResCode, catResponseMessage.mIncludeAdditionalInfo, catResponseMessage.mAdditionalInfo, getInkeyInputResponseData);
                    this.mCurrntCmd = null;
                    return;
                case 16:
                    if (fromInt == AppInterface.CommandType.SET_UP_CALL) {
                        this.mCmdIf.handleCallSetupRequestFromSim(false, null);
                        this.mCurrntCmd = null;
                        return;
                    }
                    getInkeyInputResponseData = null;
                    sendTerminalResponse(cmdDetails, catResponseMessage.mResCode, catResponseMessage.mIncludeAdditionalInfo, catResponseMessage.mAdditionalInfo, getInkeyInputResponseData);
                    this.mCurrntCmd = null;
                    return;
                case 17:
                    getInkeyInputResponseData = null;
                    sendTerminalResponse(cmdDetails, catResponseMessage.mResCode, catResponseMessage.mIncludeAdditionalInfo, catResponseMessage.mAdditionalInfo, getInkeyInputResponseData);
                    this.mCurrntCmd = null;
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.cat.CatService$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00832 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$cat$AppInterface$CommandType */
        static final /* synthetic */ int[] f2xca33cf42;
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$cat$ResultCode;

        static {
            int[] iArr = new int[ResultCode.values().length];
            $SwitchMap$com$android$internal$telephony$cat$ResultCode = iArr;
            try {
                iArr[ResultCode.HELP_INFO_REQUIRED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.OK.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_WITH_PARTIAL_COMPREHENSION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_WITH_MISSING_INFO.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_WITH_ADDITIONAL_EFS_READ.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_ICON_NOT_DISPLAYED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_MODIFIED_BY_NAA.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_LIMITED_SERVICE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_WITH_MODIFICATION.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_NAA_NOT_ACTIVE.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.PRFRMD_TONE_NOT_PLAYED.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.LAUNCH_BROWSER_ERROR.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.TERMINAL_CRNTLY_UNABLE_TO_PROCESS.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.BACKWARD_MOVE_BY_USER.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.USER_NOT_ACCEPT.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.NO_RESPONSE_FROM_USER.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.UICC_SESSION_TERM_BY_USER.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            int[] iArr2 = new int[AppInterface.CommandType.values().length];
            f2xca33cf42 = iArr2;
            try {
                iArr2[AppInterface.CommandType.SET_UP_MENU.ordinal()] = 1;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.DISPLAY_TEXT.ordinal()] = 2;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SET_UP_IDLE_MODE_TEXT.ordinal()] = 3;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SET_UP_EVENT_LIST.ordinal()] = 4;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.PROVIDE_LOCAL_INFORMATION.ordinal()] = 5;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.LAUNCH_BROWSER.ordinal()] = 6;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SELECT_ITEM.ordinal()] = 7;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.GET_INPUT.ordinal()] = 8;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.GET_INKEY.ordinal()] = 9;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.REFRESH.ordinal()] = 10;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.RUN_AT.ordinal()] = 11;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SEND_SMS.ordinal()] = 12;
            } catch (NoSuchFieldError unused29) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SEND_DTMF.ordinal()] = 13;
            } catch (NoSuchFieldError unused30) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SEND_SS.ordinal()] = 14;
            } catch (NoSuchFieldError unused31) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SEND_USSD.ordinal()] = 15;
            } catch (NoSuchFieldError unused32) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.PLAY_TONE.ordinal()] = 16;
            } catch (NoSuchFieldError unused33) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SET_UP_CALL.ordinal()] = 17;
            } catch (NoSuchFieldError unused34) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.LANGUAGE_NOTIFICATION.ordinal()] = 18;
            } catch (NoSuchFieldError unused35) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.OPEN_CHANNEL.ordinal()] = 19;
            } catch (NoSuchFieldError unused36) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.CLOSE_CHANNEL.ordinal()] = 20;
            } catch (NoSuchFieldError unused37) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.RECEIVE_DATA.ordinal()] = 21;
            } catch (NoSuchFieldError unused38) {
            }
            try {
                f2xca33cf42[AppInterface.CommandType.SEND_DATA.ordinal()] = 22;
            } catch (NoSuchFieldError unused39) {
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isStkAppInstalled() {
        List<ResolveInfo> queryBroadcastReceivers = this.mContext.getPackageManager().queryBroadcastReceivers(new Intent(AppInterface.CAT_CMD_ACTION), 128);
        return (queryBroadcastReceivers == null ? 0 : queryBroadcastReceivers.size()) > 0;
    }

    public void update(CommandsInterface commandsInterface, Context context, UiccProfile uiccProfile) {
        UiccCardApplication uiccCardApplication;
        IccRecords iccRecords;
        if (uiccProfile != null) {
            uiccCardApplication = uiccProfile.getApplicationIndex(0);
            iccRecords = uiccCardApplication != null ? uiccCardApplication.getIccRecords() : null;
        } else {
            uiccCardApplication = null;
            iccRecords = null;
        }
        synchronized (sInstanceLock) {
            if (iccRecords != null) {
                IccRecords iccRecords2 = mIccRecords;
                if (iccRecords2 != iccRecords) {
                    if (iccRecords2 != null) {
                        iccRecords2.unregisterForRecordsLoaded(this);
                    }
                    CatLog.m5d(this, "Reinitialize the Service with SIMRecords and UiccCardApplication");
                    mIccRecords = iccRecords;
                    mUiccApplication = uiccCardApplication;
                    iccRecords.registerForRecordsLoaded(this, 20, null);
                    CatLog.m5d(this, "registerForRecordsLoaded slotid=" + this.mSlotId + " instance:" + this);
                }
            }
        }
    }

    void updateIccAvailability() {
        UiccController uiccController = this.mUiccController;
        if (uiccController == null) {
            return;
        }
        IccCardStatus.CardState cardState = IccCardStatus.CardState.CARDSTATE_ABSENT;
        UiccCard uiccCard = uiccController.getUiccCard(this.mSlotId);
        if (uiccCard != null) {
            cardState = uiccCard.getCardState();
        }
        IccCardStatus.CardState cardState2 = this.mCardState;
        this.mCardState = cardState;
        CatLog.m5d(this, "New Card State = " + cardState + " Old Card State = " + cardState2);
        IccCardStatus.CardState cardState3 = IccCardStatus.CardState.CARDSTATE_PRESENT;
        if (cardState2 == cardState3 && cardState != cardState3) {
            broadcastCardStateAndIccRefreshResp(cardState, null);
        } else if (cardState2 == cardState3 || cardState != cardState3) {
        } else {
            this.mCmdIf.reportStkServiceIsRunning(null);
        }
    }

    private void changeLanguage(String str) throws RemoteException {
        LocaleList localeList = LocaleList.getDefault();
        Locale[] localeArr = new Locale[localeList.size() + 1];
        Locale locale = new Locale(str);
        int i = 0;
        localeArr[0] = locale;
        while (i < localeList.size()) {
            int i2 = i + 1;
            localeArr[i2] = localeList.get(i);
            i = i2;
        }
        ((ActivityManager) this.mContext.getSystemService(ActivityManager.class)).setDeviceLocales(new LocaleList(localeArr));
        BackupManager.dataChanged("com.android.providers.settings");
    }
}
