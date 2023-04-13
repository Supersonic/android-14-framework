package com.android.server.hdmi;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.hdmi.HdmiControlManager;
import android.net.INetd;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ConcurrentUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class HdmiCecConfig {
    public final Context mContext;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final ArrayMap<Setting, ArrayMap<SettingChangeListener, Executor>> mSettingChangeListeners;
    public LinkedHashMap<String, Setting> mSettings;
    public final StorageAdapter mStorageAdapter;

    /* loaded from: classes.dex */
    public interface SettingChangeListener {
        void onChange(@HdmiControlManager.SettingName String str);
    }

    /* loaded from: classes.dex */
    public static class VerificationException extends RuntimeException {
        public VerificationException(String str) {
            super(str);
        }
    }

    /* loaded from: classes.dex */
    public static class StorageAdapter {
        public final Context mContext;
        public final SharedPreferences mSharedPrefs;

        public StorageAdapter(Context context) {
            this.mContext = context;
            this.mSharedPrefs = context.createDeviceProtectedStorageContext().getSharedPreferences(new File(new File(Environment.getDataSystemDirectory(), "shared_prefs"), "cec_config.xml"), 0);
        }

        public String retrieveSystemProperty(String str, String str2) {
            return SystemProperties.get(str, str2);
        }

        public void storeSystemProperty(String str, String str2) {
            SystemProperties.set(str, str2);
        }

        public String retrieveGlobalSetting(String str, String str2) {
            String string = Settings.Global.getString(this.mContext.getContentResolver(), str);
            return string != null ? string : str2;
        }

        public void storeGlobalSetting(String str, String str2) {
            Settings.Global.putString(this.mContext.getContentResolver(), str, str2);
        }

        public String retrieveSharedPref(String str, String str2) {
            return this.mSharedPrefs.getString(str, str2);
        }

        public void storeSharedPref(String str, String str2) {
            this.mSharedPrefs.edit().putString(str, str2).apply();
        }
    }

    /* loaded from: classes.dex */
    public class Value {
        public final Integer mIntValue;
        public final String mStringValue;

        public Value(String str) {
            this.mStringValue = str;
            this.mIntValue = null;
        }

        public Value(Integer num) {
            this.mStringValue = null;
            this.mIntValue = num;
        }

        public String getStringValue() {
            return this.mStringValue;
        }

        public Integer getIntValue() {
            return this.mIntValue;
        }
    }

    /* loaded from: classes.dex */
    public class Setting {
        public final Context mContext;
        @HdmiControlManager.SettingName
        public final String mName;
        public final boolean mUserConfigurable;
        public Value mDefaultValue = null;
        public List<Value> mAllowedValues = new ArrayList();

        public Setting(Context context, @HdmiControlManager.SettingName String str, int i) {
            this.mContext = context;
            this.mName = str;
            this.mUserConfigurable = context.getResources().getBoolean(i);
        }

        @HdmiControlManager.SettingName
        public String getName() {
            return this.mName;
        }

        public String getValueType() {
            return getDefaultValue().getStringValue() != null ? "string" : "int";
        }

        public Value getDefaultValue() {
            Value value = this.mDefaultValue;
            if (value != null) {
                return value;
            }
            throw new VerificationException("Invalid CEC setup for '" + getName() + "' setting. Setting has no default value.");
        }

        public boolean getUserConfigurable() {
            return this.mUserConfigurable;
        }

        public final void registerValue(Value value, int i, int i2) {
            if (this.mContext.getResources().getBoolean(i)) {
                this.mAllowedValues.add(value);
                if (this.mContext.getResources().getBoolean(i2)) {
                    if (this.mDefaultValue != null) {
                        Slog.e("HdmiCecConfig", "Failed to set '" + value + "' as a default for '" + getName() + "': Setting already has a default ('" + this.mDefaultValue + "').");
                        return;
                    }
                    this.mDefaultValue = value;
                }
            }
        }

        public void registerValue(String str, int i, int i2) {
            registerValue(new Value(str), i, i2);
        }

        public void registerValue(int i, int i2, int i3) {
            registerValue(new Value(Integer.valueOf(i)), i2, i3);
        }

        public List<Value> getAllowedValues() {
            return this.mAllowedValues;
        }
    }

    @VisibleForTesting
    public HdmiCecConfig(Context context, StorageAdapter storageAdapter) {
        this.mLock = new Object();
        this.mSettingChangeListeners = new ArrayMap<>();
        this.mSettings = new LinkedHashMap<>();
        this.mContext = context;
        this.mStorageAdapter = storageAdapter;
        Setting registerSetting = registerSetting("hdmi_cec_enabled", 17891414);
        registerSetting.registerValue(1, 17891412, 17891413);
        registerSetting.registerValue(0, 17891410, 17891411);
        Setting registerSetting2 = registerSetting("hdmi_cec_version", 17891419);
        registerSetting2.registerValue(5, 17891415, 17891416);
        registerSetting2.registerValue(6, 17891417, 17891418);
        Setting registerSetting3 = registerSetting("routing_control", 17891549);
        registerSetting3.registerValue(1, 17891547, 17891548);
        registerSetting3.registerValue(0, 17891545, 17891546);
        Setting registerSetting4 = registerSetting("soundbar_mode", 17891559);
        registerSetting4.registerValue(1, 17891557, 17891558);
        registerSetting4.registerValue(0, 17891555, 17891556);
        Setting registerSetting5 = registerSetting("power_control_mode", 17891428);
        registerSetting5.registerValue("to_tv", 17891426, 17891427);
        registerSetting5.registerValue(INetd.IF_FLAG_BROADCAST, 17891420, 17891421);
        registerSetting5.registerValue("none", 17891422, 17891423);
        registerSetting5.registerValue("to_tv_and_audio_system", 17891424, 17891425);
        Setting registerSetting6 = registerSetting("power_state_change_on_active_source_lost", 17891433);
        registerSetting6.registerValue("none", 17891429, 17891430);
        registerSetting6.registerValue("standby_now", 17891431, 17891432);
        Setting registerSetting7 = registerSetting("system_audio_control", 17891564);
        registerSetting7.registerValue(1, 17891562, 17891563);
        registerSetting7.registerValue(0, 17891560, 17891561);
        Setting registerSetting8 = registerSetting("system_audio_mode_muting", 17891569);
        registerSetting8.registerValue(1, 17891567, 17891568);
        registerSetting8.registerValue(0, 17891565, 17891566);
        Setting registerSetting9 = registerSetting("volume_control_enabled", 17891584);
        registerSetting9.registerValue(1, 17891582, 17891583);
        registerSetting9.registerValue(0, 17891580, 17891581);
        Setting registerSetting10 = registerSetting("tv_wake_on_one_touch_play", 17891579);
        registerSetting10.registerValue(1, 17891577, 17891578);
        registerSetting10.registerValue(0, 17891575, 17891576);
        Setting registerSetting11 = registerSetting("tv_send_standby_on_sleep", 17891574);
        registerSetting11.registerValue(1, 17891572, 17891573);
        registerSetting11.registerValue(0, 17891570, 17891571);
        Setting registerSetting12 = registerSetting("set_menu_language", 17891554);
        registerSetting12.registerValue(1, 17891552, 17891553);
        registerSetting12.registerValue(0, 17891550, 17891551);
        Setting registerSetting13 = registerSetting("rc_profile_tv", 17891544);
        registerSetting13.registerValue(0, 17891536, 17891537);
        registerSetting13.registerValue(2, 17891538, 17891539);
        registerSetting13.registerValue(6, 17891542, 17891543);
        registerSetting13.registerValue(10, 17891540, 17891541);
        registerSetting13.registerValue(14, 17891534, 17891535);
        Setting registerSetting14 = registerSetting("rc_profile_source_handles_root_menu", 17891523);
        registerSetting14.registerValue(1, 17891519, 17891520);
        registerSetting14.registerValue(0, 17891521, 17891522);
        Setting registerSetting15 = registerSetting("rc_profile_source_handles_setup_menu", 17891528);
        registerSetting15.registerValue(1, 17891524, 17891525);
        registerSetting15.registerValue(0, 17891526, 17891527);
        Setting registerSetting16 = registerSetting("rc_profile_source_handles_contents_menu", 17891513);
        registerSetting16.registerValue(1, 17891509, 17891510);
        registerSetting16.registerValue(0, 17891511, 17891512);
        Setting registerSetting17 = registerSetting("rc_profile_source_handles_top_menu", 17891533);
        registerSetting17.registerValue(1, 17891529, 17891530);
        registerSetting17.registerValue(0, 17891531, 17891532);
        Setting registerSetting18 = registerSetting("rc_profile_source_handles_media_context_sensitive_menu", 17891518);
        registerSetting18.registerValue(1, 17891514, 17891515);
        registerSetting18.registerValue(0, 17891516, 17891517);
        Setting registerSetting19 = registerSetting("query_sad_lpcm", 17891473);
        registerSetting19.registerValue(1, 17891471, 17891472);
        registerSetting19.registerValue(0, 17891469, 17891470);
        Setting registerSetting20 = registerSetting("query_sad_dd", 17891448);
        registerSetting20.registerValue(1, 17891446, 17891447);
        registerSetting20.registerValue(0, 17891444, 17891445);
        Setting registerSetting21 = registerSetting("query_sad_mpeg1", 17891488);
        registerSetting21.registerValue(1, 17891486, 17891487);
        registerSetting21.registerValue(0, 17891484, 17891485);
        Setting registerSetting22 = registerSetting("query_sad_mp3", 17891483);
        registerSetting22.registerValue(1, 17891481, 17891482);
        registerSetting22.registerValue(0, 17891479, 17891480);
        Setting registerSetting23 = registerSetting("query_sad_mpeg2", 17891493);
        registerSetting23.registerValue(1, 17891491, 17891492);
        registerSetting23.registerValue(0, 17891489, 17891490);
        Setting registerSetting24 = registerSetting("query_sad_aac", 17891438);
        registerSetting24.registerValue(1, 17891436, 17891437);
        registerSetting24.registerValue(0, 17891434, 17891435);
        Setting registerSetting25 = registerSetting("query_sad_dts", 17891463);
        registerSetting25.registerValue(1, 17891461, 17891462);
        registerSetting25.registerValue(0, 17891459, 17891460);
        Setting registerSetting26 = registerSetting("query_sad_atrac", 17891443);
        registerSetting26.registerValue(1, 17891441, 17891442);
        registerSetting26.registerValue(0, 17891439, 17891440);
        Setting registerSetting27 = registerSetting("query_sad_onebitaudio", 17891498);
        registerSetting27.registerValue(1, 17891496, 17891497);
        registerSetting27.registerValue(0, 17891494, 17891495);
        Setting registerSetting28 = registerSetting("query_sad_ddp", 17891453);
        registerSetting28.registerValue(1, 17891451, 17891452);
        registerSetting28.registerValue(0, 17891449, 17891450);
        Setting registerSetting29 = registerSetting("query_sad_dtshd", 17891468);
        registerSetting29.registerValue(1, 17891466, 17891467);
        registerSetting29.registerValue(0, 17891464, 17891465);
        Setting registerSetting30 = registerSetting("query_sad_truehd", 17891503);
        registerSetting30.registerValue(1, 17891501, 17891502);
        registerSetting30.registerValue(0, 17891499, 17891500);
        Setting registerSetting31 = registerSetting("query_sad_dst", 17891458);
        registerSetting31.registerValue(1, 17891456, 17891457);
        registerSetting31.registerValue(0, 17891454, 17891455);
        Setting registerSetting32 = registerSetting("query_sad_wmapro", 17891508);
        registerSetting32.registerValue(1, 17891506, 17891507);
        registerSetting32.registerValue(0, 17891504, 17891505);
        Setting registerSetting33 = registerSetting("query_sad_max", 17891478);
        registerSetting33.registerValue(1, 17891476, 17891477);
        registerSetting33.registerValue(0, 17891474, 17891475);
        Setting registerSetting34 = registerSetting("earc_enabled", 17891636);
        registerSetting34.registerValue(1, 17891639, 17891640);
        registerSetting34.registerValue(0, 17891637, 17891638);
        verifySettings();
    }

    public HdmiCecConfig(Context context) {
        this(context, new StorageAdapter(context));
    }

    public final Setting registerSetting(@HdmiControlManager.SettingName String str, int i) {
        Setting setting = new Setting(this.mContext, str, i);
        this.mSettings.put(str, setting);
        return setting;
    }

    public final void verifySettings() {
        for (Setting setting : this.mSettings.values()) {
            setting.getDefaultValue();
            getStorage(setting);
            getStorageKey(setting);
        }
    }

    public final Setting getSetting(String str) {
        if (this.mSettings.containsKey(str)) {
            return this.mSettings.get(str);
        }
        return null;
    }

    public final int getStorage(Setting setting) {
        String name = setting.getName();
        name.hashCode();
        char c = 65535;
        switch (name.hashCode()) {
            case -2072577869:
                if (name.equals("hdmi_cec_version")) {
                    c = 0;
                    break;
                }
                break;
            case -1788790343:
                if (name.equals("system_audio_mode_muting")) {
                    c = 1;
                    break;
                }
                break;
            case -1618836197:
                if (name.equals("set_menu_language")) {
                    c = 2;
                    break;
                }
                break;
            case -1275604441:
                if (name.equals("rc_profile_source_handles_media_context_sensitive_menu")) {
                    c = 3;
                    break;
                }
                break;
            case -1253675651:
                if (name.equals("rc_profile_source_handles_top_menu")) {
                    c = 4;
                    break;
                }
                break;
            case -1188289112:
                if (name.equals("rc_profile_source_handles_root_menu")) {
                    c = 5;
                    break;
                }
                break;
            case -1157203295:
                if (name.equals("query_sad_atrac")) {
                    c = 6;
                    break;
                }
                break;
            case -1154431553:
                if (name.equals("query_sad_dtshd")) {
                    c = 7;
                    break;
                }
                break;
            case -1146252564:
                if (name.equals("query_sad_mpeg1")) {
                    c = '\b';
                    break;
                }
                break;
            case -1146252563:
                if (name.equals("query_sad_mpeg2")) {
                    c = '\t';
                    break;
                }
                break;
            case -1081575217:
                if (name.equals("earc_enabled")) {
                    c = '\n';
                    break;
                }
                break;
            case -971363478:
                if (name.equals("query_sad_truehd")) {
                    c = 11;
                    break;
                }
                break;
            case -910325648:
                if (name.equals("rc_profile_source_handles_contents_menu")) {
                    c = '\f';
                    break;
                }
                break;
            case -890678558:
                if (name.equals("query_sad_wmapro")) {
                    c = '\r';
                    break;
                }
                break;
            case -412334364:
                if (name.equals("routing_control")) {
                    c = 14;
                    break;
                }
                break;
            case -314100402:
                if (name.equals("query_sad_lpcm")) {
                    c = 15;
                    break;
                }
                break;
            case -293445547:
                if (name.equals("rc_profile_source_handles_setup_menu")) {
                    c = 16;
                    break;
                }
                break;
            case -219770232:
                if (name.equals("power_state_change_on_active_source_lost")) {
                    c = 17;
                    break;
                }
                break;
            case -25374657:
                if (name.equals("power_control_mode")) {
                    c = 18;
                    break;
                }
                break;
            case 18371678:
                if (name.equals("soundbar_mode")) {
                    c = 19;
                    break;
                }
                break;
            case 73184058:
                if (name.equals("volume_control_enabled")) {
                    c = 20;
                    break;
                }
                break;
            case 261187356:
                if (name.equals("hdmi_cec_enabled")) {
                    c = 21;
                    break;
                }
                break;
            case 791759782:
                if (name.equals("rc_profile_tv")) {
                    c = 22;
                    break;
                }
                break;
            case 799280879:
                if (name.equals("query_sad_onebitaudio")) {
                    c = 23;
                    break;
                }
                break;
            case 1577324768:
                if (name.equals("query_sad_dd")) {
                    c = 24;
                    break;
                }
                break;
            case 1629183631:
                if (name.equals("tv_wake_on_one_touch_play")) {
                    c = 25;
                    break;
                }
                break;
            case 1652424675:
                if (name.equals("query_sad_aac")) {
                    c = 26;
                    break;
                }
                break;
            case 1652427664:
                if (name.equals("query_sad_ddp")) {
                    c = 27;
                    break;
                }
                break;
            case 1652428133:
                if (name.equals("query_sad_dst")) {
                    c = 28;
                    break;
                }
                break;
            case 1652428163:
                if (name.equals("query_sad_dts")) {
                    c = 29;
                    break;
                }
                break;
            case 1652436228:
                if (name.equals("query_sad_max")) {
                    c = 30;
                    break;
                }
                break;
            case 1652436624:
                if (name.equals("query_sad_mp3")) {
                    c = 31;
                    break;
                }
                break;
            case 2055627683:
                if (name.equals("tv_send_standby_on_sleep")) {
                    c = ' ';
                    break;
                }
                break;
            case 2118236132:
                if (name.equals("system_audio_control")) {
                    c = '!';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
            case '\t':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case ' ':
            case '!':
                return 2;
            default:
                throw new VerificationException("Invalid CEC setting '" + setting.getName() + "' storage.");
        }
    }

    public final String getStorageKey(Setting setting) {
        String name = setting.getName();
        name.hashCode();
        char c = 65535;
        switch (name.hashCode()) {
            case -2072577869:
                if (name.equals("hdmi_cec_version")) {
                    c = 0;
                    break;
                }
                break;
            case -1788790343:
                if (name.equals("system_audio_mode_muting")) {
                    c = 1;
                    break;
                }
                break;
            case -1618836197:
                if (name.equals("set_menu_language")) {
                    c = 2;
                    break;
                }
                break;
            case -1275604441:
                if (name.equals("rc_profile_source_handles_media_context_sensitive_menu")) {
                    c = 3;
                    break;
                }
                break;
            case -1253675651:
                if (name.equals("rc_profile_source_handles_top_menu")) {
                    c = 4;
                    break;
                }
                break;
            case -1188289112:
                if (name.equals("rc_profile_source_handles_root_menu")) {
                    c = 5;
                    break;
                }
                break;
            case -1157203295:
                if (name.equals("query_sad_atrac")) {
                    c = 6;
                    break;
                }
                break;
            case -1154431553:
                if (name.equals("query_sad_dtshd")) {
                    c = 7;
                    break;
                }
                break;
            case -1146252564:
                if (name.equals("query_sad_mpeg1")) {
                    c = '\b';
                    break;
                }
                break;
            case -1146252563:
                if (name.equals("query_sad_mpeg2")) {
                    c = '\t';
                    break;
                }
                break;
            case -1081575217:
                if (name.equals("earc_enabled")) {
                    c = '\n';
                    break;
                }
                break;
            case -971363478:
                if (name.equals("query_sad_truehd")) {
                    c = 11;
                    break;
                }
                break;
            case -910325648:
                if (name.equals("rc_profile_source_handles_contents_menu")) {
                    c = '\f';
                    break;
                }
                break;
            case -890678558:
                if (name.equals("query_sad_wmapro")) {
                    c = '\r';
                    break;
                }
                break;
            case -412334364:
                if (name.equals("routing_control")) {
                    c = 14;
                    break;
                }
                break;
            case -314100402:
                if (name.equals("query_sad_lpcm")) {
                    c = 15;
                    break;
                }
                break;
            case -293445547:
                if (name.equals("rc_profile_source_handles_setup_menu")) {
                    c = 16;
                    break;
                }
                break;
            case -219770232:
                if (name.equals("power_state_change_on_active_source_lost")) {
                    c = 17;
                    break;
                }
                break;
            case -25374657:
                if (name.equals("power_control_mode")) {
                    c = 18;
                    break;
                }
                break;
            case 18371678:
                if (name.equals("soundbar_mode")) {
                    c = 19;
                    break;
                }
                break;
            case 73184058:
                if (name.equals("volume_control_enabled")) {
                    c = 20;
                    break;
                }
                break;
            case 261187356:
                if (name.equals("hdmi_cec_enabled")) {
                    c = 21;
                    break;
                }
                break;
            case 791759782:
                if (name.equals("rc_profile_tv")) {
                    c = 22;
                    break;
                }
                break;
            case 799280879:
                if (name.equals("query_sad_onebitaudio")) {
                    c = 23;
                    break;
                }
                break;
            case 1577324768:
                if (name.equals("query_sad_dd")) {
                    c = 24;
                    break;
                }
                break;
            case 1629183631:
                if (name.equals("tv_wake_on_one_touch_play")) {
                    c = 25;
                    break;
                }
                break;
            case 1652424675:
                if (name.equals("query_sad_aac")) {
                    c = 26;
                    break;
                }
                break;
            case 1652427664:
                if (name.equals("query_sad_ddp")) {
                    c = 27;
                    break;
                }
                break;
            case 1652428133:
                if (name.equals("query_sad_dst")) {
                    c = 28;
                    break;
                }
                break;
            case 1652428163:
                if (name.equals("query_sad_dts")) {
                    c = 29;
                    break;
                }
                break;
            case 1652436228:
                if (name.equals("query_sad_max")) {
                    c = 30;
                    break;
                }
                break;
            case 1652436624:
                if (name.equals("query_sad_mp3")) {
                    c = 31;
                    break;
                }
                break;
            case 2055627683:
                if (name.equals("tv_send_standby_on_sleep")) {
                    c = ' ';
                    break;
                }
                break;
            case 2118236132:
                if (name.equals("system_audio_control")) {
                    c = '!';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return setting.getName();
            case 1:
                return setting.getName();
            case 2:
                return setting.getName();
            case 3:
                return setting.getName();
            case 4:
                return setting.getName();
            case 5:
                return setting.getName();
            case 6:
                return setting.getName();
            case 7:
                return setting.getName();
            case '\b':
                return setting.getName();
            case '\t':
                return setting.getName();
            case '\n':
                return setting.getName();
            case 11:
                return setting.getName();
            case '\f':
                return setting.getName();
            case '\r':
                return setting.getName();
            case 14:
                return setting.getName();
            case 15:
                return setting.getName();
            case 16:
                return setting.getName();
            case 17:
                return setting.getName();
            case 18:
                return setting.getName();
            case 19:
                return setting.getName();
            case 20:
                return setting.getName();
            case 21:
                return setting.getName();
            case 22:
                return setting.getName();
            case 23:
                return setting.getName();
            case 24:
                return setting.getName();
            case 25:
                return setting.getName();
            case 26:
                return setting.getName();
            case 27:
                return setting.getName();
            case 28:
                return setting.getName();
            case 29:
                return setting.getName();
            case 30:
                return setting.getName();
            case 31:
                return setting.getName();
            case ' ':
                return setting.getName();
            case '!':
                return setting.getName();
            default:
                throw new VerificationException("Invalid CEC setting '" + setting.getName() + "' storage key.");
        }
    }

    public String retrieveValue(Setting setting, String str) {
        int storage = getStorage(setting);
        String storageKey = getStorageKey(setting);
        if (storage == 0) {
            HdmiLogger.debug("Reading '" + storageKey + "' sysprop.", new Object[0]);
            return this.mStorageAdapter.retrieveSystemProperty(storageKey, str);
        } else if (storage == 1) {
            HdmiLogger.debug("Reading '" + storageKey + "' global setting.", new Object[0]);
            return this.mStorageAdapter.retrieveGlobalSetting(storageKey, str);
        } else if (storage == 2) {
            HdmiLogger.debug("Reading '" + storageKey + "' shared preference.", new Object[0]);
            return this.mStorageAdapter.retrieveSharedPref(storageKey, str);
        } else {
            return null;
        }
    }

    public void storeValue(Setting setting, String str) {
        int storage = getStorage(setting);
        String storageKey = getStorageKey(setting);
        if (storage == 0) {
            HdmiLogger.debug("Setting '" + storageKey + "' sysprop.", new Object[0]);
            this.mStorageAdapter.storeSystemProperty(storageKey, str);
        } else if (storage == 1) {
            HdmiLogger.debug("Setting '" + storageKey + "' global setting.", new Object[0]);
            this.mStorageAdapter.storeGlobalSetting(storageKey, str);
        } else if (storage == 2) {
            HdmiLogger.debug("Setting '" + storageKey + "' shared pref.", new Object[0]);
            this.mStorageAdapter.storeSharedPref(storageKey, str);
            notifySettingChanged(setting);
        }
    }

    public void notifySettingChanged(final Setting setting) {
        synchronized (this.mLock) {
            ArrayMap<SettingChangeListener, Executor> arrayMap = this.mSettingChangeListeners.get(setting);
            if (arrayMap == null) {
                return;
            }
            for (Map.Entry<SettingChangeListener, Executor> entry : arrayMap.entrySet()) {
                final SettingChangeListener key = entry.getKey();
                entry.getValue().execute(new Runnable() { // from class: com.android.server.hdmi.HdmiCecConfig.1
                    @Override // java.lang.Runnable
                    public void run() {
                        key.onChange(setting.getName());
                    }
                });
            }
        }
    }

    public void registerChangeListener(@HdmiControlManager.SettingName String str, SettingChangeListener settingChangeListener) {
        registerChangeListener(str, settingChangeListener, ConcurrentUtils.DIRECT_EXECUTOR);
    }

    public void registerChangeListener(@HdmiControlManager.SettingName String str, SettingChangeListener settingChangeListener, Executor executor) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        }
        int storage = getStorage(setting);
        if (storage != 1 && storage != 2) {
            throw new IllegalArgumentException("Change listeners for setting '" + str + "' not supported.");
        }
        synchronized (this.mLock) {
            if (!this.mSettingChangeListeners.containsKey(setting)) {
                this.mSettingChangeListeners.put(setting, new ArrayMap<>());
            }
            this.mSettingChangeListeners.get(setting).put(settingChangeListener, executor);
        }
    }

    public void removeChangeListener(@HdmiControlManager.SettingName String str, SettingChangeListener settingChangeListener) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        }
        synchronized (this.mLock) {
            if (this.mSettingChangeListeners.containsKey(setting)) {
                ArrayMap<SettingChangeListener, Executor> arrayMap = this.mSettingChangeListeners.get(setting);
                arrayMap.remove(settingChangeListener);
                if (arrayMap.isEmpty()) {
                    this.mSettingChangeListeners.remove(setting);
                }
            }
        }
    }

    @HdmiControlManager.SettingName
    public List<String> getAllSettings() {
        return new ArrayList(this.mSettings.keySet());
    }

    @HdmiControlManager.SettingName
    public List<String> getUserSettings() {
        ArrayList arrayList = new ArrayList();
        for (Setting setting : this.mSettings.values()) {
            if (setting.getUserConfigurable()) {
                arrayList.add(setting.getName());
            }
        }
        return arrayList;
    }

    public boolean isStringValueType(@HdmiControlManager.SettingName String str) {
        if (getSetting(str) == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        }
        return getSetting(str).getValueType().equals("string");
    }

    public boolean isIntValueType(@HdmiControlManager.SettingName String str) {
        if (getSetting(str) == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        }
        return getSetting(str).getValueType().equals("int");
    }

    public List<String> getAllowedStringValues(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("string")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else {
            ArrayList arrayList = new ArrayList();
            for (Value value : setting.getAllowedValues()) {
                arrayList.add(value.getStringValue());
            }
            return arrayList;
        }
    }

    public List<Integer> getAllowedIntValues(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("int")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else {
            ArrayList arrayList = new ArrayList();
            for (Value value : setting.getAllowedValues()) {
                arrayList.add(value.getIntValue());
            }
            return arrayList;
        }
    }

    public String getDefaultStringValue(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("string")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else {
            return getSetting(str).getDefaultValue().getStringValue();
        }
    }

    public int getDefaultIntValue(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("int")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else {
            return getSetting(str).getDefaultValue().getIntValue().intValue();
        }
    }

    public String getStringValue(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("string")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else {
            HdmiLogger.debug("Getting CEC setting value '" + str + "'.", new Object[0]);
            return retrieveValue(setting, setting.getDefaultValue().getStringValue());
        }
    }

    public int getIntValue(@HdmiControlManager.SettingName String str) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getValueType().equals("int")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a int-type setting.");
        } else {
            HdmiLogger.debug("Getting CEC setting value '" + str + "'.", new Object[0]);
            return Integer.parseInt(retrieveValue(setting, Integer.toString(setting.getDefaultValue().getIntValue().intValue())));
        }
    }

    public void setStringValue(@HdmiControlManager.SettingName String str, String str2) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getUserConfigurable()) {
            throw new IllegalArgumentException("Updating CEC setting '" + str + "' prohibited.");
        } else if (!setting.getValueType().equals("string")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a string-type setting.");
        } else if (!getAllowedStringValues(str).contains(str2)) {
            throw new IllegalArgumentException("Invalid CEC setting '" + str + "' value: '" + str2 + "'.");
        } else {
            HdmiLogger.debug("Updating CEC setting '" + str + "' to '" + str2 + "'.", new Object[0]);
            storeValue(setting, str2);
        }
    }

    public void setIntValue(@HdmiControlManager.SettingName String str, int i) {
        Setting setting = getSetting(str);
        if (setting == null) {
            throw new IllegalArgumentException("Setting '" + str + "' does not exist.");
        } else if (!setting.getUserConfigurable()) {
            throw new IllegalArgumentException("Updating CEC setting '" + str + "' prohibited.");
        } else if (!setting.getValueType().equals("int")) {
            throw new IllegalArgumentException("Setting '" + str + "' is not a int-type setting.");
        } else if (!getAllowedIntValues(str).contains(Integer.valueOf(i))) {
            throw new IllegalArgumentException("Invalid CEC setting '" + str + "' value: '" + i + "'.");
        } else {
            HdmiLogger.debug("Updating CEC setting '" + str + "' to '" + i + "'.", new Object[0]);
            storeValue(setting, Integer.toString(i));
        }
    }
}
