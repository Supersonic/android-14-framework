package com.android.internal.telephony;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import com.android.ims.FeatureConnector;
import com.android.ims.ImsManager;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.cdma.CdmaSubscriptionSourceManager;
import com.android.internal.telephony.cdma.EriManager;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataProfileManager;
import com.android.internal.telephony.data.DataServiceManager;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.imsphone.ImsExternalCallTracker;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.imsphone.ImsPhoneCallTracker;
import com.android.internal.telephony.nitz.NitzStateMachineImpl;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.telephony.Rlog;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class TelephonyComponentFactory {
    private static final String TAG = "TelephonyComponentFactory";
    private static TelephonyComponentFactory sInstance;
    private InjectedComponents mInjectedComponents;
    private final TelephonyFacade mTelephonyFacade = new TelephonyFacade();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InjectedComponents {
        private final Set<String> mComponentNames;
        private TelephonyComponentFactory mInjectedInstance;
        private String mJarPath;
        private String mPackageName;

        private InjectedComponents() {
            this.mComponentNames = new HashSet();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getValidatedPaths() {
            if (TextUtils.isEmpty(this.mPackageName) || TextUtils.isEmpty(this.mJarPath)) {
                return null;
            }
            String str = this.mJarPath;
            String str2 = File.pathSeparator;
            return (String) Arrays.stream(str.split(str2)).filter(new Predicate() { // from class: com.android.internal.telephony.TelephonyComponentFactory$InjectedComponents$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getValidatedPaths$0;
                    lambda$getValidatedPaths$0 = TelephonyComponentFactory.InjectedComponents.lambda$getValidatedPaths$0((String) obj);
                    return lambda$getValidatedPaths$0;
                }
            }).filter(new Predicate() { // from class: com.android.internal.telephony.TelephonyComponentFactory$InjectedComponents$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getValidatedPaths$1;
                    lambda$getValidatedPaths$1 = TelephonyComponentFactory.InjectedComponents.lambda$getValidatedPaths$1((String) obj);
                    return lambda$getValidatedPaths$1;
                }
            }).distinct().collect(Collectors.joining(str2));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$getValidatedPaths$0(String str) {
            return str.startsWith("/system/") || str.startsWith("/product/") || str.startsWith("/system_ext/");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$getValidatedPaths$1(String str) {
            try {
                return (Os.statvfs(str).f_flag & ((long) OsConstants.ST_RDONLY)) != 0;
            } catch (ErrnoException e) {
                String str2 = TelephonyComponentFactory.TAG;
                Rlog.w(str2, "Injection jar is not protected , path: " + str + e.getMessage());
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void makeInjectedInstance() {
            String validatedPaths = getValidatedPaths();
            String str = TelephonyComponentFactory.TAG;
            Rlog.d(str, "validated paths: " + validatedPaths);
            if (TextUtils.isEmpty(validatedPaths)) {
                return;
            }
            try {
                this.mInjectedInstance = (TelephonyComponentFactory) new PathClassLoader(validatedPaths, ClassLoader.getSystemClassLoader()).loadClass(this.mPackageName).newInstance();
            } catch (ClassNotFoundException e) {
                String str2 = TelephonyComponentFactory.TAG;
                Rlog.e(str2, "failed: " + e.getMessage());
            } catch (IllegalAccessException | InstantiationException e2) {
                String str3 = TelephonyComponentFactory.TAG;
                Rlog.e(str3, "injection failed: " + e2.getMessage());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isComponentInjected(String str) {
            if (this.mInjectedInstance == null) {
                return false;
            }
            return this.mComponentNames.contains(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void parseXml(XmlPullParser xmlPullParser) {
            parseXmlByTag(xmlPullParser, false, new Consumer() { // from class: com.android.internal.telephony.TelephonyComponentFactory$InjectedComponents$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyComponentFactory.InjectedComponents.this.lambda$parseXml$2((XmlPullParser) obj);
                }
            }, "injection");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$parseXml$2(XmlPullParser xmlPullParser) {
            setAttributes(xmlPullParser);
            parseInjection(xmlPullParser);
        }

        private void parseInjection(XmlPullParser xmlPullParser) {
            parseXmlByTag(xmlPullParser, false, new Consumer() { // from class: com.android.internal.telephony.TelephonyComponentFactory$InjectedComponents$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyComponentFactory.InjectedComponents.this.lambda$parseInjection$3((XmlPullParser) obj);
                }
            }, "components");
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: parseComponents */
        public void lambda$parseInjection$3(XmlPullParser xmlPullParser) {
            parseXmlByTag(xmlPullParser, true, new Consumer() { // from class: com.android.internal.telephony.TelephonyComponentFactory$InjectedComponents$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyComponentFactory.InjectedComponents.this.lambda$parseComponents$4((XmlPullParser) obj);
                }
            }, "component");
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: parseComponent */
        public void lambda$parseComponents$4(XmlPullParser xmlPullParser) {
            try {
                int depth = xmlPullParser.getDepth();
                while (true) {
                    int next = xmlPullParser.next();
                    if (next == 1) {
                        return;
                    }
                    if (next == 3 && xmlPullParser.getDepth() <= depth) {
                        return;
                    }
                    if (next == 4) {
                        this.mComponentNames.add(xmlPullParser.getText());
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                Rlog.e(TelephonyComponentFactory.TAG, "Failed to parse the component.", e);
            }
        }

        private void parseXmlByTag(XmlPullParser xmlPullParser, boolean z, Consumer<XmlPullParser> consumer, String str) {
            try {
                int depth = xmlPullParser.getDepth();
                while (true) {
                    int next = xmlPullParser.next();
                    if (next == 1) {
                        return;
                    }
                    if (next == 3 && xmlPullParser.getDepth() <= depth) {
                        return;
                    }
                    if (next == 2 && str.equals(xmlPullParser.getName())) {
                        consumer.accept(xmlPullParser);
                        if (!z) {
                            return;
                        }
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                String str2 = TelephonyComponentFactory.TAG;
                Rlog.e(str2, "Failed to parse or find tag: " + str, e);
            }
        }

        private void setAttributes(XmlPullParser xmlPullParser) {
            for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                String attributeName = xmlPullParser.getAttributeName(i);
                String attributeValue = xmlPullParser.getAttributeValue(i);
                if ("package".equals(attributeName)) {
                    this.mPackageName = attributeValue;
                } else if ("jar".equals(attributeName)) {
                    this.mJarPath = attributeValue;
                }
            }
        }
    }

    public static TelephonyComponentFactory getInstance() {
        if (sInstance == null) {
            sInstance = new TelephonyComponentFactory();
        }
        return sInstance;
    }

    public void injectTheComponentFactory(XmlResourceParser xmlResourceParser) {
        if (this.mInjectedComponents != null) {
            Rlog.d(TAG, "Already injected.");
        } else if (xmlResourceParser != null) {
            InjectedComponents injectedComponents = new InjectedComponents();
            this.mInjectedComponents = injectedComponents;
            injectedComponents.parseXml(xmlResourceParser);
            this.mInjectedComponents.makeInjectedInstance();
            boolean z = !TextUtils.isEmpty(this.mInjectedComponents.getValidatedPaths());
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Total components injected: ");
            sb.append(z ? this.mInjectedComponents.mComponentNames.size() : 0);
            Rlog.d(str, sb.toString());
        }
    }

    public TelephonyComponentFactory inject(String str) {
        InjectedComponents injectedComponents = this.mInjectedComponents;
        if (injectedComponents != null && injectedComponents.isComponentInjected(str)) {
            return this.mInjectedComponents.mInjectedInstance;
        }
        return sInstance;
    }

    public GsmCdmaCallTracker makeGsmCdmaCallTracker(GsmCdmaPhone gsmCdmaPhone) {
        return new GsmCdmaCallTracker(gsmCdmaPhone);
    }

    public SmsStorageMonitor makeSmsStorageMonitor(Phone phone) {
        return new SmsStorageMonitor(phone);
    }

    public SmsUsageMonitor makeSmsUsageMonitor(Context context) {
        return new SmsUsageMonitor(context);
    }

    public ServiceStateTracker makeServiceStateTracker(GsmCdmaPhone gsmCdmaPhone, CommandsInterface commandsInterface) {
        return new ServiceStateTracker(gsmCdmaPhone, commandsInterface);
    }

    public EmergencyNumberTracker makeEmergencyNumberTracker(Phone phone, CommandsInterface commandsInterface) {
        return new EmergencyNumberTracker(phone, commandsInterface);
    }

    public NitzStateMachine makeNitzStateMachine(GsmCdmaPhone gsmCdmaPhone) {
        return NitzStateMachineImpl.createInstance(gsmCdmaPhone);
    }

    public SimActivationTracker makeSimActivationTracker(Phone phone) {
        return new SimActivationTracker(phone);
    }

    public CarrierSignalAgent makeCarrierSignalAgent(Phone phone) {
        return new CarrierSignalAgent(phone);
    }

    public CarrierActionAgent makeCarrierActionAgent(Phone phone) {
        return new CarrierActionAgent(phone);
    }

    public CarrierResolver makeCarrierResolver(Phone phone) {
        return new CarrierResolver(phone);
    }

    public IccPhoneBookInterfaceManager makeIccPhoneBookInterfaceManager(Phone phone) {
        return new IccPhoneBookInterfaceManager(phone);
    }

    public IccSmsInterfaceManager makeIccSmsInterfaceManager(Phone phone) {
        return new IccSmsInterfaceManager(phone);
    }

    public UiccProfile makeUiccProfile(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, UiccCard uiccCard, Object obj) {
        return new UiccProfile(context, commandsInterface, iccCardStatus, i, uiccCard, obj);
    }

    public EriManager makeEriManager(Phone phone, int i) {
        return new EriManager(phone, i);
    }

    public WspTypeDecoder makeWspTypeDecoder(byte[] bArr) {
        return new WspTypeDecoder(bArr);
    }

    public InboundSmsTracker makeInboundSmsTracker(Context context, byte[] bArr, long j, int i, boolean z, boolean z2, String str, String str2, String str3, boolean z3, int i2, int i3) {
        return new InboundSmsTracker(context, bArr, j, i, z, z2, str, str2, str3, z3, i2, i3);
    }

    public InboundSmsTracker makeInboundSmsTracker(Context context, byte[] bArr, long j, int i, boolean z, String str, String str2, int i2, int i3, int i4, boolean z2, String str3, boolean z3, int i5, int i6) {
        return new InboundSmsTracker(context, bArr, j, i, z, str, str2, i2, i3, i4, z2, str3, z3, i5, i6);
    }

    public InboundSmsTracker makeInboundSmsTracker(Context context, Cursor cursor, boolean z) {
        return new InboundSmsTracker(context, cursor, z);
    }

    public ImsPhoneCallTracker makeImsPhoneCallTracker(ImsPhone imsPhone) {
        return new ImsPhoneCallTracker(imsPhone, new ImsPhoneCallTracker.ConnectorFactory() { // from class: com.android.internal.telephony.TelephonyComponentFactory$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.imsphone.ImsPhoneCallTracker.ConnectorFactory
            public final FeatureConnector create(Context context, int i, String str, FeatureConnector.Listener listener, Executor executor) {
                return ImsManager.getConnector(context, i, str, listener, executor);
            }
        });
    }

    public ImsExternalCallTracker makeImsExternalCallTracker(ImsPhone imsPhone) {
        return new ImsExternalCallTracker(imsPhone, imsPhone.getContext().getMainExecutor());
    }

    public AppSmsManager makeAppSmsManager(Context context) {
        return new AppSmsManager(context);
    }

    public DeviceStateMonitor makeDeviceStateMonitor(Phone phone) {
        return new DeviceStateMonitor(phone);
    }

    public AccessNetworksManager makeAccessNetworksManager(Phone phone, Looper looper) {
        return new AccessNetworksManager(phone, looper);
    }

    public CdmaSubscriptionSourceManager getCdmaSubscriptionSourceManagerInstance(Context context, CommandsInterface commandsInterface, Handler handler, int i, Object obj) {
        return CdmaSubscriptionSourceManager.getInstance(context, commandsInterface, handler, i, obj);
    }

    public LocaleTracker makeLocaleTracker(Phone phone, NitzStateMachine nitzStateMachine, Looper looper) {
        return new LocaleTracker(phone, nitzStateMachine, looper);
    }

    public Phone makePhone(Context context, CommandsInterface commandsInterface, PhoneNotifier phoneNotifier, int i, int i2, TelephonyComponentFactory telephonyComponentFactory) {
        return new GsmCdmaPhone(context, commandsInterface, phoneNotifier, i, i2, telephonyComponentFactory);
    }

    public SubscriptionController initSubscriptionController(Context context) {
        return SubscriptionController.init(context);
    }

    public PhoneSwitcher makePhoneSwitcher(int i, Context context, Looper looper) {
        return PhoneSwitcher.make(i, context, looper);
    }

    public DisplayInfoController makeDisplayInfoController(Phone phone) {
        return new DisplayInfoController(phone);
    }

    public MultiSimSettingController initMultiSimSettingController(Context context, SubscriptionController subscriptionController) {
        return MultiSimSettingController.init(context, subscriptionController);
    }

    public SignalStrengthController makeSignalStrengthController(GsmCdmaPhone gsmCdmaPhone) {
        return new SignalStrengthController(gsmCdmaPhone);
    }

    public SubscriptionInfoUpdater makeSubscriptionInfoUpdater(Looper looper, Context context, SubscriptionController subscriptionController) {
        return new SubscriptionInfoUpdater(looper, context, subscriptionController);
    }

    public LinkBandwidthEstimator makeLinkBandwidthEstimator(Phone phone) {
        return new LinkBandwidthEstimator(phone, this.mTelephonyFacade);
    }

    public DataNetworkController makeDataNetworkController(Phone phone, Looper looper) {
        return new DataNetworkController(phone, looper);
    }

    public DataProfileManager makeDataProfileManager(Phone phone, DataNetworkController dataNetworkController, DataServiceManager dataServiceManager, Looper looper, DataProfileManager.DataProfileManagerCallback dataProfileManagerCallback) {
        return new DataProfileManager(phone, dataNetworkController, dataServiceManager, looper, dataProfileManagerCallback);
    }

    public DataSettingsManager makeDataSettingsManager(Phone phone, DataNetworkController dataNetworkController, Looper looper, DataSettingsManager.DataSettingsManagerCallback dataSettingsManagerCallback) {
        return new DataSettingsManager(phone, dataNetworkController, looper, dataSettingsManagerCallback);
    }
}
