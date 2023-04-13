package com.google.android.mms;

import com.android.internal.widget.MessagingMessage;
import java.util.ArrayList;
/* loaded from: classes5.dex */
public class ContentType {
    public static final String APP_DRM_CONTENT = "application/vnd.oma.drm.content";
    public static final String APP_DRM_MESSAGE = "application/vnd.oma.drm.message";
    public static final String APP_SMIL = "application/smil";
    public static final String APP_WAP_XHTML = "application/vnd.wap.xhtml+xml";
    public static final String APP_XHTML = "application/xhtml+xml";
    public static final String AUDIO_3GPP = "audio/3gpp";
    public static final String AUDIO_AAC = "audio/aac";
    public static final String AUDIO_AMR = "audio/amr";
    public static final String AUDIO_IMELODY = "audio/imelody";
    public static final String AUDIO_MID = "audio/mid";
    public static final String AUDIO_MIDI = "audio/midi";
    public static final String AUDIO_MP3 = "audio/mp3";
    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String AUDIO_MPEG = "audio/mpeg";
    public static final String AUDIO_MPEG3 = "audio/mpeg3";
    public static final String AUDIO_MPG = "audio/mpg";
    public static final String AUDIO_OGG = "application/ogg";
    public static final String AUDIO_OGG2 = "audio/ogg";
    public static final String AUDIO_UNSPECIFIED = "audio/*";
    public static final String AUDIO_X_MID = "audio/x-mid";
    public static final String AUDIO_X_MIDI = "audio/x-midi";
    public static final String AUDIO_X_MP3 = "audio/x-mp3";
    public static final String AUDIO_X_MPEG = "audio/x-mpeg";
    public static final String AUDIO_X_MPEG3 = "audio/x-mpeg3";
    public static final String AUDIO_X_MPG = "audio/x-mpg";
    public static final String AUDIO_X_WAV = "audio/x-wav";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_JPG = "image/jpg";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String IMAGE_WBMP = "image/vnd.wap.wbmp";
    public static final String IMAGE_X_MS_BMP = "image/x-ms-bmp";
    public static final String MMS_GENERIC = "application/vnd.wap.mms-generic";
    public static final String MMS_MESSAGE = "application/vnd.wap.mms-message";
    public static final String MULTIPART_ALTERNATIVE = "application/vnd.wap.multipart.alternative";
    public static final String MULTIPART_MIXED = "application/vnd.wap.multipart.mixed";
    public static final String MULTIPART_RELATED = "application/vnd.wap.multipart.related";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_VCALENDAR = "text/x-vCalendar";
    public static final String TEXT_VCARD = "text/x-vCard";
    public static final String VIDEO_3G2 = "video/3gpp2";
    public static final String VIDEO_3GPP = "video/3gpp";
    public static final String VIDEO_H263 = "video/h263";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_UNSPECIFIED = "video/*";
    private static final ArrayList<String> sSupportedAudioTypes;
    private static final ArrayList<String> sSupportedContentTypes;
    private static final ArrayList<String> sSupportedImageTypes;
    private static final ArrayList<String> sSupportedVideoTypes;

