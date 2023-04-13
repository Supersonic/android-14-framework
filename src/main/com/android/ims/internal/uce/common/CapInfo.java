package com.android.ims.internal.uce.common;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class CapInfo implements Parcelable {
    public static final String CALLCOMPOSER = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gppservice.ims.icsi.gsma.callcomposer\"";
    public static final String CAPDISC_VIA_PRESENCE = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.dp\"";
    public static final String CHATBOT = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gppapplication.ims.iari.rcs.chatbot\"";
    public static final String CHATBOTROLE = "+g.gsma.rcs.isbot";
    public static final Parcelable.Creator<CapInfo> CREATOR = new Parcelable.Creator<CapInfo>() { // from class: com.android.ims.internal.uce.common.CapInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CapInfo createFromParcel(Parcel source) {
            return new CapInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CapInfo[] newArray(int size) {
            return new CapInfo[size];
        }
    };
    public static final String FILE_TRANSFER = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.ft\"";
    public static final String FILE_TRANSFER_HTTP = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.fthttp\"";
    public static final String FILE_TRANSFER_SNF = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.ftstandfw\"";
    public static final String FILE_TRANSFER_THUMBNAIL = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.ftthumb\"";
    public static final String FULL_SNF_GROUPCHAT = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.fullsfgroupchat\"";
    public static final String GEOPULL = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geopull\"";
    public static final String GEOPULL_FT = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geopullft\"";
    public static final String GEOPUSH = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geopush\"";
    public static final String GEOSMS = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gppapplication.ims.iari.rcs.geosms\"";
    public static final String IMAGE_SHARE = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.gsma-is\"";
    public static final String INSTANT_MSG = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.im\"";
    public static final String IP_VIDEO = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\";video";
    public static final String IP_VOICE = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"";
    public static final String MMTEL_CALLCOMPOSER = "+g.gsma.callcomposer";
    public static final String POSTCALL = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gppservice.ims.icsi.gsma.callunanswered\"";
    public static final String RCS_IP_VIDEO_CALL = "+g.gsma.rcs.ipvideocall";
    public static final String RCS_IP_VIDEO_ONLY_CALL = "+g.gsma.rcs.ipvideoonlycall";
    public static final String RCS_IP_VOICE_CALL = "+g.gsma.rcs.ipcall";
    public static final String SHAREDMAP = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gppservice.ims.icsi.gsma.sharedmap\"";
    public static final String SHAREDSKETCH = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gppservice.ims.icsi.gsma.sharedsketch\"";
    public static final String SOCIAL_PRESENCE = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.sp\"";
    public static final String STANDALONE_CHATBOT = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.chatbot.sa\"";
    public static final String STANDALONE_MSG = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.msg;urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.largemsg\"";
    public static final String VIDEO_SHARE = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.gsma-vs\"";
    public static final String VIDEO_SHARE_DURING_CS = "+g.3gpp.cs-voice";
    private boolean mCallComposerSupported;
    private Map<String, String> mCapInfoMap;
    private long mCapTimestamp;
    private boolean mCdViaPresenceSupported;
    private boolean mChatbotRoleSupported;
    private boolean mChatbotSupported;
    private String[] mExts;
    private boolean mFtHttpSupported;
    private boolean mFtSnFSupported;
    private boolean mFtSupported;
    private boolean mFtThumbSupported;
    private boolean mFullSnFGroupChatSupported;
    private boolean mGeoPullFtSupported;
    private boolean mGeoPullSupported;
    private boolean mGeoPushSupported;
    private boolean mGeoSmsSupported;
    private boolean mImSupported;
    private boolean mIpVideoSupported;
    private boolean mIpVoiceSupported;
    private boolean mIsSupported;
    private boolean mMmtelCallComposerSupported;
    private boolean mPostCallSupported;
    private boolean mRcsIpVideoCallSupported;
    private boolean mRcsIpVideoOnlyCallSupported;
    private boolean mRcsIpVoiceCallSupported;
    private boolean mSharedMapSupported;
    private boolean mSharedSketchSupported;
    private boolean mSmChatbotSupported;
    private boolean mSmSupported;
    private boolean mSpSupported;
    private boolean mVsDuringCSSupported;
    private boolean mVsSupported;

    public CapInfo() {
        this.mImSupported = false;
        this.mFtSupported = false;
        this.mFtThumbSupported = false;
        this.mFtSnFSupported = false;
        this.mFtHttpSupported = false;
        this.mIsSupported = false;
        this.mVsDuringCSSupported = false;
        this.mVsSupported = false;
        this.mSpSupported = false;
        this.mCdViaPresenceSupported = false;
        this.mIpVoiceSupported = false;
        this.mIpVideoSupported = false;
        this.mGeoPullFtSupported = false;
        this.mGeoPullSupported = false;
        this.mGeoPushSupported = false;
        this.mSmSupported = false;
        this.mFullSnFGroupChatSupported = false;
        this.mRcsIpVoiceCallSupported = false;
        this.mRcsIpVideoCallSupported = false;
        this.mRcsIpVideoOnlyCallSupported = false;
        this.mGeoSmsSupported = false;
        this.mCallComposerSupported = false;
        this.mPostCallSupported = false;
        this.mSharedMapSupported = false;
        this.mSharedSketchSupported = false;
        this.mChatbotSupported = false;
        this.mChatbotRoleSupported = false;
        this.mSmChatbotSupported = false;
        this.mMmtelCallComposerSupported = false;
        this.mExts = new String[10];
        this.mCapTimestamp = 0L;
        this.mCapInfoMap = new HashMap();
    }

    public boolean isImSupported() {
        return this.mImSupported;
    }

    public void setImSupported(boolean imSupported) {
        this.mImSupported = imSupported;
    }

    public boolean isFtThumbSupported() {
        return this.mFtThumbSupported;
    }

    public void setFtThumbSupported(boolean ftThumbSupported) {
        this.mFtThumbSupported = ftThumbSupported;
    }

    public boolean isFtSnFSupported() {
        return this.mFtSnFSupported;
    }

    public void setFtSnFSupported(boolean ftSnFSupported) {
        this.mFtSnFSupported = ftSnFSupported;
    }

    public boolean isFtHttpSupported() {
        return this.mFtHttpSupported;
    }

    public void setFtHttpSupported(boolean ftHttpSupported) {
        this.mFtHttpSupported = ftHttpSupported;
    }

    public boolean isFtSupported() {
        return this.mFtSupported;
    }

    public void setFtSupported(boolean ftSupported) {
        this.mFtSupported = ftSupported;
    }

    public boolean isIsSupported() {
        return this.mIsSupported;
    }

    public void setIsSupported(boolean isSupported) {
        this.mIsSupported = isSupported;
    }

    public boolean isVsDuringCSSupported() {
        return this.mVsDuringCSSupported;
    }

    public void setVsDuringCSSupported(boolean vsDuringCSSupported) {
        this.mVsDuringCSSupported = vsDuringCSSupported;
    }

    public boolean isVsSupported() {
        return this.mVsSupported;
    }

    public void setVsSupported(boolean vsSupported) {
        this.mVsSupported = vsSupported;
    }

    public boolean isSpSupported() {
        return this.mSpSupported;
    }

    public void setSpSupported(boolean spSupported) {
        this.mSpSupported = spSupported;
    }

    public boolean isCdViaPresenceSupported() {
        return this.mCdViaPresenceSupported;
    }

    public void setCdViaPresenceSupported(boolean cdViaPresenceSupported) {
        this.mCdViaPresenceSupported = cdViaPresenceSupported;
    }

    public boolean isIpVoiceSupported() {
        return this.mIpVoiceSupported;
    }

    public void setIpVoiceSupported(boolean ipVoiceSupported) {
        this.mIpVoiceSupported = ipVoiceSupported;
    }

    public boolean isIpVideoSupported() {
        return this.mIpVideoSupported;
    }

    public void setIpVideoSupported(boolean ipVideoSupported) {
        this.mIpVideoSupported = ipVideoSupported;
    }

    public boolean isGeoPullFtSupported() {
        return this.mGeoPullFtSupported;
    }

    public void setGeoPullFtSupported(boolean geoPullFtSupported) {
        this.mGeoPullFtSupported = geoPullFtSupported;
    }

    public boolean isGeoPullSupported() {
        return this.mGeoPullSupported;
    }

    public void setGeoPullSupported(boolean geoPullSupported) {
        this.mGeoPullSupported = geoPullSupported;
    }

    public boolean isGeoPushSupported() {
        return this.mGeoPushSupported;
    }

    public void setGeoPushSupported(boolean geoPushSupported) {
        this.mGeoPushSupported = geoPushSupported;
    }

    public boolean isSmSupported() {
        return this.mSmSupported;
    }

    public void setSmSupported(boolean smSupported) {
        this.mSmSupported = smSupported;
    }

    public boolean isFullSnFGroupChatSupported() {
        return this.mFullSnFGroupChatSupported;
    }

    public boolean isRcsIpVoiceCallSupported() {
        return this.mRcsIpVoiceCallSupported;
    }

    public boolean isRcsIpVideoCallSupported() {
        return this.mRcsIpVideoCallSupported;
    }

    public boolean isRcsIpVideoOnlyCallSupported() {
        return this.mRcsIpVideoOnlyCallSupported;
    }

    public void setFullSnFGroupChatSupported(boolean fullSnFGroupChatSupported) {
        this.mFullSnFGroupChatSupported = fullSnFGroupChatSupported;
    }

    public void setRcsIpVoiceCallSupported(boolean rcsIpVoiceCallSupported) {
        this.mRcsIpVoiceCallSupported = rcsIpVoiceCallSupported;
    }

    public void setRcsIpVideoCallSupported(boolean rcsIpVideoCallSupported) {
        this.mRcsIpVideoCallSupported = rcsIpVideoCallSupported;
    }

    public void setRcsIpVideoOnlyCallSupported(boolean rcsIpVideoOnlyCallSupported) {
        this.mRcsIpVideoOnlyCallSupported = rcsIpVideoOnlyCallSupported;
    }

    public boolean isGeoSmsSupported() {
        return this.mGeoSmsSupported;
    }

    public void setGeoSmsSupported(boolean geoSmsSupported) {
        this.mGeoSmsSupported = geoSmsSupported;
    }

    public boolean isCallComposerSupported() {
        return this.mCallComposerSupported;
    }

    public void setCallComposerSupported(boolean callComposerSupported) {
        this.mCallComposerSupported = callComposerSupported;
    }

    public boolean isPostCallSupported() {
        return this.mPostCallSupported;
    }

    public void setPostCallSupported(boolean postCallSupported) {
        this.mPostCallSupported = postCallSupported;
    }

    public boolean isSharedMapSupported() {
        return this.mSharedMapSupported;
    }

    public void setSharedMapSupported(boolean sharedMapSupported) {
        this.mSharedMapSupported = sharedMapSupported;
    }

    public boolean isSharedSketchSupported() {
        return this.mSharedSketchSupported;
    }

    public void setSharedSketchSupported(boolean sharedSketchSupported) {
        this.mSharedSketchSupported = sharedSketchSupported;
    }

    public boolean isChatbotSupported() {
        return this.mChatbotSupported;
    }

    public void setChatbotSupported(boolean chatbotSupported) {
        this.mChatbotSupported = chatbotSupported;
    }

    public boolean isChatbotRoleSupported() {
        return this.mChatbotRoleSupported;
    }

    public void setChatbotRoleSupported(boolean chatbotRoleSupported) {
        this.mChatbotRoleSupported = chatbotRoleSupported;
    }

    public boolean isSmChatbotSupported() {
        return this.mSmChatbotSupported;
    }

    public void setSmChatbotSupported(boolean smChatbotSupported) {
        this.mSmChatbotSupported = smChatbotSupported;
    }

    public boolean isMmtelCallComposerSupported() {
        return this.mMmtelCallComposerSupported;
    }

    public void setMmtelCallComposerSupported(boolean mmtelCallComposerSupported) {
        this.mMmtelCallComposerSupported = mmtelCallComposerSupported;
    }

    public String[] getExts() {
        return this.mExts;
    }

    public void setExts(String[] exts) {
        this.mExts = exts;
    }

    public long getCapTimestamp() {
        return this.mCapTimestamp;
    }

    public void setCapTimestamp(long capTimestamp) {
        this.mCapTimestamp = capTimestamp;
    }

    public void addCapability(String featureTagName, String versions) {
        this.mCapInfoMap.put(featureTagName, versions);
    }

    public String getCapabilityVersions(String featureTagName) {
        return this.mCapInfoMap.get(featureTagName);
    }

    public void removeCapability(String featureTagName) {
        this.mCapInfoMap.remove(featureTagName);
    }

    public void setCapInfoMap(Map<String, String> capInfoMap) {
        this.mCapInfoMap = capInfoMap;
    }

    public Map<String, String> getCapInfoMap() {
        return this.mCapInfoMap;
    }

    public boolean isCapabilitySupported(String featureTag) {
        return this.mCapInfoMap.containsKey(featureTag);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mImSupported ? 1 : 0);
        dest.writeInt(this.mFtSupported ? 1 : 0);
        dest.writeInt(this.mFtThumbSupported ? 1 : 0);
        dest.writeInt(this.mFtSnFSupported ? 1 : 0);
        dest.writeInt(this.mFtHttpSupported ? 1 : 0);
        dest.writeInt(this.mIsSupported ? 1 : 0);
        dest.writeInt(this.mVsDuringCSSupported ? 1 : 0);
        dest.writeInt(this.mVsSupported ? 1 : 0);
        dest.writeInt(this.mSpSupported ? 1 : 0);
        dest.writeInt(this.mCdViaPresenceSupported ? 1 : 0);
        dest.writeInt(this.mIpVoiceSupported ? 1 : 0);
        dest.writeInt(this.mIpVideoSupported ? 1 : 0);
        dest.writeInt(this.mGeoPullFtSupported ? 1 : 0);
        dest.writeInt(this.mGeoPullSupported ? 1 : 0);
        dest.writeInt(this.mGeoPushSupported ? 1 : 0);
        dest.writeInt(this.mSmSupported ? 1 : 0);
        dest.writeInt(this.mFullSnFGroupChatSupported ? 1 : 0);
        dest.writeInt(this.mGeoSmsSupported ? 1 : 0);
        dest.writeInt(this.mCallComposerSupported ? 1 : 0);
        dest.writeInt(this.mPostCallSupported ? 1 : 0);
        dest.writeInt(this.mSharedMapSupported ? 1 : 0);
        dest.writeInt(this.mSharedSketchSupported ? 1 : 0);
        dest.writeInt(this.mChatbotSupported ? 1 : 0);
        dest.writeInt(this.mChatbotRoleSupported ? 1 : 0);
        dest.writeInt(this.mSmChatbotSupported ? 1 : 0);
        dest.writeInt(this.mMmtelCallComposerSupported ? 1 : 0);
        dest.writeInt(this.mRcsIpVoiceCallSupported ? 1 : 0);
        dest.writeInt(this.mRcsIpVideoCallSupported ? 1 : 0);
        dest.writeInt(this.mRcsIpVideoOnlyCallSupported ? 1 : 0);
        dest.writeStringArray(this.mExts);
        dest.writeLong(this.mCapTimestamp);
        Bundle capInfoBundle = new Bundle();
        for (Map.Entry<String, String> entry : this.mCapInfoMap.entrySet()) {
            capInfoBundle.putString(entry.getKey(), entry.getValue());
        }
        dest.writeBundle(capInfoBundle);
    }

    private CapInfo(Parcel source) {
        this.mImSupported = false;
        this.mFtSupported = false;
        this.mFtThumbSupported = false;
        this.mFtSnFSupported = false;
        this.mFtHttpSupported = false;
        this.mIsSupported = false;
        this.mVsDuringCSSupported = false;
        this.mVsSupported = false;
        this.mSpSupported = false;
        this.mCdViaPresenceSupported = false;
        this.mIpVoiceSupported = false;
        this.mIpVideoSupported = false;
        this.mGeoPullFtSupported = false;
        this.mGeoPullSupported = false;
        this.mGeoPushSupported = false;
        this.mSmSupported = false;
        this.mFullSnFGroupChatSupported = false;
        this.mRcsIpVoiceCallSupported = false;
        this.mRcsIpVideoCallSupported = false;
        this.mRcsIpVideoOnlyCallSupported = false;
        this.mGeoSmsSupported = false;
        this.mCallComposerSupported = false;
        this.mPostCallSupported = false;
        this.mSharedMapSupported = false;
        this.mSharedSketchSupported = false;
        this.mChatbotSupported = false;
        this.mChatbotRoleSupported = false;
        this.mSmChatbotSupported = false;
        this.mMmtelCallComposerSupported = false;
        this.mExts = new String[10];
        this.mCapTimestamp = 0L;
        this.mCapInfoMap = new HashMap();
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mImSupported = source.readInt() != 0;
        this.mFtSupported = source.readInt() != 0;
        this.mFtThumbSupported = source.readInt() != 0;
        this.mFtSnFSupported = source.readInt() != 0;
        this.mFtHttpSupported = source.readInt() != 0;
        this.mIsSupported = source.readInt() != 0;
        this.mVsDuringCSSupported = source.readInt() != 0;
        this.mVsSupported = source.readInt() != 0;
        this.mSpSupported = source.readInt() != 0;
        this.mCdViaPresenceSupported = source.readInt() != 0;
        this.mIpVoiceSupported = source.readInt() != 0;
        this.mIpVideoSupported = source.readInt() != 0;
        this.mGeoPullFtSupported = source.readInt() != 0;
        this.mGeoPullSupported = source.readInt() != 0;
        this.mGeoPushSupported = source.readInt() != 0;
        this.mSmSupported = source.readInt() != 0;
        this.mFullSnFGroupChatSupported = source.readInt() != 0;
        this.mGeoSmsSupported = source.readInt() != 0;
        this.mCallComposerSupported = source.readInt() != 0;
        this.mPostCallSupported = source.readInt() != 0;
        this.mSharedMapSupported = source.readInt() != 0;
        this.mSharedSketchSupported = source.readInt() != 0;
        this.mChatbotSupported = source.readInt() != 0;
        this.mChatbotRoleSupported = source.readInt() != 0;
        this.mSmChatbotSupported = source.readInt() != 0;
        this.mMmtelCallComposerSupported = source.readInt() != 0;
        this.mRcsIpVoiceCallSupported = source.readInt() != 0;
        this.mRcsIpVideoCallSupported = source.readInt() != 0;
        this.mRcsIpVideoOnlyCallSupported = source.readInt() != 0;
        this.mExts = source.createStringArray();
        this.mCapTimestamp = source.readLong();
        Bundle capInfoBundle = source.readBundle();
        for (String key : capInfoBundle.keySet()) {
            this.mCapInfoMap.put(key, capInfoBundle.getString(key));
        }
    }
}
