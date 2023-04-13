package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.cat.AppInterface;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.IccFileHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class CommandParamsFactory extends Handler {
    private static CommandParamsFactory sInstance;
    private RilMessageDecoder mCaller;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IconLoader mIconLoader;
    private boolean mNoAlphaUsrCnf;
    private String mRequestedLanguage;
    private String mSavedLanguage;
    private boolean mStkSmsSendViaTelephony;
    private CommandParams mCmdParams = null;
    private int mIconLoadState = 0;
    private boolean mloadIcon = false;

    public static synchronized CommandParamsFactory getInstance(RilMessageDecoder rilMessageDecoder, IccFileHandler iccFileHandler, Context context) {
        synchronized (CommandParamsFactory.class) {
            CommandParamsFactory commandParamsFactory = sInstance;
            if (commandParamsFactory != null) {
                return commandParamsFactory;
            }
            if (iccFileHandler != null) {
                return new CommandParamsFactory(rilMessageDecoder, iccFileHandler, context);
            }
            return null;
        }
    }

    private CommandParamsFactory(RilMessageDecoder rilMessageDecoder, IccFileHandler iccFileHandler, Context context) {
        this.mNoAlphaUsrCnf = false;
        this.mStkSmsSendViaTelephony = false;
        this.mCaller = rilMessageDecoder;
        this.mIconLoader = IconLoader.getInstance(this, iccFileHandler);
        try {
            this.mNoAlphaUsrCnf = context.getResources().getBoolean(17891811);
        } catch (Resources.NotFoundException unused) {
            this.mNoAlphaUsrCnf = false;
        }
        try {
            this.mStkSmsSendViaTelephony = context.getResources().getBoolean(17891812);
        } catch (Resources.NotFoundException unused2) {
            this.mStkSmsSendViaTelephony = false;
        }
    }

    private CommandDetails processCommandDetails(List<ComprehensionTlv> list) {
        ComprehensionTlv searchForTag;
        if (list != null && (searchForTag = searchForTag(ComprehensionTlvTag.COMMAND_DETAILS, list)) != null) {
            try {
                return ValueParser.retrieveCommandDetails(searchForTag);
            } catch (ResultException e) {
                CatLog.m5d(this, "processCommandDetails: Failed to procees command details e=" + e);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void make(BerTlv berTlv) {
        boolean processSelectItem;
        if (berTlv == null) {
            return;
        }
        this.mCmdParams = null;
        this.mIconLoadState = 0;
        if (berTlv.getTag() != 208) {
            sendCmdParams(ResultCode.CMD_TYPE_NOT_UNDERSTOOD);
            return;
        }
        List<ComprehensionTlv> comprehensionTlvs = berTlv.getComprehensionTlvs();
        CommandDetails processCommandDetails = processCommandDetails(comprehensionTlvs);
        if (processCommandDetails == null) {
            sendCmdParams(ResultCode.CMD_TYPE_NOT_UNDERSTOOD);
            return;
        }
        AppInterface.CommandType fromInt = AppInterface.CommandType.fromInt(processCommandDetails.typeOfCommand);
        if (fromInt == null) {
            this.mCmdParams = new CommandParams(processCommandDetails);
            sendCmdParams(ResultCode.BEYOND_TERMINAL_CAPABILITY);
        } else if (!berTlv.isLengthValid()) {
            this.mCmdParams = new CommandParams(processCommandDetails);
            sendCmdParams(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        } else {
            try {
                switch (C00851.f3xca33cf42[fromInt.ordinal()]) {
                    case 1:
                        processSelectItem = processSelectItem(processCommandDetails, comprehensionTlvs);
                        break;
                    case 2:
                        processSelectItem = processSelectItem(processCommandDetails, comprehensionTlvs);
                        break;
                    case 3:
                        processSelectItem = processDisplayText(processCommandDetails, comprehensionTlvs);
                        break;
                    case 4:
                        processSelectItem = processSetUpIdleModeText(processCommandDetails, comprehensionTlvs);
                        break;
                    case 5:
                        processSelectItem = processGetInkey(processCommandDetails, comprehensionTlvs);
                        break;
                    case 6:
                        processSelectItem = processGetInput(processCommandDetails, comprehensionTlvs);
                        break;
                    case 7:
                        if (this.mStkSmsSendViaTelephony) {
                            processSelectItem = processSMSEventNotify(processCommandDetails, comprehensionTlvs);
                            break;
                        } else {
                            processSelectItem = processEventNotify(processCommandDetails, comprehensionTlvs);
                            break;
                        }
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        processSelectItem = processEventNotify(processCommandDetails, comprehensionTlvs);
                        break;
                    case 13:
                    case 14:
                        processSelectItem = processSetupCall(processCommandDetails, comprehensionTlvs);
                        break;
                    case 15:
                        processSelectItem = processLaunchBrowser(processCommandDetails, comprehensionTlvs);
                        break;
                    case 16:
                        processSelectItem = processPlayTone(processCommandDetails, comprehensionTlvs);
                        break;
                    case 17:
                        processSelectItem = processSetUpEventList(processCommandDetails, comprehensionTlvs);
                        break;
                    case 18:
                        processSelectItem = processProvideLocalInfo(processCommandDetails, comprehensionTlvs);
                        break;
                    case 19:
                        processSelectItem = processLanguageNotification(processCommandDetails, comprehensionTlvs);
                        break;
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                        processSelectItem = processBIPClient(processCommandDetails, comprehensionTlvs);
                        break;
                    default:
                        this.mCmdParams = new CommandParams(processCommandDetails);
                        sendCmdParams(ResultCode.BEYOND_TERMINAL_CAPABILITY);
                        return;
                }
                if (processSelectItem) {
                    return;
                }
                sendCmdParams(ResultCode.OK);
            } catch (ResultException e) {
                CatLog.m5d(this, "make: caught ResultException e=" + e);
                this.mCmdParams = new CommandParams(processCommandDetails);
                sendCmdParams(e.result());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.cat.CommandParamsFactory$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00851 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$cat$AppInterface$CommandType */
        static final /* synthetic */ int[] f3xca33cf42;

        static {
            int[] iArr = new int[AppInterface.CommandType.values().length];
            f3xca33cf42 = iArr;
            try {
                iArr[AppInterface.CommandType.SET_UP_MENU.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SELECT_ITEM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.DISPLAY_TEXT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SET_UP_IDLE_MODE_TEXT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.GET_INKEY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.GET_INPUT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SEND_SMS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SEND_DTMF.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.REFRESH.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.RUN_AT.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SEND_SS.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SEND_USSD.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.GET_CHANNEL_STATUS.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SET_UP_CALL.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.LAUNCH_BROWSER.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.PLAY_TONE.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SET_UP_EVENT_LIST.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.PROVIDE_LOCAL_INFORMATION.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.LANGUAGE_NOTIFICATION.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.OPEN_CHANNEL.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.CLOSE_CHANNEL.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.RECEIVE_DATA.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                f3xca33cf42[AppInterface.CommandType.SEND_DATA.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1 && this.mIconLoader != null) {
            sendCmdParams(setIcons(message.obj));
        }
    }

    private ResultCode setIcons(Object obj) {
        Bitmap[] bitmapArr;
        if (obj == null) {
            CatLog.m5d(this, "Optional Icon data is NULL");
            this.mCmdParams.mLoadIconFailed = true;
            this.mloadIcon = false;
            return ResultCode.OK;
        }
        int i = this.mIconLoadState;
        if (i == 1) {
            this.mCmdParams.setIcon((Bitmap) obj);
        } else if (i == 2) {
            for (Bitmap bitmap : (Bitmap[]) obj) {
                this.mCmdParams.setIcon(bitmap);
                if (bitmap == null && this.mloadIcon) {
                    CatLog.m5d(this, "Optional Icon data is NULL while loading multi icons");
                    this.mCmdParams.mLoadIconFailed = true;
                }
            }
        }
        return ResultCode.OK;
    }

    private void sendCmdParams(ResultCode resultCode) {
        this.mCaller.sendMsgParamsDecoded(resultCode, this.mCmdParams);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ComprehensionTlv searchForTag(ComprehensionTlvTag comprehensionTlvTag, List<ComprehensionTlv> list) {
        return searchForNextTag(comprehensionTlvTag, list.iterator());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ComprehensionTlv searchForNextTag(ComprehensionTlvTag comprehensionTlvTag, Iterator<ComprehensionTlv> it) {
        int value = comprehensionTlvTag.value();
        while (it.hasNext()) {
            ComprehensionTlv next = it.next();
            if (next.getTag() == value) {
                return next;
            }
        }
        return null;
    }

    private boolean processDisplayText(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process DisplayText");
        TextMessage textMessage = new TextMessage();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.TEXT_STRING, list);
        if (searchForTag != null) {
            textMessage.text = ValueParser.retrieveTextString(searchForTag);
        }
        if (textMessage.text == null) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
        if (searchForTag(ComprehensionTlvTag.IMMEDIATE_RESPONSE, list) != null) {
            textMessage.responseNeeded = false;
        }
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag2 != null) {
            iconId = ValueParser.retrieveIconId(searchForTag2);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        ComprehensionTlv searchForTag3 = searchForTag(ComprehensionTlvTag.DURATION, list);
        if (searchForTag3 != null) {
            textMessage.duration = ValueParser.retrieveDuration(searchForTag3);
        }
        int i = commandDetails.commandQualifier;
        textMessage.isHighPriority = (i & 1) != 0;
        textMessage.userClear = (i & 128) != 0;
        this.mCmdParams = new DisplayTextParams(commandDetails, textMessage);
        if (iconId != null) {
            this.mloadIcon = true;
            this.mIconLoadState = 1;
            this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
            return true;
        }
        return false;
    }

    private boolean processSetUpIdleModeText(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process SetUpIdleModeText");
        TextMessage textMessage = new TextMessage();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.TEXT_STRING, list);
        if (searchForTag != null) {
            textMessage.text = ValueParser.retrieveTextString(searchForTag);
        }
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag2 != null) {
            iconId = ValueParser.retrieveIconId(searchForTag2);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        if (textMessage.text == null && iconId != null && !textMessage.iconSelfExplanatory) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
        this.mCmdParams = new DisplayTextParams(commandDetails, textMessage);
        if (iconId != null) {
            this.mloadIcon = true;
            this.mIconLoadState = 1;
            this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
            return true;
        }
        return false;
    }

    private boolean processGetInkey(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process GetInkey");
        Input input = new Input();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.TEXT_STRING, list);
        if (searchForTag != null) {
            input.text = ValueParser.retrieveTextString(searchForTag);
            ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
            if (searchForTag2 != null) {
                iconId = ValueParser.retrieveIconId(searchForTag2);
                input.iconSelfExplanatory = iconId.selfExplanatory;
            } else {
                iconId = null;
            }
            ComprehensionTlv searchForTag3 = searchForTag(ComprehensionTlvTag.DURATION, list);
            if (searchForTag3 != null) {
                input.duration = ValueParser.retrieveDuration(searchForTag3);
            }
            input.minLen = 1;
            input.maxLen = 1;
            int i = commandDetails.commandQualifier;
            input.digitOnly = (i & 1) == 0;
            input.ucs2 = (i & 2) != 0;
            input.yesNo = (i & 4) != 0;
            input.helpAvailable = (i & 128) != 0;
            input.echo = true;
            this.mCmdParams = new GetInputParams(commandDetails, input);
            if (iconId != null) {
                this.mloadIcon = true;
                this.mIconLoadState = 1;
                this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
                return true;
            }
            return false;
        }
        throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
    }

    private boolean processGetInput(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process GetInput");
        Input input = new Input();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.TEXT_STRING, list);
        if (searchForTag != null) {
            input.text = ValueParser.retrieveTextString(searchForTag);
            ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.RESPONSE_LENGTH, list);
            if (searchForTag2 != null) {
                try {
                    byte[] rawValue = searchForTag2.getRawValue();
                    int valueIndex = searchForTag2.getValueIndex();
                    input.minLen = rawValue[valueIndex] & 255;
                    input.maxLen = rawValue[valueIndex + 1] & 255;
                    ComprehensionTlv searchForTag3 = searchForTag(ComprehensionTlvTag.DEFAULT_TEXT, list);
                    if (searchForTag3 != null) {
                        input.defaultText = ValueParser.retrieveTextString(searchForTag3);
                    }
                    ComprehensionTlv searchForTag4 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
                    if (searchForTag4 != null) {
                        iconId = ValueParser.retrieveIconId(searchForTag4);
                        input.iconSelfExplanatory = iconId.selfExplanatory;
                    } else {
                        iconId = null;
                    }
                    ComprehensionTlv searchForTag5 = searchForTag(ComprehensionTlvTag.DURATION, list);
                    if (searchForTag5 != null) {
                        input.duration = ValueParser.retrieveDuration(searchForTag5);
                    }
                    int i = commandDetails.commandQualifier;
                    input.digitOnly = (i & 1) == 0;
                    boolean z = (i & 2) != 0;
                    input.ucs2 = z;
                    input.echo = (i & 4) == 0;
                    boolean z2 = (i & 8) != 0;
                    input.packed = z2;
                    input.helpAvailable = (i & 128) != 0;
                    if (z && input.maxLen > 118) {
                        CatLog.m5d(this, "UCS2: received maxLen = " + input.maxLen + ", truncating to " + TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE);
                        input.maxLen = TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE;
                    } else if (!z2 && input.maxLen > 239) {
                        CatLog.m5d(this, "GSM 7Bit Default: received maxLen = " + input.maxLen + ", truncating to 239");
                        input.maxLen = 239;
                    }
                    this.mCmdParams = new GetInputParams(commandDetails, input);
                    if (iconId != null) {
                        this.mloadIcon = true;
                        this.mIconLoadState = 1;
                        this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
                        return true;
                    }
                    return false;
                } catch (IndexOutOfBoundsException unused) {
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
                }
            }
            throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
        }
        throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
    }

    private boolean processSelectItem(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process SelectItem");
        Menu menu = new Menu();
        Iterator<ComprehensionTlv> it = list.iterator();
        AppInterface.CommandType fromInt = AppInterface.CommandType.fromInt(commandDetails.typeOfCommand);
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.ALPHA_ID, list);
        if (searchForTag != null) {
            menu.title = ValueParser.retrieveAlphaId(searchForTag, this.mNoAlphaUsrCnf);
        } else if (fromInt == AppInterface.CommandType.SET_UP_MENU) {
            throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
        }
        while (true) {
            ComprehensionTlv searchForNextTag = searchForNextTag(ComprehensionTlvTag.ITEM, it);
            if (searchForNextTag == null) {
                break;
            }
            menu.items.add(ValueParser.retrieveItem(searchForNextTag));
        }
        if (menu.items.size() == 0) {
            throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
        }
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ITEM_ID, list);
        if (searchForTag2 != null) {
            menu.defaultItem = ValueParser.retrieveItemId(searchForTag2) - 1;
        }
        ComprehensionTlv searchForTag3 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        ItemsIconId itemsIconId = null;
        if (searchForTag3 != null) {
            this.mIconLoadState = 1;
            iconId = ValueParser.retrieveIconId(searchForTag3);
            menu.titleIconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        ComprehensionTlv searchForTag4 = searchForTag(ComprehensionTlvTag.ITEM_ICON_ID_LIST, list);
        if (searchForTag4 != null) {
            this.mIconLoadState = 2;
            itemsIconId = ValueParser.retrieveItemsIconId(searchForTag4);
            menu.itemsIconSelfExplanatory = itemsIconId.selfExplanatory;
        }
        int i = commandDetails.commandQualifier;
        if ((i & 1) != 0) {
            if ((i & 2) == 0) {
                menu.presentationType = PresentationType.DATA_VALUES;
            } else {
                menu.presentationType = PresentationType.NAVIGATION_OPTIONS;
            }
        }
        menu.softKeyPreferred = (i & 4) != 0;
        menu.helpAvailable = (i & 128) != 0;
        this.mCmdParams = new SelectItemParams(commandDetails, menu, iconId != null);
        int i2 = this.mIconLoadState;
        if (i2 != 0) {
            if (i2 == 1) {
                this.mloadIcon = true;
                this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
            } else if (i2 == 2) {
                int[] iArr = itemsIconId.recordNumbers;
                if (iconId != null) {
                    int[] iArr2 = new int[iArr.length + 1];
                    iArr2[0] = iconId.recordNumber;
                    System.arraycopy(iArr, 0, iArr2, 1, iArr.length);
                    iArr = iArr2;
                }
                this.mloadIcon = true;
                this.mIconLoader.loadIcons(iArr, obtainMessage(1));
            }
            return true;
        }
        return false;
    }

    private boolean processEventNotify(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process EventNotify");
        TextMessage textMessage = new TextMessage();
        textMessage.text = ValueParser.retrieveAlphaId(searchForTag(ComprehensionTlvTag.ALPHA_ID, list), this.mNoAlphaUsrCnf);
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag != null) {
            iconId = ValueParser.retrieveIconId(searchForTag);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        textMessage.responseNeeded = false;
        this.mCmdParams = new DisplayTextParams(commandDetails, textMessage);
        if (iconId != null) {
            this.mloadIcon = true;
            this.mIconLoadState = 1;
            this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
            return true;
        }
        return false;
    }

    public boolean processSMSEventNotify(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        CatLog.m5d(this, "processSMSEventNotify");
        TextMessage textMessage = new TextMessage();
        textMessage.text = ValueParser.retrieveAlphaId(searchForTag(ComprehensionTlvTag.ALPHA_ID, list), this.mNoAlphaUsrCnf);
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag != null) {
            textMessage.iconSelfExplanatory = ValueParser.retrieveIconId(searchForTag).selfExplanatory;
        }
        textMessage.responseNeeded = false;
        DisplayTextParams displayTextParams = new DisplayTextParams(commandDetails, textMessage);
        SmsMessage retrieveTpduAsSmsMessage = ValueParser.retrieveTpduAsSmsMessage(searchForTag(ComprehensionTlvTag.SMS_TPDU, list));
        if (retrieveTpduAsSmsMessage != null) {
            TextMessage textMessage2 = new TextMessage();
            textMessage2.text = retrieveTpduAsSmsMessage.getMessageBody();
            TextMessage textMessage3 = new TextMessage();
            textMessage3.text = retrieveTpduAsSmsMessage.getRecipientAddress();
            this.mCmdParams = new SendSMSParams(commandDetails, textMessage2, textMessage3, displayTextParams);
            return false;
        }
        return true;
    }

    private boolean processSetUpEventList(CommandDetails commandDetails, List<ComprehensionTlv> list) {
        CatLog.m5d(this, "process SetUpEventList");
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.EVENT_LIST, list);
        if (searchForTag != null) {
            try {
                byte[] rawValue = searchForTag.getRawValue();
                int valueIndex = searchForTag.getValueIndex();
                int length = searchForTag.getLength();
                int[] iArr = new int[length];
                int i = 0;
                while (length > 0) {
                    int i2 = rawValue[valueIndex] & 255;
                    valueIndex++;
                    length--;
                    if (i2 == 4 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 15) {
                        iArr[i] = i2;
                        i++;
                    }
                }
                this.mCmdParams = new SetEventListParams(commandDetails, iArr);
            } catch (IndexOutOfBoundsException unused) {
                CatLog.m3e(this, " IndexOutofBoundException in processSetUpEventList");
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0075 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean processLaunchBrowser(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        String gsm8BitUnpackedToString;
        ComprehensionTlv searchForTag;
        int i;
        LaunchBrowserMode launchBrowserMode;
        CatLog.m5d(this, "process LaunchBrowser");
        TextMessage textMessage = new TextMessage();
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.URL, list);
        IconId iconId = null;
        if (searchForTag2 != null) {
            try {
                byte[] rawValue = searchForTag2.getRawValue();
                int valueIndex = searchForTag2.getValueIndex();
                int length = searchForTag2.getLength();
                if (length > 0) {
                    gsm8BitUnpackedToString = GsmAlphabet.gsm8BitUnpackedToString(rawValue, valueIndex, length);
                    textMessage.text = ValueParser.retrieveAlphaId(searchForTag(ComprehensionTlvTag.ALPHA_ID, list), this.mNoAlphaUsrCnf);
                    searchForTag = searchForTag(ComprehensionTlvTag.ICON_ID, list);
                    if (searchForTag != null) {
                        iconId = ValueParser.retrieveIconId(searchForTag);
                        textMessage.iconSelfExplanatory = iconId.selfExplanatory;
                    }
                    i = commandDetails.commandQualifier;
                    if (i != 2) {
                        launchBrowserMode = LaunchBrowserMode.USE_EXISTING_BROWSER;
                    } else if (i != 3) {
                        launchBrowserMode = LaunchBrowserMode.LAUNCH_IF_NOT_ALREADY_LAUNCHED;
                    } else {
                        launchBrowserMode = LaunchBrowserMode.LAUNCH_NEW_BROWSER;
                    }
                    this.mCmdParams = new LaunchBrowserParams(commandDetails, textMessage, gsm8BitUnpackedToString, launchBrowserMode);
                    if (iconId == null) {
                        this.mIconLoadState = 1;
                        this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
                        return true;
                    }
                    return false;
                }
            } catch (IndexOutOfBoundsException unused) {
                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
            }
        }
        gsm8BitUnpackedToString = null;
        textMessage.text = ValueParser.retrieveAlphaId(searchForTag(ComprehensionTlvTag.ALPHA_ID, list), this.mNoAlphaUsrCnf);
        searchForTag = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag != null) {
        }
        i = commandDetails.commandQualifier;
        if (i != 2) {
        }
        this.mCmdParams = new LaunchBrowserParams(commandDetails, textMessage, gsm8BitUnpackedToString, launchBrowserMode);
        if (iconId == null) {
        }
    }

    private boolean processPlayTone(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        Tone tone;
        CatLog.m5d(this, "process PlayTone");
        TextMessage textMessage = new TextMessage();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.TONE, list);
        IconId iconId = null;
        if (searchForTag == null || searchForTag.getLength() <= 0) {
            tone = null;
        } else {
            try {
                tone = Tone.fromInt(searchForTag.getRawValue()[searchForTag.getValueIndex()]);
            } catch (IndexOutOfBoundsException unused) {
                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
            }
        }
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ALPHA_ID, list);
        if (searchForTag2 != null) {
            String retrieveAlphaId = ValueParser.retrieveAlphaId(searchForTag2, this.mNoAlphaUsrCnf);
            textMessage.text = retrieveAlphaId;
            if (retrieveAlphaId == null) {
                textMessage.text = PhoneConfigurationManager.SSSS;
            }
        }
        ComprehensionTlv searchForTag3 = searchForTag(ComprehensionTlvTag.DURATION, list);
        Duration retrieveDuration = searchForTag3 != null ? ValueParser.retrieveDuration(searchForTag3) : null;
        ComprehensionTlv searchForTag4 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag4 != null) {
            iconId = ValueParser.retrieveIconId(searchForTag4);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        }
        IconId iconId2 = iconId;
        boolean z = (commandDetails.commandQualifier & 1) != 0;
        textMessage.responseNeeded = false;
        this.mCmdParams = new PlayToneParams(commandDetails, textMessage, tone, retrieveDuration, z);
        if (iconId2 != null) {
            this.mIconLoadState = 1;
            this.mIconLoader.loadIcon(iconId2.recordNumber, obtainMessage(1));
            return true;
        }
        return false;
    }

    private boolean processSetupCall(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        IconId iconId;
        CatLog.m5d(this, "process SetupCall");
        Iterator<ComprehensionTlv> it = list.iterator();
        TextMessage textMessage = new TextMessage();
        TextMessage textMessage2 = new TextMessage();
        ComprehensionTlvTag comprehensionTlvTag = ComprehensionTlvTag.ALPHA_ID;
        textMessage.text = ValueParser.retrieveAlphaId(searchForNextTag(comprehensionTlvTag, it), this.mNoAlphaUsrCnf);
        ComprehensionTlvTag comprehensionTlvTag2 = ComprehensionTlvTag.ICON_ID;
        ComprehensionTlv searchForTag = searchForTag(comprehensionTlvTag2, list);
        IconId iconId2 = null;
        if (searchForTag != null) {
            iconId = ValueParser.retrieveIconId(searchForTag);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        ComprehensionTlv searchForNextTag = searchForNextTag(comprehensionTlvTag, it);
        if (searchForNextTag != null) {
            textMessage2.text = ValueParser.retrieveAlphaId(searchForNextTag, this.mNoAlphaUsrCnf);
        }
        ComprehensionTlv searchForTag2 = searchForTag(comprehensionTlvTag2, list);
        if (searchForTag2 != null) {
            iconId2 = ValueParser.retrieveIconId(searchForTag2);
            textMessage2.iconSelfExplanatory = iconId2.selfExplanatory;
        }
        this.mCmdParams = new CallSetupParams(commandDetails, textMessage, textMessage2);
        if (iconId == null && iconId2 == null) {
            return false;
        }
        this.mIconLoadState = 2;
        int[] iArr = new int[2];
        iArr[0] = iconId != null ? iconId.recordNumber : -1;
        iArr[1] = iconId2 != null ? iconId2.recordNumber : -1;
        this.mIconLoader.loadIcons(iArr, obtainMessage(1));
        return true;
    }

    private boolean processProvideLocalInfo(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        CatLog.m5d(this, "process ProvideLocalInfo");
        int i = commandDetails.commandQualifier;
        if (i == 3) {
            CatLog.m5d(this, "PLI [DTTZ_SETTING]");
            this.mCmdParams = new CommandParams(commandDetails);
            return false;
        } else if (i == 4) {
            CatLog.m5d(this, "PLI [LANGUAGE_SETTING]");
            this.mCmdParams = new CommandParams(commandDetails);
            return false;
        } else {
            CatLog.m5d(this, "PLI[" + commandDetails.commandQualifier + "] Command Not Supported");
            this.mCmdParams = new CommandParams(commandDetails);
            throw new ResultException(ResultCode.BEYOND_TERMINAL_CAPABILITY);
        }
    }

    private boolean processLanguageNotification(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        String str;
        CatLog.m5d(this, "process Language Notification");
        String language = Locale.getDefault().getLanguage();
        int i = commandDetails.commandQualifier;
        String str2 = null;
        if (i == 0) {
            if (TextUtils.isEmpty(this.mSavedLanguage) || TextUtils.isEmpty(this.mRequestedLanguage) || !this.mRequestedLanguage.equals(language)) {
                str = null;
            } else {
                CatLog.m5d(this, "Non-specific language notification changes the language setting back to " + this.mSavedLanguage);
                str = this.mSavedLanguage;
            }
            this.mSavedLanguage = null;
            this.mRequestedLanguage = null;
            str2 = str;
        } else if (i == 1) {
            ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.LANGUAGE, list);
            if (searchForTag != null) {
                if (searchForTag.getLength() != 2) {
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
                }
                str2 = GsmAlphabet.gsm8BitUnpackedToString(searchForTag.getRawValue(), searchForTag.getValueIndex(), 2);
                if (TextUtils.isEmpty(this.mSavedLanguage) || (!TextUtils.isEmpty(this.mRequestedLanguage) && !this.mRequestedLanguage.equals(language))) {
                    this.mSavedLanguage = language;
                }
                this.mRequestedLanguage = str2;
                CatLog.m5d(this, "Specific language notification changes the language setting to " + this.mRequestedLanguage);
            }
        } else {
            CatLog.m5d(this, "LN[" + commandDetails.commandQualifier + "] Command Not Supported");
        }
        this.mCmdParams = new LanguageParams(commandDetails, str2);
        return false;
    }

    private boolean processBIPClient(CommandDetails commandDetails, List<ComprehensionTlv> list) throws ResultException {
        AppInterface.CommandType fromInt;
        boolean z;
        IconId iconId;
        if (AppInterface.CommandType.fromInt(commandDetails.typeOfCommand) != null) {
            CatLog.m5d(this, "process " + fromInt.name());
        }
        TextMessage textMessage = new TextMessage();
        ComprehensionTlv searchForTag = searchForTag(ComprehensionTlvTag.ALPHA_ID, list);
        if (searchForTag != null) {
            textMessage.text = ValueParser.retrieveAlphaId(searchForTag, this.mNoAlphaUsrCnf);
            CatLog.m5d(this, "alpha TLV text=" + textMessage.text);
            z = true;
        } else {
            z = false;
        }
        ComprehensionTlv searchForTag2 = searchForTag(ComprehensionTlvTag.ICON_ID, list);
        if (searchForTag2 != null) {
            iconId = ValueParser.retrieveIconId(searchForTag2);
            textMessage.iconSelfExplanatory = iconId.selfExplanatory;
        } else {
            iconId = null;
        }
        textMessage.responseNeeded = false;
        this.mCmdParams = new BIPClientParams(commandDetails, textMessage, z);
        if (iconId != null) {
            this.mIconLoadState = 1;
            this.mIconLoader.loadIcon(iconId.recordNumber, obtainMessage(1));
            return true;
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void dispose() {
        this.mIconLoader.dispose();
        this.mIconLoader = null;
        this.mCmdParams = null;
        this.mCaller = null;
        sInstance = null;
    }
}