    static {
        ArrayList<String> arrayList = new ArrayList<>();
        sSupportedContentTypes = arrayList;
        ArrayList<String> arrayList2 = new ArrayList<>();
        sSupportedImageTypes = arrayList2;
        ArrayList<String> arrayList3 = new ArrayList<>();
        sSupportedAudioTypes = arrayList3;
        ArrayList<String> arrayList4 = new ArrayList<>();
        sSupportedVideoTypes = arrayList4;
        arrayList.add("text/plain");
        arrayList.add("text/html");
        arrayList.add(TEXT_VCALENDAR);
        arrayList.add(TEXT_VCARD);
        arrayList.add(IMAGE_JPEG);
        arrayList.add(IMAGE_GIF);
        arrayList.add(IMAGE_WBMP);
        arrayList.add(IMAGE_PNG);
        arrayList.add(IMAGE_JPG);
        arrayList.add(IMAGE_X_MS_BMP);
        arrayList.add(AUDIO_AAC);
        arrayList.add(AUDIO_AMR);
        arrayList.add(AUDIO_IMELODY);
        arrayList.add(AUDIO_MID);
        arrayList.add(AUDIO_MIDI);
        arrayList.add(AUDIO_MP3);
        arrayList.add(AUDIO_MP4);
        arrayList.add(AUDIO_MPEG3);
        arrayList.add("audio/mpeg");
        arrayList.add(AUDIO_MPG);
        arrayList.add(AUDIO_X_MID);
        arrayList.add(AUDIO_X_MIDI);
        arrayList.add(AUDIO_X_MP3);
        arrayList.add(AUDIO_X_MPEG3);
        arrayList.add(AUDIO_X_MPEG);
        arrayList.add(AUDIO_X_MPG);
        arrayList.add(AUDIO_X_WAV);
        arrayList.add("audio/3gpp");
        arrayList.add(AUDIO_OGG);
        arrayList.add(AUDIO_OGG2);
        arrayList.add("video/3gpp");
        arrayList.add(VIDEO_3G2);
        arrayList.add(VIDEO_H263);
        arrayList.add(VIDEO_MP4);
        arrayList.add(APP_SMIL);
        arrayList.add(APP_WAP_XHTML);
        arrayList.add(APP_XHTML);
        arrayList.add(APP_DRM_CONTENT);
        arrayList.add("application/vnd.oma.drm.message");
        arrayList2.add(IMAGE_JPEG);
        arrayList2.add(IMAGE_GIF);
        arrayList2.add(IMAGE_WBMP);
        arrayList2.add(IMAGE_PNG);
        arrayList2.add(IMAGE_JPG);
        arrayList2.add(IMAGE_X_MS_BMP);
        arrayList3.add(AUDIO_AAC);
        arrayList3.add(AUDIO_AMR);
        arrayList3.add(AUDIO_IMELODY);
        arrayList3.add(AUDIO_MID);
        arrayList3.add(AUDIO_MIDI);
        arrayList3.add(AUDIO_MP3);
        arrayList3.add(AUDIO_MPEG3);
        arrayList3.add("audio/mpeg");
        arrayList3.add(AUDIO_MPG);
        arrayList3.add(AUDIO_MP4);
        arrayList3.add(AUDIO_X_MID);
        arrayList3.add(AUDIO_X_MIDI);
        arrayList3.add(AUDIO_X_MP3);
        arrayList3.add(AUDIO_X_MPEG3);
        arrayList3.add(AUDIO_X_MPEG);
        arrayList3.add(AUDIO_X_MPG);
        arrayList3.add(AUDIO_X_WAV);
        arrayList3.add("audio/3gpp");
        arrayList3.add(AUDIO_OGG);
        arrayList3.add(AUDIO_OGG2);
        arrayList4.add("video/3gpp");
        arrayList4.add(VIDEO_3G2);
        arrayList4.add(VIDEO_H263);
        arrayList4.add(VIDEO_MP4);
    }

    private ContentType() {
    }

    public static boolean isSupportedType(String contentType) {
        return contentType != null && sSupportedContentTypes.contains(contentType);
    }

    public static boolean isSupportedImageType(String contentType) {
        return isImageType(contentType) && isSupportedType(contentType);
    }

    public static boolean isSupportedAudioType(String contentType) {
        return isAudioType(contentType) && isSupportedType(contentType);
    }

    public static boolean isSupportedVideoType(String contentType) {
        return isVideoType(contentType) && isSupportedType(contentType);
    }

    public static boolean isTextType(String contentType) {
        return contentType != null && contentType.startsWith("text/");
    }

    public static boolean isImageType(String contentType) {
        return contentType != null && contentType.startsWith(MessagingMessage.IMAGE_MIME_TYPE_PREFIX);
    }

    public static boolean isAudioType(String contentType) {
        return contentType != null && contentType.startsWith("audio/");
    }

    public static boolean isVideoType(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    public static boolean isDrmType(String contentType) {
        return contentType != null && (contentType.equals(APP_DRM_CONTENT) || contentType.equals("application/vnd.oma.drm.message"));
    }

    public static boolean isUnspecified(String contentType) {
        return contentType != null && contentType.endsWith("*");
    }

    public static ArrayList<String> getImageTypes() {
        return (ArrayList) sSupportedImageTypes.clone();
    }

    public static ArrayList<String> getAudioTypes() {
        return (ArrayList) sSupportedAudioTypes.clone();
    }

    public static ArrayList<String> getVideoTypes() {
        return (ArrayList) sSupportedVideoTypes.clone();
    }

    public static ArrayList<String> getSupportedTypes() {
        return (ArrayList) sSupportedContentTypes.clone();
    }
}
