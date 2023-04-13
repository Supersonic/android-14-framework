package android.hardware.soundtrigger;

import android.Manifest;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class KeyphraseEnrollmentInfo {
    public static final String ACTION_MANAGE_VOICE_KEYPHRASES = "com.android.intent.action.MANAGE_VOICE_KEYPHRASES";
    public static final String EXTRA_VOICE_KEYPHRASE_ACTION = "com.android.intent.extra.VOICE_KEYPHRASE_ACTION";
    public static final String EXTRA_VOICE_KEYPHRASE_HINT_TEXT = "com.android.intent.extra.VOICE_KEYPHRASE_HINT_TEXT";
    public static final String EXTRA_VOICE_KEYPHRASE_LOCALE = "com.android.intent.extra.VOICE_KEYPHRASE_LOCALE";
    public static final int MANAGE_ACTION_ENROLL = 0;
    public static final int MANAGE_ACTION_RE_ENROLL = 1;
    public static final int MANAGE_ACTION_UN_ENROLL = 2;
    private static final String TAG = "KeyphraseEnrollmentInfo";
    private static final String VOICE_KEYPHRASE_META_DATA = "android.voice_enrollment";
    private final Map<KeyphraseMetadata, String> mKeyphrasePackageMap;
    private final KeyphraseMetadata[] mKeyphrases;
    private String mParseError;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ManageActions {
    }

    public KeyphraseEnrollmentInfo(PackageManager pm) {
        Objects.requireNonNull(pm);
        List<ResolveInfo> ris = pm.queryIntentServices(new Intent(ACTION_MANAGE_VOICE_KEYPHRASES), 65536);
        if (ris == null || ris.isEmpty()) {
            this.mParseError = "No enrollment applications found";
            this.mKeyphrasePackageMap = Collections.emptyMap();
            this.mKeyphrases = null;
            return;
        }
        List<String> parseErrors = new ArrayList<>();
        this.mKeyphrasePackageMap = new HashMap();
        for (ResolveInfo ri : ris) {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(ri.serviceInfo.packageName, 128);
                if ((ai.privateFlags & 8) == 0) {
                    Slog.m90w(TAG, ai.packageName + " is not a privileged system app");
                } else if (!Manifest.C0000permission.MANAGE_VOICE_KEYPHRASES.equals(ai.permission)) {
                    Slog.m90w(TAG, ai.packageName + " does not require MANAGE_VOICE_KEYPHRASES");
                } else {
                    KeyphraseMetadata metadata = getKeyphraseMetadataFromApplicationInfo(pm, ai, parseErrors);
                    if (metadata != null) {
                        this.mKeyphrasePackageMap.put(metadata, ai.packageName);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                String error = "error parsing voice enrollment meta-data for " + ri.serviceInfo.packageName;
                parseErrors.add(error + ": " + e);
                Slog.m89w(TAG, error, e);
            }
        }
        if (this.mKeyphrasePackageMap.isEmpty()) {
            parseErrors.add("No suitable enrollment application found");
            Slog.m90w(TAG, "No suitable enrollment application found");
            this.mKeyphrases = null;
        } else {
            this.mKeyphrases = (KeyphraseMetadata[]) this.mKeyphrasePackageMap.keySet().toArray(new KeyphraseMetadata[0]);
        }
        if (!parseErrors.isEmpty()) {
            this.mParseError = TextUtils.join("\n", parseErrors);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x007e, code lost:
        if (r1 != null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0080, code lost:
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b8, code lost:
        if (0 == 0) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00bb, code lost:
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private KeyphraseMetadata getKeyphraseMetadataFromApplicationInfo(PackageManager pm, ApplicationInfo ai, List<String> parseErrors) {
        XmlResourceParser parser = null;
        String packageName = ai.packageName;
        KeyphraseMetadata keyphraseMetadata = null;
        try {
            try {
                parser = ai.loadXmlMetaData(pm, VOICE_KEYPHRASE_META_DATA);
                if (parser == null) {
                    String error = "No android.voice_enrollment meta-data for " + packageName;
                    parseErrors.add(error);
                    Slog.m90w(TAG, error);
                    return null;
                }
                Resources res = pm.getResourcesForApplication(ai);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!"voice-enrollment-application".equals(nodeName)) {
                    String error2 = "Meta-data does not start with voice-enrollment-application tag for " + packageName;
                    parseErrors.add(error2);
                    Slog.m90w(TAG, error2);
                    if (parser != null) {
                        parser.close();
                    }
                    return null;
                }
                TypedArray array = res.obtainAttributes(attrs, C4057R.styleable.VoiceEnrollmentApplication);
                keyphraseMetadata = getKeyphraseFromTypedArray(array, packageName, parseErrors);
                array.recycle();
            } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException e) {
                String error3 = "Error parsing keyphrase enrollment meta-data for " + packageName;
                parseErrors.add(error3 + ": " + e);
                Slog.m89w(TAG, error3, e);
            }
        } finally {
            if (0 != 0) {
                parser.close();
            }
        }
    }

    private KeyphraseMetadata getKeyphraseFromTypedArray(TypedArray array, String packageName, List<String> parseErrors) {
        int searchKeyphraseId = array.getInt(0, -1);
        if (searchKeyphraseId <= 0) {
            String error = "No valid searchKeyphraseId specified in meta-data for " + packageName;
            parseErrors.add(error);
            Slog.m90w(TAG, error);
            return null;
        }
        String searchKeyphrase = array.getString(1);
        if (searchKeyphrase == null) {
            String error2 = "No valid searchKeyphrase specified in meta-data for " + packageName;
            parseErrors.add(error2);
            Slog.m90w(TAG, error2);
            return null;
        }
        String searchKeyphraseSupportedLocales = array.getString(2);
        if (searchKeyphraseSupportedLocales == null) {
            String error3 = "No valid searchKeyphraseSupportedLocales specified in meta-data for " + packageName;
            parseErrors.add(error3);
            Slog.m90w(TAG, error3);
            return null;
        }
        ArraySet<Locale> locales = new ArraySet<>();
        if (!TextUtils.isEmpty(searchKeyphraseSupportedLocales)) {
            try {
                String[] supportedLocalesDelimited = searchKeyphraseSupportedLocales.split(",");
                for (String s : supportedLocalesDelimited) {
                    locales.add(Locale.forLanguageTag(s));
                }
            } catch (Exception e) {
                String error4 = "Error reading searchKeyphraseSupportedLocales from meta-data for " + packageName;
                parseErrors.add(error4);
                Slog.m90w(TAG, error4);
                return null;
            }
        }
        int recognitionModes = array.getInt(3, -1);
        if (recognitionModes < 0) {
            String error5 = "No valid searchKeyphraseRecognitionFlags specified in meta-data for " + packageName;
            parseErrors.add(error5);
            Slog.m90w(TAG, error5);
            return null;
        }
        return new KeyphraseMetadata(searchKeyphraseId, searchKeyphrase, locales, recognitionModes);
    }

    public String getParseError() {
        return this.mParseError;
    }

    public Collection<KeyphraseMetadata> listKeyphraseMetadata() {
        return Arrays.asList(this.mKeyphrases);
    }

    public Intent getManageKeyphraseIntent(int action, String keyphrase, Locale locale) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        Map<KeyphraseMetadata, String> map = this.mKeyphrasePackageMap;
        if (map == null || map.isEmpty()) {
            Slog.m90w(TAG, "No enrollment application exists");
            return null;
        }
        KeyphraseMetadata keyphraseMetadata = getKeyphraseMetadata(keyphrase, locale);
        if (keyphraseMetadata != null) {
            return new Intent(ACTION_MANAGE_VOICE_KEYPHRASES).setPackage(this.mKeyphrasePackageMap.get(keyphraseMetadata)).putExtra(EXTRA_VOICE_KEYPHRASE_HINT_TEXT, keyphrase).putExtra(EXTRA_VOICE_KEYPHRASE_LOCALE, locale.toLanguageTag()).putExtra(EXTRA_VOICE_KEYPHRASE_ACTION, action);
        }
        return null;
    }

    public KeyphraseMetadata getKeyphraseMetadata(String keyphrase, Locale locale) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        KeyphraseMetadata[] keyphraseMetadataArr = this.mKeyphrases;
        if (keyphraseMetadataArr != null && keyphraseMetadataArr.length > 0) {
            for (KeyphraseMetadata keyphraseMetadata : keyphraseMetadataArr) {
                if (keyphraseMetadata.supportsPhrase(keyphrase) && keyphraseMetadata.supportsLocale(locale)) {
                    return keyphraseMetadata;
                }
            }
        }
        Slog.m90w(TAG, "No enrollment application supports the given keyphrase/locale: '" + keyphrase + "'/" + locale);
        return null;
    }

    public String toString() {
        return "KeyphraseEnrollmentInfo [KeyphrasePackageMap=" + this.mKeyphrasePackageMap.toString() + ", ParseError=" + this.mParseError + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
