package android.content;

import android.content.p001pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.StrictMode;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.textclassifier.TextLinks;
import com.android.internal.transition.EpicenterTranslateClipReveal;
import com.android.internal.util.ArrayUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class ClipData implements Parcelable {
    final ClipDescription mClipDescription;
    final Bitmap mIcon;
    final ArrayList<Item> mItems;
    private boolean mParcelItemActivityInfos;
    static final String[] MIMETYPES_TEXT_PLAIN = {"text/plain"};
    static final String[] MIMETYPES_TEXT_HTML = {"text/html"};
    static final String[] MIMETYPES_TEXT_URILIST = {ClipDescription.MIMETYPE_TEXT_URILIST};
    static final String[] MIMETYPES_TEXT_INTENT = {ClipDescription.MIMETYPE_TEXT_INTENT};
    public static final Parcelable.Creator<ClipData> CREATOR = new Parcelable.Creator<ClipData>() { // from class: android.content.ClipData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClipData createFromParcel(Parcel source) {
            return new ClipData(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClipData[] newArray(int size) {
            return new ClipData[size];
        }
    };

    /* loaded from: classes.dex */
    public static class Item {
        private ActivityInfo mActivityInfo;
        final String mHtmlText;
        final Intent mIntent;
        final CharSequence mText;
        private TextLinks mTextLinks;
        Uri mUri;

        public Item(Item other) {
            this.mText = other.mText;
            this.mHtmlText = other.mHtmlText;
            this.mIntent = other.mIntent;
            this.mUri = other.mUri;
            this.mActivityInfo = other.mActivityInfo;
            this.mTextLinks = other.mTextLinks;
        }

        public Item(CharSequence text) {
            this.mText = text;
            this.mHtmlText = null;
            this.mIntent = null;
            this.mUri = null;
        }

        public Item(CharSequence text, String htmlText) {
            this.mText = text;
            this.mHtmlText = htmlText;
            this.mIntent = null;
            this.mUri = null;
        }

        public Item(Intent intent) {
            this.mText = null;
            this.mHtmlText = null;
            this.mIntent = intent;
            this.mUri = null;
        }

        public Item(Uri uri) {
            this.mText = null;
            this.mHtmlText = null;
            this.mIntent = null;
            this.mUri = uri;
        }

        public Item(CharSequence text, Intent intent, Uri uri) {
            this.mText = text;
            this.mHtmlText = null;
            this.mIntent = intent;
            this.mUri = uri;
        }

        public Item(CharSequence text, String htmlText, Intent intent, Uri uri) {
            if (htmlText != null && text == null) {
                throw new IllegalArgumentException("Plain text must be supplied if HTML text is supplied");
            }
            this.mText = text;
            this.mHtmlText = htmlText;
            this.mIntent = intent;
            this.mUri = uri;
        }

        public CharSequence getText() {
            return this.mText;
        }

        public String getHtmlText() {
            return this.mHtmlText;
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public ActivityInfo getActivityInfo() {
            return this.mActivityInfo;
        }

        public void setActivityInfo(ActivityInfo info) {
            this.mActivityInfo = info;
        }

        public TextLinks getTextLinks() {
            return this.mTextLinks;
        }

        public void setTextLinks(TextLinks textLinks) {
            this.mTextLinks = textLinks;
        }

        public CharSequence coerceToText(Context context) {
            CharSequence text = getText();
            if (text != null) {
                return text;
            }
            Uri uri = getUri();
            if (uri == null) {
                Intent intent = getIntent();
                return intent != null ? intent.toUri(1) : "";
            }
            ContentResolver resolver = context.getContentResolver();
            AssetFileDescriptor descr = null;
            FileInputStream stream = null;
            InputStreamReader reader = null;
            try {
                try {
                    descr = resolver.openTypedAssetFileDescriptor(uri, "text/*", null);
                } catch (FileNotFoundException | RuntimeException e) {
                } catch (SecurityException e2) {
                    Log.m103w("ClipData", "Failure opening stream", e2);
                }
                if (descr == null) {
                    IoUtils.closeQuietly(descr);
                    IoUtils.closeQuietly((AutoCloseable) null);
                    IoUtils.closeQuietly((AutoCloseable) null);
                    String scheme = uri.getScheme();
                    return ("content".equals(scheme) || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme) || "file".equals(scheme)) ? "" : uri.toString();
                }
                try {
                    stream = descr.createInputStream();
                    reader = new InputStreamReader(stream, "UTF-8");
                    StringBuilder builder = new StringBuilder(128);
                    char[] buffer = new char[8192];
                    while (true) {
                        int len = reader.read(buffer);
                        if (len <= 0) {
                            String sb = builder.toString();
                            IoUtils.closeQuietly(descr);
                            IoUtils.closeQuietly(stream);
                            IoUtils.closeQuietly(reader);
                            return sb;
                        }
                        builder.append(buffer, 0, len);
                    }
                } catch (IOException e3) {
                    Log.m103w("ClipData", "Failure loading text", e3);
                    String iOException = e3.toString();
                    IoUtils.closeQuietly(descr);
                    IoUtils.closeQuietly(stream);
                    IoUtils.closeQuietly(reader);
                    return iOException;
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly(descr);
                IoUtils.closeQuietly(stream);
                IoUtils.closeQuietly(reader);
                throw th;
            }
        }

        public CharSequence coerceToStyledText(Context context) {
            CharSequence text = getText();
            if (text instanceof Spanned) {
                return text;
            }
            String htmlText = getHtmlText();
            if (htmlText != null) {
                try {
                    CharSequence newText = Html.fromHtml(htmlText);
                    if (newText != null) {
                        return newText;
                    }
                } catch (RuntimeException e) {
                }
            }
            if (text != null) {
                return text;
            }
            return coerceToHtmlOrStyledText(context, true);
        }

        public String coerceToHtmlText(Context context) {
            String htmlText = getHtmlText();
            if (htmlText != null) {
                return htmlText;
            }
            CharSequence text = getText();
            if (text != null) {
                if (text instanceof Spanned) {
                    return Html.toHtml((Spanned) text);
                }
                return Html.escapeHtml(text);
            }
            CharSequence text2 = coerceToHtmlOrStyledText(context, false);
            if (text2 != null) {
                return text2.toString();
            }
            return null;
        }

        private CharSequence coerceToHtmlOrStyledText(Context context, boolean styled) {
            if (this.mUri == null) {
                Intent intent = this.mIntent;
                return intent != null ? styled ? uriToStyledText(intent.toUri(1)) : uriToHtml(intent.toUri(1)) : "";
            }
            String[] types = null;
            try {
                types = context.getContentResolver().getStreamTypes(this.mUri, "text/*");
            } catch (SecurityException e) {
            }
            boolean hasHtml = false;
            boolean hasText = false;
            String str = "text/html";
            if (types != null) {
                for (String type : types) {
                    if ("text/html".equals(type)) {
                        hasHtml = true;
                    } else if (type.startsWith("text/")) {
                        hasText = true;
                    }
                }
            }
            if (hasHtml || hasText) {
                FileInputStream stream = null;
                try {
                    try {
                        try {
                            try {
                                ContentResolver contentResolver = context.getContentResolver();
                                Uri uri = this.mUri;
                                if (!hasHtml) {
                                    str = "text/plain";
                                }
                                AssetFileDescriptor descr = contentResolver.openTypedAssetFileDescriptor(uri, str, null);
                                stream = descr.createInputStream();
                                InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
                                StringBuilder builder = new StringBuilder(128);
                                char[] buffer = new char[8192];
                                while (true) {
                                    int len = reader.read(buffer);
                                    if (len <= 0) {
                                        break;
                                    }
                                    builder.append(buffer, 0, len);
                                }
                                String text = builder.toString();
                                if (!hasHtml) {
                                    if (styled) {
                                        if (stream != null) {
                                            try {
                                                stream.close();
                                            } catch (IOException e2) {
                                            }
                                        }
                                        return text;
                                    }
                                    String escapeHtml = Html.escapeHtml(text);
                                    if (stream != null) {
                                        try {
                                            stream.close();
                                        } catch (IOException e3) {
                                        }
                                    }
                                    return escapeHtml;
                                } else if (!styled) {
                                    String str2 = text.toString();
                                    if (stream != null) {
                                        try {
                                            stream.close();
                                        } catch (IOException e4) {
                                        }
                                    }
                                    return str2;
                                } else {
                                    try {
                                        CharSequence newText = Html.fromHtml(text);
                                        return newText != null ? newText : text;
                                    } catch (RuntimeException e5) {
                                        if (stream != null) {
                                            try {
                                                stream.close();
                                            } catch (IOException e6) {
                                            }
                                        }
                                        return text;
                                    }
                                }
                            } catch (IOException e7) {
                            }
                        } catch (SecurityException e8) {
                            Log.m103w("ClipData", "Failure opening stream", e8);
                            if (stream != null) {
                                stream.close();
                            }
                        }
                    } catch (FileNotFoundException e9) {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e10) {
                        Log.m103w("ClipData", "Failure loading text", e10);
                        String escapeHtml2 = Html.escapeHtml(e10.toString());
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e11) {
                            }
                        }
                        return escapeHtml2;
                    }
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e12) {
                        }
                    }
                }
            }
            String scheme = this.mUri.getScheme();
            return ("content".equals(scheme) || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme) || "file".equals(scheme)) ? "" : styled ? uriToStyledText(this.mUri.toString()) : uriToHtml(this.mUri.toString());
        }

        private String uriToHtml(String uri) {
            StringBuilder builder = new StringBuilder(256);
            builder.append("<a href=\"");
            builder.append(Html.escapeHtml(uri));
            builder.append("\">");
            builder.append(Html.escapeHtml(uri));
            builder.append("</a>");
            return builder.toString();
        }

        private CharSequence uriToStyledText(String uri) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append((CharSequence) uri);
            builder.setSpan(new URLSpan(uri), 0, builder.length(), 33);
            return builder;
        }

        public String toString() {
            StringBuilder b = new StringBuilder(128);
            b.append("ClipData.Item { ");
            toShortString(b, true);
            b.append(" }");
            return b.toString();
        }

        public void toShortString(StringBuilder b, boolean redactContent) {
            boolean first = true;
            if (this.mHtmlText != null) {
                first = false;
                if (redactContent) {
                    b.append("H(").append(this.mHtmlText.length()).append(')');
                } else {
                    b.append("H:").append(this.mHtmlText);
                }
            }
            if (this.mText != null) {
                if (!first) {
                    b.append(' ');
                }
                first = false;
                if (redactContent) {
                    b.append("T(").append(this.mText.length()).append(')');
                } else {
                    b.append("T:").append(this.mText);
                }
            }
            if (this.mUri != null) {
                if (!first) {
                    b.append(' ');
                }
                first = false;
                if (redactContent) {
                    b.append("U(").append(this.mUri.getScheme()).append(')');
                } else {
                    b.append("U:").append(this.mUri);
                }
            }
            if (this.mIntent != null) {
                if (!first) {
                    b.append(' ');
                }
                b.append("I:");
                this.mIntent.toShortString(b, redactContent, true, true, true);
            }
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            String str = this.mHtmlText;
            if (str != null) {
                proto.write(1138166333441L, str);
            } else {
                CharSequence charSequence = this.mText;
                if (charSequence != null) {
                    proto.write(1138166333442L, charSequence.toString());
                } else {
                    Uri uri = this.mUri;
                    if (uri != null) {
                        proto.write(1138166333443L, uri.toString());
                    } else {
                        Intent intent = this.mIntent;
                        if (intent != null) {
                            intent.dumpDebug(proto, 1146756268036L, true, true, true, true);
                        } else {
                            proto.write(1133871366149L, true);
                        }
                    }
                }
            }
            proto.end(token);
        }
    }

    public ClipData(CharSequence label, String[] mimeTypes, Item item) {
        ClipDescription clipDescription = new ClipDescription(label, mimeTypes);
        this.mClipDescription = clipDescription;
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        this.mIcon = null;
        ArrayList<Item> arrayList = new ArrayList<>();
        this.mItems = arrayList;
        arrayList.add(item);
        clipDescription.setIsStyledText(isStyledText());
    }

    public ClipData(ClipDescription description, Item item) {
        this.mClipDescription = description;
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        this.mIcon = null;
        ArrayList<Item> arrayList = new ArrayList<>();
        this.mItems = arrayList;
        arrayList.add(item);
        description.setIsStyledText(isStyledText());
    }

    public ClipData(ClipDescription description, ArrayList<Item> items) {
        this.mClipDescription = description;
        if (items == null) {
            throw new NullPointerException("item is null");
        }
        this.mIcon = null;
        this.mItems = items;
    }

    public ClipData(ClipData other) {
        this.mClipDescription = other.mClipDescription;
        this.mIcon = other.mIcon;
        this.mItems = new ArrayList<>(other.mItems);
    }

    public ClipData copyForTransferWithActivityInfo() {
        ClipData copy = new ClipData(this);
        copy.mParcelItemActivityInfos = true;
        return copy;
    }

    public boolean willParcelWithActivityInfo() {
        return this.mParcelItemActivityInfos;
    }

    public static ClipData newPlainText(CharSequence label, CharSequence text) {
        Item item = new Item(text);
        return new ClipData(label, MIMETYPES_TEXT_PLAIN, item);
    }

    public static ClipData newHtmlText(CharSequence label, CharSequence text, String htmlText) {
        Item item = new Item(text, htmlText);
        return new ClipData(label, MIMETYPES_TEXT_HTML, item);
    }

    public static ClipData newIntent(CharSequence label, Intent intent) {
        Item item = new Item(intent);
        return new ClipData(label, MIMETYPES_TEXT_INTENT, item);
    }

    public static ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri) {
        Item item = new Item(uri);
        String[] mimeTypes = getMimeTypes(resolver, uri);
        return new ClipData(label, mimeTypes, item);
    }

    private static String[] getMimeTypes(ContentResolver resolver, Uri uri) {
        String[] mimeTypes = null;
        if ("content".equals(uri.getScheme())) {
            String realType = resolver.getType(uri);
            mimeTypes = resolver.getStreamTypes(uri, "*/*");
            if (realType != null) {
                if (mimeTypes == null) {
                    mimeTypes = new String[]{realType};
                } else if (!ArrayUtils.contains(mimeTypes, realType)) {
                    String[] tmp = new String[mimeTypes.length + 1];
                    tmp[0] = realType;
                    System.arraycopy(mimeTypes, 0, tmp, 1, mimeTypes.length);
                    mimeTypes = tmp;
                }
            }
        }
        if (mimeTypes == null) {
            String[] mimeTypes2 = MIMETYPES_TEXT_URILIST;
            return mimeTypes2;
        }
        return mimeTypes;
    }

    public static ClipData newRawUri(CharSequence label, Uri uri) {
        Item item = new Item(uri);
        return new ClipData(label, MIMETYPES_TEXT_URILIST, item);
    }

    public ClipDescription getDescription() {
        return this.mClipDescription;
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        this.mItems.add(item);
        if (this.mItems.size() == 1) {
            this.mClipDescription.setIsStyledText(isStyledText());
        }
    }

    public void addItem(ContentResolver resolver, Item item) {
        addItem(item);
        if (item.getHtmlText() != null) {
            this.mClipDescription.addMimeTypes(MIMETYPES_TEXT_HTML);
        } else if (item.getText() != null) {
            this.mClipDescription.addMimeTypes(MIMETYPES_TEXT_PLAIN);
        }
        if (item.getIntent() != null) {
            this.mClipDescription.addMimeTypes(MIMETYPES_TEXT_INTENT);
        }
        if (item.getUri() != null) {
            this.mClipDescription.addMimeTypes(getMimeTypes(resolver, item.getUri()));
        }
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public int getItemCount() {
        return this.mItems.size();
    }

    public Item getItemAt(int index) {
        return this.mItems.get(index);
    }

    public void setItemAt(int index, Item item) {
        this.mItems.set(index, item);
    }

    public void prepareToLeaveProcess(boolean leavingPackage) {
        prepareToLeaveProcess(leavingPackage, 1);
    }

    public void prepareToLeaveProcess(boolean leavingPackage, int intentFlags) {
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            Item item = this.mItems.get(i);
            if (item.mIntent != null) {
                item.mIntent.prepareToLeaveProcess(leavingPackage);
            }
            if (item.mUri != null && leavingPackage) {
                if (StrictMode.vmFileUriExposureEnabled()) {
                    item.mUri.checkFileUriExposed("ClipData.Item.getUri()");
                }
                if (StrictMode.vmContentUriWithoutPermissionEnabled()) {
                    item.mUri.checkContentUriWithoutPermission("ClipData.Item.getUri()", intentFlags);
                }
            }
        }
    }

    public void prepareToEnterProcess(AttributionSource source) {
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            Item item = this.mItems.get(i);
            if (item.mIntent != null) {
                item.mIntent.prepareToEnterProcess(false, source);
            }
        }
    }

    public void fixUris(int contentUserHint) {
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            Item item = this.mItems.get(i);
            if (item.mIntent != null) {
                item.mIntent.fixUris(contentUserHint);
            }
            if (item.mUri != null) {
                item.mUri = ContentProvider.maybeAddUserId(item.mUri, contentUserHint);
            }
        }
    }

    public void fixUrisLight(int contentUserHint) {
        Uri data;
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            Item item = this.mItems.get(i);
            if (item.mIntent != null && (data = item.mIntent.getData()) != null) {
                item.mIntent.setData(ContentProvider.maybeAddUserId(data, contentUserHint));
            }
            if (item.mUri != null) {
                item.mUri = ContentProvider.maybeAddUserId(item.mUri, contentUserHint);
            }
        }
    }

    private boolean isStyledText() {
        if (this.mItems.isEmpty()) {
            return false;
        }
        CharSequence text = this.mItems.get(0).getText();
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            if (TextUtils.hasStyleSpan(spanned)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(128);
        b.append("ClipData { ");
        toShortString(b, true);
        b.append(" }");
        return b.toString();
    }

    public void toShortString(StringBuilder b, boolean redactContent) {
        ClipDescription clipDescription = this.mClipDescription;
        boolean first = clipDescription != null ? !clipDescription.toShortString(b, redactContent) : true;
        if (this.mIcon != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("I:");
            b.append(this.mIcon.getWidth());
            b.append(EpicenterTranslateClipReveal.StateProperty.TARGET_X);
            b.append(this.mIcon.getHeight());
        }
        if (this.mItems.size() != 1) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append(this.mItems.size()).append(" items:");
        }
        for (int i = 0; i < this.mItems.size(); i++) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append('{');
            this.mItems.get(i).toShortString(b, redactContent);
            b.append('}');
        }
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        ClipDescription clipDescription = this.mClipDescription;
        if (clipDescription != null) {
            clipDescription.dumpDebug(proto, 1146756268033L);
        }
        if (this.mIcon != null) {
            long iToken = proto.start(1146756268034L);
            proto.write(1120986464257L, this.mIcon.getWidth());
            proto.write(1120986464258L, this.mIcon.getHeight());
            proto.end(iToken);
        }
        for (int i = 0; i < this.mItems.size(); i++) {
            this.mItems.get(i).dumpDebug(proto, 2246267895811L);
        }
        proto.end(token);
    }

    public void collectUris(List<Uri> out) {
        for (int i = 0; i < this.mItems.size(); i++) {
            Item item = getItemAt(i);
            if (item.getUri() != null) {
                out.add(item.getUri());
            }
            Intent intent = item.getIntent();
            if (intent != null) {
                if (intent.getData() != null) {
                    out.add(intent.getData());
                }
                if (intent.getClipData() != null) {
                    intent.getClipData().collectUris(out);
                }
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mClipDescription.writeToParcel(dest, flags);
        if (this.mIcon != null) {
            dest.writeInt(1);
            this.mIcon.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        int N = this.mItems.size();
        dest.writeInt(N);
        for (int i = 0; i < N; i++) {
            Item item = this.mItems.get(i);
            TextUtils.writeToParcel(item.mText, dest, flags);
            dest.writeString8(item.mHtmlText);
            dest.writeTypedObject(item.mIntent, flags);
            dest.writeTypedObject(item.mUri, flags);
            dest.writeTypedObject(this.mParcelItemActivityInfos ? item.mActivityInfo : null, flags);
            dest.writeTypedObject(item.mTextLinks, flags);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClipData(Parcel in) {
        this.mClipDescription = new ClipDescription(in);
        if (in.readInt() != 0) {
            this.mIcon = Bitmap.CREATOR.createFromParcel(in);
        } else {
            this.mIcon = null;
        }
        this.mItems = new ArrayList<>();
        int N = in.readInt();
        for (int i = 0; i < N; i++) {
            CharSequence text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            String htmlText = in.readString8();
            Intent intent = (Intent) in.readTypedObject(Intent.CREATOR);
            Uri uri = (Uri) in.readTypedObject(Uri.CREATOR);
            ActivityInfo info = (ActivityInfo) in.readTypedObject(ActivityInfo.CREATOR);
            TextLinks textLinks = (TextLinks) in.readTypedObject(TextLinks.CREATOR);
            Item item = new Item(text, htmlText, intent, uri);
            item.setActivityInfo(info);
            item.setTextLinks(textLinks);
            this.mItems.add(item);
        }
    }
}
