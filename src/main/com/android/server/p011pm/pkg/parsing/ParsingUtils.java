package com.android.server.p011pm.pkg.parsing;

import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.Parcelling;
import com.android.internal.util.XmlUtils;
import com.android.server.p011pm.pkg.component.ParsedIntentInfo;
import com.android.server.p011pm.pkg.component.ParsedIntentInfoImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.parsing.ParsingUtils */
/* loaded from: classes2.dex */
public class ParsingUtils {
    public static String buildClassName(String str, CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0) {
            return null;
        }
        String charSequence2 = charSequence.toString();
        if (charSequence2.charAt(0) == '.') {
            return str + charSequence2;
        } else if (charSequence2.indexOf(46) < 0) {
            return str + '.' + charSequence2;
        } else {
            return charSequence2;
        }
    }

    public static ParseResult unknownTag(String str, ParsingPackage parsingPackage, XmlResourceParser xmlResourceParser, ParseInput parseInput) throws IOException, XmlPullParserException {
        Slog.w("PackageParsing", "Unknown element under " + str + ": " + xmlResourceParser.getName() + " at " + parsingPackage.getBaseApkPath() + " " + xmlResourceParser.getPositionDescription());
        XmlUtils.skipCurrentTag(xmlResourceParser);
        return parseInput.success((Object) null);
    }

    public static <Interface, Impl extends Interface> List<Interface> createTypedInterfaceList(Parcel parcel, Parcelable.Creator<Impl> creator) {
        int readInt = parcel.readInt();
        if (readInt < 0) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList(readInt);
        while (readInt > 0) {
            arrayList.add(parcel.readTypedObject(creator));
            readInt--;
        }
        return arrayList;
    }

    public static void writeParcelableList(Parcel parcel, List<?> list) {
        if (list == null) {
            parcel.writeInt(-1);
            return;
        }
        int size = list.size();
        parcel.writeInt(size);
        for (int i = 0; i < size; i++) {
            parcel.writeTypedObject((Parcelable) list.get(i), 0);
        }
    }

    /* renamed from: com.android.server.pm.pkg.parsing.ParsingUtils$StringPairListParceler */
    /* loaded from: classes2.dex */
    public static class StringPairListParceler implements Parcelling<List<Pair<String, ParsedIntentInfo>>> {
        public void parcel(List<Pair<String, ParsedIntentInfo>> list, Parcel parcel, int i) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                Pair<String, ParsedIntentInfo> pair = list.get(i2);
                parcel.writeString((String) pair.first);
                parcel.writeParcelable((Parcelable) pair.second, i);
            }
        }

        public List<Pair<String, ParsedIntentInfo>> unparcel(Parcel parcel) {
            int readInt = parcel.readInt();
            if (readInt == -1) {
                return null;
            }
            if (readInt == 0) {
                return new ArrayList(0);
            }
            ArrayList arrayList = new ArrayList(readInt);
            for (int i = 0; i < readInt; i++) {
                arrayList.add(Pair.create(parcel.readString(), (ParsedIntentInfo) parcel.readParcelable(ParsedIntentInfoImpl.class.getClassLoader(), ParsedIntentInfo.class)));
            }
            return arrayList;
        }
    }

    public static ParseResult<Set<String>> parseKnownActivityEmbeddingCerts(TypedArray typedArray, Resources resources, int i, ParseInput parseInput) {
        Set set = null;
        if (!typedArray.hasValue(i)) {
            return parseInput.success((Object) null);
        }
        int resourceId = typedArray.getResourceId(i, 0);
        if (resourceId != 0) {
            if (resources.getResourceTypeName(resourceId).equals("array")) {
                String[] stringArray = resources.getStringArray(resourceId);
                if (stringArray != null) {
                    set = Set.of((Object[]) stringArray);
                }
            } else {
                String string = resources.getString(resourceId);
                if (string != null) {
                    set = Set.of(string);
                }
            }
            if (set == null || set.isEmpty()) {
                return parseInput.error("Defined a knownActivityEmbeddingCerts attribute but the provided resource is null");
            }
            return parseInput.success(set);
        }
        String string2 = typedArray.getString(i);
        if (string2 == null || string2.isEmpty()) {
            return parseInput.error("Defined a knownActivityEmbeddingCerts attribute but the provided string is empty");
        }
        return parseInput.success(Set.of(string2));
    }
}
