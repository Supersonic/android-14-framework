package com.android.ims.rcs.uce.presence.pidfparser;

import android.net.Uri;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Audio;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Duplex;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.ServiceCaps;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Video;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.Description;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.ServiceDescription;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.ServiceId;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.Version;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Basic;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Contact;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Presence;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Status;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Timestamp;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Tuple;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class PidfParserUtils {
    private static String[] REQUEST_RESULT_REASON_NOT_FOUND = {"noresource", "rejected"};

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Presence getPresence(RcsContactUceCapability capabilities) {
        Presence presence = new Presence(capabilities.getContactUri());
        List<RcsContactPresenceTuple> tupleList = capabilities.getCapabilityTuples();
        if (tupleList == null || tupleList.isEmpty()) {
            return presence;
        }
        for (RcsContactPresenceTuple presenceTuple : tupleList) {
            Tuple tupleElement = getTupleElement(presenceTuple);
            if (tupleElement != null) {
                presence.addTuple(tupleElement);
            }
        }
        return presence;
    }

    private static Tuple getTupleElement(RcsContactPresenceTuple presenceTuple) {
        if (presenceTuple == null) {
            return null;
        }
        Tuple tupleElement = new Tuple();
        handleTupleStatusElement(tupleElement, presenceTuple.getStatus());
        handleTupleServiceDescriptionElement(tupleElement, presenceTuple.getServiceId(), presenceTuple.getServiceVersion(), presenceTuple.getServiceDescription());
        handleServiceCapsElement(tupleElement, presenceTuple.getServiceCapabilities());
        handleTupleContactElement(tupleElement, presenceTuple.getContactUri());
        return tupleElement;
    }

    private static void handleTupleContactElement(Tuple tupleElement, Uri uri) {
        if (uri == null) {
            return;
        }
        Contact contactElement = new Contact();
        contactElement.setContact(uri.toString());
        tupleElement.setContact(contactElement);
    }

    private static void handleTupleStatusElement(Tuple tupleElement, String status) {
        if (TextUtils.isEmpty(status)) {
            return;
        }
        Basic basicElement = new Basic(status);
        Status statusElement = new Status();
        statusElement.setBasic(basicElement);
        tupleElement.setStatus(statusElement);
    }

    private static void handleTupleServiceDescriptionElement(Tuple tupleElement, String serviceId, String version, String description) {
        String[] versionAry;
        ServiceId serviceIdElement = null;
        Version versionElement = null;
        Description descriptionElement = null;
        if (!TextUtils.isEmpty(serviceId)) {
            serviceIdElement = new ServiceId(serviceId);
        }
        if (!TextUtils.isEmpty(version) && (versionAry = version.split("\\.")) != null && versionAry.length == 2) {
            int majorVersion = Integer.parseInt(versionAry[0]);
            int minorVersion = Integer.parseInt(versionAry[1]);
            versionElement = new Version(majorVersion, minorVersion);
        }
        if (!TextUtils.isEmpty(description)) {
            descriptionElement = new Description(description);
        }
        if (serviceIdElement != null && versionElement != null) {
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setServiceId(serviceIdElement);
            serviceDescription.setVersion(versionElement);
            if (descriptionElement != null) {
                serviceDescription.setDescription(descriptionElement);
            }
            tupleElement.setServiceDescription(serviceDescription);
        }
    }

    private static void handleServiceCapsElement(Tuple tupleElement, RcsContactPresenceTuple.ServiceCapabilities serviceCaps) {
        if (serviceCaps == null) {
            return;
        }
        ServiceCaps servCapsElement = new ServiceCaps();
        Audio audioElement = new Audio(serviceCaps.isAudioCapable());
        Video videoElement = new Video(serviceCaps.isVideoCapable());
        servCapsElement.addElement(audioElement);
        servCapsElement.addElement(videoElement);
        List<String> supportedDuplexModes = serviceCaps.getSupportedDuplexModes();
        List<String> UnsupportedDuplexModes = serviceCaps.getUnsupportedDuplexModes();
        if ((supportedDuplexModes != null && !supportedDuplexModes.isEmpty()) || (UnsupportedDuplexModes != null && !UnsupportedDuplexModes.isEmpty())) {
            Duplex duplex = new Duplex();
            if (!supportedDuplexModes.isEmpty()) {
                duplex.addSupportedType(supportedDuplexModes.get(0));
            }
            if (!UnsupportedDuplexModes.isEmpty()) {
                duplex.addNotSupportedType(UnsupportedDuplexModes.get(0));
            }
            servCapsElement.addElement(duplex);
        }
        tupleElement.setServiceCaps(servCapsElement);
    }

    public static String getTupleStatus(Tuple tuple) {
        Status status;
        Basic basic;
        if (tuple == null || (status = tuple.getStatus()) == null || (basic = status.getBasic()) == null) {
            return null;
        }
        return basic.getValue();
    }

    public static String getTupleServiceId(Tuple tuple) {
        ServiceDescription servDescription;
        ServiceId serviceId;
        if (tuple == null || (servDescription = tuple.getServiceDescription()) == null || (serviceId = servDescription.getServiceId()) == null) {
            return null;
        }
        return serviceId.getValue();
    }

    public static String getTupleServiceVersion(Tuple tuple) {
        ServiceDescription servDescription;
        Version version;
        if (tuple == null || (servDescription = tuple.getServiceDescription()) == null || (version = servDescription.getVersion()) == null) {
            return null;
        }
        return version.getValue();
    }

    public static String getTupleServiceDescription(Tuple tuple) {
        ServiceDescription servDescription;
        Description description;
        if (tuple == null || (servDescription = tuple.getServiceDescription()) == null || (description = servDescription.getDescription()) == null) {
            return null;
        }
        return description.getValue();
    }

    public static String getTupleContact(Tuple tuple) {
        Contact contact;
        if (tuple == null || (contact = tuple.getContact()) == null) {
            return null;
        }
        return contact.getContact();
    }

    public static String getTupleTimestamp(Tuple tuple) {
        Timestamp timestamp;
        if (tuple == null || (timestamp = tuple.getTimestamp()) == null) {
            return null;
        }
        return timestamp.getValue();
    }

    public static boolean getTupleMalformedStatus(Tuple tuple) {
        if (tuple == null) {
            return false;
        }
        return tuple.getMalformed();
    }

    public static RcsContactUceCapability getTerminatedCapability(Uri contact, final String reason) {
        int requestResult;
        if (reason == null) {
            reason = "";
        }
        Stream stream = Arrays.stream(REQUEST_RESULT_REASON_NOT_FOUND);
        Objects.requireNonNull(reason);
        if (stream.anyMatch(new Predicate() { // from class: com.android.ims.rcs.uce.presence.pidfparser.PidfParserUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return reason.equalsIgnoreCase((String) obj);
            }
        })) {
            requestResult = 2;
        } else {
            requestResult = 0;
        }
        RcsContactUceCapability.PresenceBuilder builder = new RcsContactUceCapability.PresenceBuilder(contact, 0, requestResult);
        return builder.build();
    }

    public static RcsContactUceCapability getNotFoundContactCapabilities(Uri contact) {
        RcsContactUceCapability.PresenceBuilder builder = new RcsContactUceCapability.PresenceBuilder(contact, 0, 2);
        return builder.build();
    }
}
