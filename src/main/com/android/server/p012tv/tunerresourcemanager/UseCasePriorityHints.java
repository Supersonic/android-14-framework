package com.android.server.p012tv.tunerresourcemanager;

import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.server.SystemService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.tv.tunerresourcemanager.UseCasePriorityHints */
/* loaded from: classes2.dex */
public class UseCasePriorityHints {
    public static final boolean DEBUG = Log.isLoggable("UseCasePriorityHints", 3);

    /* renamed from: NS */
    public static final String f1157NS = null;
    public SparseArray<int[]> mPriorityHints = new SparseArray<>();
    public Set<Integer> mVendorDefinedUseCase = new HashSet();
    public int mDefaultForeground = 150;
    public int mDefaultBackground = 50;

    public static boolean isPredefinedUseCase(int i) {
        return i == 100 || i == 200 || i == 300 || i == 400 || i == 500;
    }

    public int getForegroundPriority(int i) {
        if (this.mPriorityHints.get(i) != null && this.mPriorityHints.get(i).length == 2) {
            return this.mPriorityHints.get(i)[0];
        }
        return this.mDefaultForeground;
    }

    public int getBackgroundPriority(int i) {
        if (this.mPriorityHints.get(i) != null && this.mPriorityHints.get(i).length == 2) {
            return this.mPriorityHints.get(i)[1];
        }
        return this.mDefaultBackground;
    }

    public boolean isDefinedUseCase(int i) {
        return this.mVendorDefinedUseCase.contains(Integer.valueOf(i)) || isPredefinedUseCase(i);
    }

    public void parse() {
        File file = new File("/vendor/etc/tunerResourceManagerUseCaseConfig.xml");
        if (file.exists()) {
            try {
                parseInternal(new FileInputStream(file));
                return;
            } catch (IOException e) {
                Slog.e("UseCasePriorityHints", "Error reading vendor file: " + file, e);
                return;
            } catch (XmlPullParserException e2) {
                Slog.e("UseCasePriorityHints", "Unable to parse vendor file: " + file, e2);
                return;
            }
        }
        if (DEBUG) {
            Slog.i("UseCasePriorityHints", "no vendor priority configuration available. Using default priority");
        }
        addNewUseCasePriority(100, FrameworkStatsLog.f369xce0f313f, 100);
        addNewUseCasePriority(200, 450, 200);
        addNewUseCasePriority(300, SystemService.PHASE_LOCK_SETTINGS_READY, 300);
        addNewUseCasePriority(FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, 490, FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        addNewUseCasePriority(500, 600, 500);
    }

    @VisibleForTesting
    public void parseInternal(InputStream inputStream) throws IOException, XmlPullParserException {
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        resolvePullParser.nextTag();
        readUseCase(resolvePullParser);
        inputStream.close();
        for (int i = 0; i < this.mPriorityHints.size(); i++) {
            int keyAt = this.mPriorityHints.keyAt(i);
            int[] iArr = this.mPriorityHints.get(keyAt);
            if (DEBUG) {
                Slog.d("UseCasePriorityHints", "{defaultFg=" + this.mDefaultForeground + ", defaultBg=" + this.mDefaultBackground + "}");
                Slog.d("UseCasePriorityHints", "{useCase=" + keyAt + ", fg=" + iArr[0] + ", bg=" + iArr[1] + "}");
            }
        }
    }

    public final void readUseCase(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        typedXmlPullParser.require(2, f1157NS, "config");
        while (typedXmlPullParser.next() != 3) {
            if (typedXmlPullParser.getEventType() == 2) {
                String name = typedXmlPullParser.getName();
                if (name.equals("useCaseDefault")) {
                    this.mDefaultForeground = readAttributeToInt("fgPriority", typedXmlPullParser);
                    this.mDefaultBackground = readAttributeToInt("bgPriority", typedXmlPullParser);
                    typedXmlPullParser.nextTag();
                    typedXmlPullParser.require(3, f1157NS, name);
                } else if (name.equals("useCasePreDefined")) {
                    int formatTypeToNum = formatTypeToNum("type", typedXmlPullParser);
                    if (formatTypeToNum == -1) {
                        Slog.e("UseCasePriorityHints", "Wrong predefined use case name given in the vendor config.");
                    } else {
                        addNewUseCasePriority(formatTypeToNum, readAttributeToInt("fgPriority", typedXmlPullParser), readAttributeToInt("bgPriority", typedXmlPullParser));
                        typedXmlPullParser.nextTag();
                        typedXmlPullParser.require(3, f1157NS, name);
                    }
                } else if (name.equals("useCaseVendor")) {
                    int readAttributeToInt = readAttributeToInt("id", typedXmlPullParser);
                    addNewUseCasePriority(readAttributeToInt, readAttributeToInt("fgPriority", typedXmlPullParser), readAttributeToInt("bgPriority", typedXmlPullParser));
                    this.mVendorDefinedUseCase.add(Integer.valueOf(readAttributeToInt));
                    typedXmlPullParser.nextTag();
                    typedXmlPullParser.require(3, f1157NS, name);
                } else {
                    skip(typedXmlPullParser);
                }
            }
        }
    }

    public final void skip(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        if (typedXmlPullParser.getEventType() != 2) {
            throw new IllegalStateException();
        }
        int i = 1;
        while (i != 0) {
            int next = typedXmlPullParser.next();
            if (next == 2) {
                i++;
            } else if (next == 3) {
                i--;
            }
        }
    }

    public final int readAttributeToInt(String str, TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException {
        return typedXmlPullParser.getAttributeInt((String) null, str);
    }

    public final void addNewUseCasePriority(int i, int i2, int i3) {
        this.mPriorityHints.append(i, new int[]{i2, i3});
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int formatTypeToNum(String str, TypedXmlPullParser typedXmlPullParser) {
        char c;
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str);
        attributeValue.hashCode();
        switch (attributeValue.hashCode()) {
            case -884787515:
                if (attributeValue.equals("USE_CASE_BACKGROUND")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 377959794:
                if (attributeValue.equals("USE_CASE_PLAYBACK")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1222007747:
                if (attributeValue.equals("USE_CASE_LIVE")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1222209876:
                if (attributeValue.equals("USE_CASE_SCAN")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1990900072:
                if (attributeValue.equals("USE_CASE_RECORD")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 100;
            case 1:
                return 300;
            case 2:
                return FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND;
            case 3:
                return 200;
            case 4:
                return 500;
            default:
                return -1;
        }
    }
}
