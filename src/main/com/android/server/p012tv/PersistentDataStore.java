package com.android.server.p012tv;

import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContentRating;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.tv.PersistentDataStore */
/* loaded from: classes2.dex */
public final class PersistentDataStore {
    public final AtomicFile mAtomicFile;
    public boolean mBlockedRatingsChanged;
    public final Context mContext;
    public boolean mLoaded;
    public boolean mParentalControlsEnabled;
    public boolean mParentalControlsEnabledChanged;
    public final Handler mHandler = new Handler();
    public final List<TvContentRating> mBlockedRatings = Collections.synchronizedList(new ArrayList());
    public final Runnable mSaveRunnable = new Runnable() { // from class: com.android.server.tv.PersistentDataStore.1
        @Override // java.lang.Runnable
        public void run() {
            PersistentDataStore.this.save();
        }
    };

    public PersistentDataStore(Context context, int i) {
        this.mContext = context;
        File userSystemDirectory = Environment.getUserSystemDirectory(i);
        if (!userSystemDirectory.exists() && !userSystemDirectory.mkdirs()) {
            throw new IllegalStateException("User dir cannot be created: " + userSystemDirectory);
        }
        this.mAtomicFile = new AtomicFile(new File(userSystemDirectory, "tv-input-manager-state.xml"), "tv-input-state");
    }

    public boolean isParentalControlsEnabled() {
        loadIfNeeded();
        return this.mParentalControlsEnabled;
    }

    public void setParentalControlsEnabled(boolean z) {
        loadIfNeeded();
        if (this.mParentalControlsEnabled != z) {
            this.mParentalControlsEnabled = z;
            this.mParentalControlsEnabledChanged = true;
            postSave();
        }
    }

    public boolean isRatingBlocked(TvContentRating tvContentRating) {
        loadIfNeeded();
        synchronized (this.mBlockedRatings) {
            for (TvContentRating tvContentRating2 : this.mBlockedRatings) {
                if (tvContentRating.contains(tvContentRating2)) {
                    return true;
                }
            }
            return false;
        }
    }

    public TvContentRating[] getBlockedRatings() {
        loadIfNeeded();
        List<TvContentRating> list = this.mBlockedRatings;
        return (TvContentRating[]) list.toArray(new TvContentRating[list.size()]);
    }

    public void addBlockedRating(TvContentRating tvContentRating) {
        loadIfNeeded();
        if (tvContentRating == null || this.mBlockedRatings.contains(tvContentRating)) {
            return;
        }
        this.mBlockedRatings.add(tvContentRating);
        this.mBlockedRatingsChanged = true;
        postSave();
    }

    public void removeBlockedRating(TvContentRating tvContentRating) {
        loadIfNeeded();
        if (tvContentRating == null || !this.mBlockedRatings.contains(tvContentRating)) {
            return;
        }
        this.mBlockedRatings.remove(tvContentRating);
        this.mBlockedRatingsChanged = true;
        postSave();
    }

    public final void loadIfNeeded() {
        if (this.mLoaded) {
            return;
        }
        load();
        this.mLoaded = true;
    }

    public final void clearState() {
        this.mBlockedRatings.clear();
        this.mParentalControlsEnabled = false;
    }

    public final void load() {
        clearState();
        try {
            FileInputStream openRead = this.mAtomicFile.openRead();
            try {
                try {
                    loadFromXml(Xml.resolvePullParser(openRead));
                } finally {
                    IoUtils.closeQuietly(openRead);
                }
            } catch (IOException | XmlPullParserException e) {
                Slog.w("TvInputManagerService", "Failed to load tv input manager persistent store data.", e);
                clearState();
            }
        } catch (FileNotFoundException unused) {
        }
    }

    public final void postSave() {
        this.mHandler.removeCallbacks(this.mSaveRunnable);
        this.mHandler.post(this.mSaveRunnable);
    }

    public final void save() {
        try {
            FileOutputStream startWrite = this.mAtomicFile.startWrite();
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            saveToXml(resolveSerializer);
            resolveSerializer.flush();
            this.mAtomicFile.finishWrite(startWrite);
            broadcastChangesIfNeeded();
        } catch (IOException e) {
            Slog.w("TvInputManagerService", "Failed to save tv input manager persistent store data.", e);
        }
    }

    public final void broadcastChangesIfNeeded() {
        if (this.mParentalControlsEnabledChanged) {
            this.mParentalControlsEnabledChanged = false;
            this.mContext.sendBroadcastAsUser(new Intent("android.media.tv.action.PARENTAL_CONTROLS_ENABLED_CHANGED"), UserHandle.ALL);
        }
        if (this.mBlockedRatingsChanged) {
            this.mBlockedRatingsChanged = false;
            this.mContext.sendBroadcastAsUser(new Intent("android.media.tv.action.BLOCKED_RATINGS_CHANGED"), UserHandle.ALL);
        }
    }

    public final void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        XmlUtils.beginDocument(typedXmlPullParser, "tv-input-manager-state");
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("blocked-ratings")) {
                loadBlockedRatingsFromXml(typedXmlPullParser);
            } else if (typedXmlPullParser.getName().equals("parental-controls")) {
                this.mParentalControlsEnabled = typedXmlPullParser.getAttributeBoolean((String) null, "enabled");
            }
        }
    }

    public final void loadBlockedRatingsFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("rating")) {
                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "string");
                if (TextUtils.isEmpty(attributeValue)) {
                    throw new XmlPullParserException("Missing string attribute on rating");
                }
                this.mBlockedRatings.add(TvContentRating.unflattenFromString(attributeValue));
            }
        }
    }

    public final void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.startDocument((String) null, Boolean.TRUE);
        typedXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        typedXmlSerializer.startTag((String) null, "tv-input-manager-state");
        typedXmlSerializer.startTag((String) null, "blocked-ratings");
        synchronized (this.mBlockedRatings) {
            for (TvContentRating tvContentRating : this.mBlockedRatings) {
                typedXmlSerializer.startTag((String) null, "rating");
                typedXmlSerializer.attribute((String) null, "string", tvContentRating.flattenToString());
                typedXmlSerializer.endTag((String) null, "rating");
            }
        }
        typedXmlSerializer.endTag((String) null, "blocked-ratings");
        typedXmlSerializer.startTag((String) null, "parental-controls");
        typedXmlSerializer.attributeBoolean((String) null, "enabled", this.mParentalControlsEnabled);
        typedXmlSerializer.endTag((String) null, "parental-controls");
        typedXmlSerializer.endTag((String) null, "tv-input-manager-state");
        typedXmlSerializer.endDocument();
    }
}
