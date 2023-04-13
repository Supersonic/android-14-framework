package com.android.ims.rcs.uce.presence.publish;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.ims.rcs.uce.util.FeatureTags;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class PublishServiceDescTracker {
    private static final Map<ServiceDescription, Set<String>> DEFAULT_SERVICE_DESCRIPTION_MAP;
    private static final String TAG = "PublishServiceDescTracker";
    private final Map<ServiceDescription, Set<String>> mServiceDescriptionFeatureTagMap;
    private final Set<ServiceDescription> mServiceDescriptionPartialMatches = new ArraySet();
    private final Set<ServiceDescription> mRegistrationCapabilities = new ArraySet();
    private Set<String> mRegistrationFeatureTags = new ArraySet();

    static {
        ArrayMap<ServiceDescription, Set<String>> map = new ArrayMap<>(23);
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHAT_IM, Collections.singleton(FeatureTags.FEATURE_TAG_CHAT_IM));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHAT_SESSION, Collections.singleton(FeatureTags.FEATURE_TAG_CHAT_SESSION));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_FT, Collections.singleton(FeatureTags.FEATURE_TAG_FILE_TRANSFER));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_FT_SMS, Collections.singleton(FeatureTags.FEATURE_TAG_FILE_TRANSFER_VIA_SMS));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_PRESENCE, Collections.singleton(FeatureTags.FEATURE_TAG_PRESENCE));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_MMTEL_VOICE, Collections.singleton(FeatureTags.FEATURE_TAG_MMTEL));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_MMTEL_VOICE_VIDEO, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_MMTEL, "video")));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_GEOPUSH, Collections.singleton(FeatureTags.FEATURE_TAG_GEO_PUSH));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_GEOPUSH_SMS, Collections.singleton(FeatureTags.FEATURE_TAG_GEO_PUSH_VIA_SMS));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CALL_COMPOSER, Collections.singleton(FeatureTags.FEATURE_TAG_CALL_COMPOSER_ENRICHED_CALLING));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CALL_COMPOSER_MMTEL, Collections.singleton(FeatureTags.FEATURE_TAG_CALL_COMPOSER_VIA_TELEPHONY));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_POST_CALL, Collections.singleton(FeatureTags.FEATURE_TAG_POST_CALL));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_SHARED_MAP, Collections.singleton(FeatureTags.FEATURE_TAG_SHARED_MAP));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_SHARED_SKETCH, Collections.singleton(FeatureTags.FEATURE_TAG_SHARED_SKETCH));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SESSION, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_SESSION, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SESSION_V1, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_SESSION, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_V2_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SESSION_V2, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_SESSION, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_V2_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SA_SESSION, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_STANDALONE_MSG, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SA_SESSION_V1, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_STANDALONE_MSG, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_V2_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_SA_SESSION_V2, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_CHATBOT_COMMUNICATION_USING_STANDALONE_MSG, FeatureTags.FEATURE_TAG_CHATBOT_VERSION_V2_SUPPORTED)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_CHATBOT_ROLE, Collections.singleton(FeatureTags.FEATURE_TAG_CHATBOT_ROLE));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_SLM, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_PAGER_MODE, FeatureTags.FEATURE_TAG_LARGE_MODE, FeatureTags.FEATURE_TAG_DEFERRED_MESSAGING, FeatureTags.FEATURE_TAG_LARGE_PAGER_MODE)));
        map.put(ServiceDescription.SERVICE_DESCRIPTION_SLM_PAGER_LARGE, new ArraySet<>(Arrays.asList(FeatureTags.FEATURE_TAG_PAGER_MODE, FeatureTags.FEATURE_TAG_LARGE_MODE)));
        DEFAULT_SERVICE_DESCRIPTION_MAP = Collections.unmodifiableMap(map);
    }

    public static PublishServiceDescTracker fromCarrierConfig(String[] carrierConfig) {
        Map<ServiceDescription, Set<String>> elements = new ArrayMap<>();
        for (Map.Entry<ServiceDescription, Set<String>> entry : DEFAULT_SERVICE_DESCRIPTION_MAP.entrySet()) {
            elements.put(entry.getKey(), (Set) entry.getValue().stream().map(new Function() { // from class: com.android.ims.rcs.uce.presence.publish.PublishServiceDescTracker$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String removeInconsistencies;
                    removeInconsistencies = PublishServiceDescTracker.removeInconsistencies((String) obj);
                    return removeInconsistencies;
                }
            }).collect(Collectors.toSet()));
        }
        if (carrierConfig != null) {
            for (String entry2 : carrierConfig) {
                String[] serviceDesc = entry2.split("\\|");
                if (serviceDesc.length < 4) {
                    Log.w(TAG, "fromCarrierConfig: error parsing " + entry2);
                } else {
                    elements.put(new ServiceDescription(serviceDesc[0].trim(), serviceDesc[1].trim(), serviceDesc[2].trim()), parseFeatureTags(serviceDesc[3]));
                }
            }
        }
        return new PublishServiceDescTracker(elements);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Set<String> parseFeatureTags(String featureTags) {
        String[] featureTagSplit = featureTags.split(";");
        if (featureTagSplit.length == 0) {
            return Collections.emptySet();
        }
        ArraySet<String> tags = new ArraySet<>(featureTagSplit.length);
        for (String tag : featureTagSplit) {
            tags.add(removeInconsistencies(tag));
        }
        return tags;
    }

    private PublishServiceDescTracker(Map<ServiceDescription, Set<String>> serviceFeatureTagMap) {
        this.mServiceDescriptionFeatureTagMap = serviceFeatureTagMap;
        Set<ServiceDescription> keySet = serviceFeatureTagMap.keySet();
        for (final ServiceDescription c : keySet) {
            this.mServiceDescriptionPartialMatches.addAll((Collection) keySet.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.PublishServiceDescTracker$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return PublishServiceDescTracker.lambda$new$0(ServiceDescription.this, (ServiceDescription) obj);
                }
            }).collect(Collectors.toList()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$0(ServiceDescription c, ServiceDescription s) {
        return !Objects.equals(s, c) && isSimilar(c, s);
    }

    public void updateImsRegistration(Set<String> imsRegistration) {
        Set<String> sanitizedTags = (Set) imsRegistration.stream().map(new Function() { // from class: com.android.ims.rcs.uce.presence.publish.PublishServiceDescTracker$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Set parseFeatureTags;
                parseFeatureTags = PublishServiceDescTracker.parseFeatureTags((String) obj);
                return parseFeatureTags;
            }
        }).map(new Function() { // from class: com.android.ims.rcs.uce.presence.publish.PublishServiceDescTracker$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PublishServiceDescTracker.lambda$updateImsRegistration$1((Set) obj);
            }
        }).collect(Collectors.toSet());
        Map<ServiceDescription, Integer> aliasedServiceDescScore = new ArrayMap<>();
        synchronized (this.mRegistrationCapabilities) {
            this.mRegistrationFeatureTags = imsRegistration;
            this.mRegistrationCapabilities.clear();
            for (final Map.Entry<ServiceDescription, Set<String>> desc : this.mServiceDescriptionFeatureTagMap.entrySet()) {
                boolean found = true;
                Iterator<String> it = desc.getValue().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String tag = it.next();
                    if (!sanitizedTags.contains(tag)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    if (this.mServiceDescriptionPartialMatches.contains(desc.getKey())) {
                        ServiceDescription aliasedDesc = aliasedServiceDescScore.keySet().stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.PublishServiceDescTracker$$ExternalSyntheticLambda4
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj) {
                                boolean isSimilar;
                                isSimilar = PublishServiceDescTracker.isSimilar((ServiceDescription) obj, (ServiceDescription) desc.getKey());
                                return isSimilar;
                            }
                        }).findFirst().orElse(null);
                        if (aliasedDesc != null) {
                            Integer prevEntrySize = aliasedServiceDescScore.get(aliasedDesc);
                            if (prevEntrySize != null && prevEntrySize.intValue() <= desc.getValue().size()) {
                                aliasedServiceDescScore.remove(aliasedDesc);
                                aliasedServiceDescScore.put(desc.getKey(), Integer.valueOf(desc.getValue().size()));
                            }
                        } else {
                            aliasedServiceDescScore.put(desc.getKey(), Integer.valueOf(desc.getValue().size()));
                        }
                    } else {
                        this.mRegistrationCapabilities.add(desc.getKey());
                    }
                }
            }
            this.mRegistrationCapabilities.addAll(aliasedServiceDescScore.keySet());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$updateImsRegistration$1(Set s) {
        return (String) s.iterator().next();
    }

    public Set<ServiceDescription> copyRegistrationCapabilities() {
        ArraySet arraySet;
        synchronized (this.mRegistrationCapabilities) {
            arraySet = new ArraySet(this.mRegistrationCapabilities);
        }
        return arraySet;
    }

    public Set<String> copyRegistrationFeatureTags() {
        ArraySet arraySet;
        synchronized (this.mRegistrationCapabilities) {
            arraySet = new ArraySet(this.mRegistrationFeatureTags);
        }
        return arraySet;
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter pw = new IndentingPrintWriter(printWriter, "  ");
        pw.println(TAG);
        pw.increaseIndent();
        pw.println("ServiceDescription -> Feature Tag Map:");
        pw.increaseIndent();
        for (Map.Entry<ServiceDescription, Set<String>> entry : this.mServiceDescriptionFeatureTagMap.entrySet()) {
            pw.print(entry.getKey());
            pw.print("->");
            pw.println(entry.getValue());
        }
        pw.println();
        pw.decreaseIndent();
        if (!this.mServiceDescriptionPartialMatches.isEmpty()) {
            pw.println("Similar ServiceDescriptions:");
            pw.increaseIndent();
            for (ServiceDescription entry2 : this.mServiceDescriptionPartialMatches) {
                pw.println(entry2);
            }
            pw.decreaseIndent();
        } else {
            pw.println("No Similar ServiceDescriptions:");
        }
        pw.println();
        pw.println("Last IMS registration update:");
        pw.increaseIndent();
        for (String entry3 : this.mRegistrationFeatureTags) {
            pw.println(entry3);
        }
        pw.println();
        pw.decreaseIndent();
        pw.println("Capabilities:");
        pw.increaseIndent();
        for (ServiceDescription entry4 : this.mRegistrationCapabilities) {
            pw.println(entry4);
        }
        pw.println();
        pw.decreaseIndent();
        pw.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSimilar(ServiceDescription a, ServiceDescription b) {
        return a.serviceId.equals(b.serviceId) && a.version.equals(b.version);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String removeInconsistencies(String tag) {
        return tag.toLowerCase().replaceAll("\\s+", "");
    }
}
