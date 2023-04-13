package com.android.ims.rcs.uce.util;

import android.net.Uri;
import android.telephony.ims.RcsContactUceCapability;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class FeatureTags {
    public static final String FEATURE_TAG_CALL_COMPOSER_ENRICHED_CALLING = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.callcomposer\"";
    public static final String FEATURE_TAG_CALL_COMPOSER_VIA_TELEPHONY = "+g.gsma.callcomposer";
    public static final String FEATURE_TAG_CHATBOT_COMMUNICATION_USING_SESSION = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.chatbot\"";
    public static final String FEATURE_TAG_CHATBOT_COMMUNICATION_USING_STANDALONE_MSG = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.chatbot.sa\"";
    public static final String FEATURE_TAG_CHATBOT_ROLE = "+g.gsma.rcs.isbot";
    public static final String FEATURE_TAG_CHATBOT_VERSION_SUPPORTED = "+g.gsma.rcs.botversion=\"#=1\"";
    public static final String FEATURE_TAG_CHATBOT_VERSION_V2_SUPPORTED = "+g.gsma.rcs.botversion=\"#=1,#=2\"";
    public static final String FEATURE_TAG_CHAT_IM = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.im\"";
    public static final String FEATURE_TAG_CHAT_SESSION = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.session\"";
    public static final String FEATURE_TAG_DEFERRED_MESSAGING = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.deferred\"";
    public static final String FEATURE_TAG_FILE_TRANSFER = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.fthttp\"";
    public static final String FEATURE_TAG_FILE_TRANSFER_VIA_SMS = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.ftsms\"";
    public static final String FEATURE_TAG_GEO_PUSH = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geopush\"";
    public static final String FEATURE_TAG_GEO_PUSH_VIA_SMS = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcs.geosms\"";
    public static final String FEATURE_TAG_LARGE_MODE = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.largemsg\"";
    public static final String FEATURE_TAG_LARGE_PAGER_MODE = "+g.gsma.rcs.cpm.pager-large";
    public static final String FEATURE_TAG_MMTEL = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"";
    public static final String FEATURE_TAG_PAGER_MODE = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.msg\"";
    public static final String FEATURE_TAG_POST_CALL = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.callunanswered\"";
    public static final String FEATURE_TAG_PRESENCE = "+g.3gpp.iari-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.rcse.dp\"";
    public static final String FEATURE_TAG_SHARED_MAP = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.sharedmap\"";
    public static final String FEATURE_TAG_SHARED_SKETCH = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.gsma.sharedsketch\"";
    public static final String FEATURE_TAG_STANDALONE_MSG = "+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.msg,urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.largemsg,urn%3Aurn-7%3A3gpp-service.ims.icsi.oma.cpm.deferred\";+g.gsma.rcs.cpm.pager-large";
    public static final String FEATURE_TAG_VIDEO = "video";

    public static void addFeatureTags(RcsContactUceCapability.OptionsBuilder optionsBuilder, boolean mmtelAudioSupport, boolean mmtelVideoSupport, boolean presenceSupport, boolean callComposerSupport, Set<String> registrationTags) {
        if (presenceSupport) {
            registrationTags.add(FEATURE_TAG_PRESENCE);
        } else {
            registrationTags.remove(FEATURE_TAG_PRESENCE);
        }
        if (mmtelAudioSupport && mmtelVideoSupport) {
            registrationTags.add(FEATURE_TAG_MMTEL);
            registrationTags.add("video");
        } else if (mmtelAudioSupport) {
            registrationTags.add(FEATURE_TAG_MMTEL);
            registrationTags.remove("video");
        } else {
            registrationTags.remove(FEATURE_TAG_MMTEL);
            registrationTags.remove("video");
        }
        if (callComposerSupport) {
            registrationTags.add(FEATURE_TAG_CALL_COMPOSER_VIA_TELEPHONY);
        } else {
            registrationTags.remove(FEATURE_TAG_CALL_COMPOSER_VIA_TELEPHONY);
        }
        if (!registrationTags.isEmpty()) {
            optionsBuilder.addFeatureTags(registrationTags);
        }
    }

    public static RcsContactUceCapability getContactCapability(Uri contact, int sourceType, List<String> featureTags) {
        final RcsContactUceCapability.OptionsBuilder builder = new RcsContactUceCapability.OptionsBuilder(contact, sourceType);
        builder.setRequestResult(3);
        featureTags.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.util.FeatureTags$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                builder.addFeatureTag((String) obj);
            }
        });
        return builder.build();
    }
}
