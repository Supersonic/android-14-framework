package android.content.p001pm;

import android.app.Person;
import android.app.appsearch.AppSearchSchema;
import android.app.appsearch.GenericDocument;
import android.content.ComponentName;
import android.content.Intent;
import android.content.LocusId;
import android.content.p001pm.AppSearchShortcutInfo;
import android.p008os.Bundle;
import android.p008os.PersistableBundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.util.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
/* renamed from: android.content.pm.AppSearchShortcutInfo */
/* loaded from: classes.dex */
public class AppSearchShortcutInfo extends GenericDocument {
    public static final String IS_DISABLED = "Dis";
    public static final String IS_DYNAMIC = "Dyn";
    public static final String IS_IMMUTABLE = "Im";
    public static final String IS_MANIFEST = "Man";
    public static final String KEY_ACTIVITY = "activity";
    public static final String KEY_FLAGS = "flags";
    public static final String KEY_ICON_RES_ID = "iconResId";
    public static final String KEY_PERSON = "person";
    public static final String NOT_DISABLED = "nDis";
    public static final String NOT_DYNAMIC = "nDyn";
    public static final String NOT_IMMUTABLE = "nIm";
    public static final String NOT_MANIFEST = "nMan";
    public static final int SCHEMA_VERSION = 3;
    public static final long SHORTCUT_TTL = TimeUnit.DAYS.toMillis(90);
    public static final String SCHEMA_TYPE = "Shortcut";
    public static final String KEY_SHORT_LABEL = "shortLabel";
    public static final String KEY_LONG_LABEL = "longLabel";
    public static final String KEY_DISABLED_MESSAGE = "disabledMessage";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_INTENTS = "intents";
    public static final String KEY_INTENT_PERSISTABLE_EXTRAS = "intentPersistableExtras";
    public static final String KEY_LOCUS_ID = "locusId";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_ICON_RES_NAME = "iconResName";
    public static final String KEY_ICON_URI = "iconUri";
    public static final String KEY_DISABLED_REASON = "disabledReason";
    public static final String KEY_CAPABILITY = "capability";
    public static final String KEY_CAPABILITY_BINDINGS = "capabilityBindings";
    public static final AppSearchSchema SCHEMA = new AppSearchSchema.Builder(SCHEMA_TYPE).addProperty(new AppSearchSchema.StringPropertyConfig.Builder("activity").setCardinality(2).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_SHORT_LABEL).setCardinality(2).setTokenizerType(1).setIndexingType(2).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_LONG_LABEL).setCardinality(2).setTokenizerType(1).setIndexingType(2).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_DISABLED_MESSAGE).setCardinality(2).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_CATEGORIES).setCardinality(1).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_INTENTS).setCardinality(1).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.BytesPropertyConfig.Builder(KEY_INTENT_PERSISTABLE_EXTRAS).setCardinality(1).build()).addProperty(new AppSearchSchema.DocumentPropertyConfig.Builder("person", AppSearchShortcutPerson.SCHEMA_TYPE).setCardinality(1).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_LOCUS_ID).setCardinality(2).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.BytesPropertyConfig.Builder(KEY_EXTRAS).setCardinality(2).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder("flags").setCardinality(1).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.LongPropertyConfig.Builder("iconResId").setCardinality(2).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_ICON_RES_NAME).setCardinality(2).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_ICON_URI).setCardinality(2).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_DISABLED_REASON).setCardinality(3).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_CAPABILITY).setCardinality(1).setTokenizerType(1).setIndexingType(1).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder(KEY_CAPABILITY_BINDINGS).setCardinality(1).setTokenizerType(1).setIndexingType(2).build()).build();

    public AppSearchShortcutInfo(GenericDocument document) {
        super(document);
    }

    public static AppSearchShortcutInfo instance(ShortcutInfo shortcutInfo) {
        Objects.requireNonNull(shortcutInfo);
        return new Builder(shortcutInfo.getPackage(), shortcutInfo.getId()).setActivity(shortcutInfo.getActivity()).setShortLabel(shortcutInfo.getShortLabel()).setLongLabel(shortcutInfo.getLongLabel()).setDisabledMessage(shortcutInfo.getDisabledMessage()).setCategories(shortcutInfo.getCategories()).setIntents(shortcutInfo.getIntents()).setExtras(shortcutInfo.getExtras()).setCreationTimestampMillis(shortcutInfo.getLastChangedTimestamp()).setFlags(shortcutInfo.getFlags()).setIconResId(shortcutInfo.getIconResourceId()).setIconResName(shortcutInfo.getIconResName()).setIconUri(shortcutInfo.getIconUri()).setDisabledReason(shortcutInfo.getDisabledReason()).setPersons(shortcutInfo.getPersons()).setLocusId(shortcutInfo.getLocusId()).setCapabilityBindings(shortcutInfo.getCapabilityBindingsInternal()).setTtlMillis(SHORTCUT_TTL).build();
    }

    public ShortcutInfo toShortcutInfo(int userId) {
        Intent[] intentArr;
        Bundle[] bundleArr;
        int disabledReason;
        String packageName = getNamespace();
        String activityString = getPropertyString("activity");
        ComponentName activity = activityString == null ? null : ComponentName.unflattenFromString(activityString);
        String shortLabel = getPropertyString(KEY_SHORT_LABEL);
        String longLabel = getPropertyString(KEY_LONG_LABEL);
        String disabledMessage = getPropertyString(KEY_DISABLED_MESSAGE);
        String[] categories = getPropertyStringArray(KEY_CATEGORIES);
        Set<String> categoriesSet = categories == null ? null : new ArraySet<>(Arrays.asList(categories));
        String[] intentsStrings = getPropertyStringArray(KEY_INTENTS);
        if (intentsStrings == null) {
            intentArr = new Intent[0];
        } else {
            intentArr = (Intent[]) Arrays.stream(intentsStrings).map(new Function() { // from class: android.content.pm.AppSearchShortcutInfo$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AppSearchShortcutInfo.lambda$toShortcutInfo$0((String) obj);
                }
            }).toArray(new IntFunction() { // from class: android.content.pm.AppSearchShortcutInfo$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return AppSearchShortcutInfo.lambda$toShortcutInfo$1(i);
                }
            });
        }
        Intent[] intents = intentArr;
        byte[][] intentExtrasesBytes = getPropertyBytesArray(KEY_INTENT_PERSISTABLE_EXTRAS);
        if (intentExtrasesBytes == null) {
            bundleArr = null;
        } else {
            bundleArr = (Bundle[]) Arrays.stream(intentExtrasesBytes).map(new Function() { // from class: android.content.pm.AppSearchShortcutInfo$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle transformToBundle;
                    transformToBundle = AppSearchShortcutInfo.this.transformToBundle((byte[]) obj);
                    return transformToBundle;
                }
            }).toArray(new IntFunction() { // from class: android.content.pm.AppSearchShortcutInfo$$ExternalSyntheticLambda4
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return AppSearchShortcutInfo.lambda$toShortcutInfo$2(i);
                }
            });
        }
        Bundle[] intentExtrases = bundleArr;
        if (intents != null) {
            for (int i = 0; i < intents.length; i++) {
                Intent intent = intents[i];
                if (intent != null && intentExtrases != null && intentExtrases.length > i && intentExtrases[i] != null && intentExtrases[i].size() != 0) {
                    intent.replaceExtras(intentExtrases[i]);
                }
            }
        }
        Person[] persons = parsePerson(getPropertyDocumentArray("person"));
        String locusIdString = getPropertyString(KEY_LOCUS_ID);
        LocusId locusId = locusIdString != null ? new LocusId(locusIdString) : null;
        byte[] extrasByte = getPropertyBytes(KEY_EXTRAS);
        PersistableBundle extras = transformToPersistableBundle(extrasByte);
        int flags = parseFlags(getPropertyStringArray("flags"));
        int iconResId = (int) getPropertyLong("iconResId");
        String iconResName = getPropertyString(KEY_ICON_RES_NAME);
        String iconUri = getPropertyString(KEY_ICON_URI);
        String disabledReasonString = getPropertyString(KEY_DISABLED_REASON);
        if (!TextUtils.isEmpty(disabledReasonString)) {
            disabledReason = Integer.parseInt(getPropertyString(KEY_DISABLED_REASON));
        } else {
            disabledReason = 0;
        }
        Map<String, Map<String, List<String>>> capabilityBindings = parseCapabilityBindings(getPropertyStringArray(KEY_CAPABILITY_BINDINGS));
        return new ShortcutInfo(userId, getId(), packageName, activity, null, shortLabel, 0, null, longLabel, 0, null, disabledMessage, 0, null, categoriesSet, intents, Integer.MAX_VALUE, extras, getCreationTimestampMillis(), flags, iconResId, iconResName, null, iconUri, disabledReason, persons, locusId, null, capabilityBindings);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Intent lambda$toShortcutInfo$0(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return new Intent("android.intent.action.VIEW");
        }
        try {
            return Intent.parseUri(uri, 0);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Intent[] lambda$toShortcutInfo$1(int x$0) {
        return new Intent[x$0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Bundle[] lambda$toShortcutInfo$2(int x$0) {
        return new Bundle[x$0];
    }

    public static List<GenericDocument> toGenericDocuments(Collection<ShortcutInfo> shortcuts) {
        List<GenericDocument> docs = new ArrayList<>(shortcuts.size());
        for (ShortcutInfo si : shortcuts) {
            docs.add(instance(si));
        }
        return docs;
    }

    /* renamed from: android.content.pm.AppSearchShortcutInfo$Builder */
    /* loaded from: classes.dex */
    public static class Builder extends GenericDocument.Builder<Builder> {
        private List<String> mFlags;

        public Builder(String packageName, String id) {
            super(packageName, id, AppSearchShortcutInfo.SCHEMA_TYPE);
            this.mFlags = new ArrayList(1);
        }

        public Builder setLocusId(LocusId locusId) {
            if (locusId != null) {
                setPropertyString(AppSearchShortcutInfo.KEY_LOCUS_ID, locusId.getId());
            }
            return this;
        }

        public Builder setActivity(ComponentName activity) {
            if (activity != null) {
                setPropertyString("activity", activity.flattenToShortString());
            }
            return this;
        }

        public Builder setShortLabel(CharSequence shortLabel) {
            if (!TextUtils.isEmpty(shortLabel)) {
                setPropertyString(AppSearchShortcutInfo.KEY_SHORT_LABEL, Preconditions.checkStringNotEmpty(shortLabel, "shortLabel cannot be empty").toString());
            }
            return this;
        }

        public Builder setLongLabel(CharSequence longLabel) {
            if (!TextUtils.isEmpty(longLabel)) {
                setPropertyString(AppSearchShortcutInfo.KEY_LONG_LABEL, Preconditions.checkStringNotEmpty(longLabel, "longLabel cannot be empty").toString());
            }
            return this;
        }

        public Builder setDisabledMessage(CharSequence disabledMessage) {
            if (!TextUtils.isEmpty(disabledMessage)) {
                setPropertyString(AppSearchShortcutInfo.KEY_DISABLED_MESSAGE, Preconditions.checkStringNotEmpty(disabledMessage, "disabledMessage cannot be empty").toString());
            }
            return this;
        }

        public Builder setCategories(Set<String> categories) {
            if (categories != null && !categories.isEmpty()) {
                setPropertyString(AppSearchShortcutInfo.KEY_CATEGORIES, (String[]) categories.stream().toArray(new IntFunction() { // from class: android.content.pm.AppSearchShortcutInfo$Builder$$ExternalSyntheticLambda0
                    @Override // java.util.function.IntFunction
                    public final Object apply(int i) {
                        return AppSearchShortcutInfo.Builder.lambda$setCategories$0(i);
                    }
                }));
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ String[] lambda$setCategories$0(int x$0) {
            return new String[x$0];
        }

        public Builder setIntent(Intent intent) {
            if (intent == null) {
                return this;
            }
            return setIntents(new Intent[]{intent});
        }

        public Builder setIntents(Intent[] intents) {
            if (intents == null || intents.length == 0) {
                return this;
            }
            for (Intent intent : intents) {
                Objects.requireNonNull(intent, "intents cannot contain null");
                Objects.requireNonNull(intent.getAction(), "intent's action must be set");
            }
            byte[][] intentExtrases = new byte[intents.length];
            for (int i = 0; i < intents.length; i++) {
                Bundle extras = intents[i].getExtras();
                intentExtrases[i] = extras == null ? new byte[0] : AppSearchShortcutInfo.transformToByteArray(new PersistableBundle(extras));
            }
            setPropertyString(AppSearchShortcutInfo.KEY_INTENTS, (String[]) Arrays.stream(intents).map(new Function() { // from class: android.content.pm.AppSearchShortcutInfo$Builder$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String uri;
                    uri = ((Intent) obj).toUri(0);
                    return uri;
                }
            }).toArray(new IntFunction() { // from class: android.content.pm.AppSearchShortcutInfo$Builder$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i2) {
                    return AppSearchShortcutInfo.Builder.lambda$setIntents$2(i2);
                }
            }));
            setPropertyBytes(AppSearchShortcutInfo.KEY_INTENT_PERSISTABLE_EXTRAS, intentExtrases);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ String[] lambda$setIntents$2(int x$0) {
            return new String[x$0];
        }

        public Builder setPerson(Person person) {
            if (person == null) {
                return this;
            }
            return setPersons(new Person[]{person});
        }

        public Builder setPersons(Person[] persons) {
            if (persons == null || persons.length == 0) {
                return this;
            }
            GenericDocument[] documents = new GenericDocument[persons.length];
            for (int i = 0; i < persons.length; i++) {
                Person person = persons[i];
                if (person != null) {
                    AppSearchShortcutPerson personEntity = AppSearchShortcutPerson.instance(person);
                    documents[i] = personEntity;
                }
            }
            setPropertyDocument("person", documents);
            return this;
        }

        public Builder setExtras(PersistableBundle extras) {
            if (extras != null) {
                setPropertyBytes(AppSearchShortcutInfo.KEY_EXTRAS, AppSearchShortcutInfo.transformToByteArray(extras));
            }
            return this;
        }

        public Builder setFlags(int flags) {
            String[] flagArray = AppSearchShortcutInfo.flattenFlags(flags);
            if (flagArray != null && flagArray.length > 0) {
                this.mFlags.addAll(Arrays.asList(flagArray));
            }
            return this;
        }

        public Builder setIconResId(int iconResId) {
            setPropertyLong("iconResId", iconResId);
            return this;
        }

        public Builder setIconResName(String iconResName) {
            if (!TextUtils.isEmpty(iconResName)) {
                setPropertyString(AppSearchShortcutInfo.KEY_ICON_RES_NAME, iconResName);
            }
            return this;
        }

        public Builder setIconUri(String iconUri) {
            if (!TextUtils.isEmpty(iconUri)) {
                setPropertyString(AppSearchShortcutInfo.KEY_ICON_URI, iconUri);
            }
            return this;
        }

        public Builder setDisabledReason(int disabledReason) {
            setPropertyString(AppSearchShortcutInfo.KEY_DISABLED_REASON, String.valueOf(disabledReason));
            return this;
        }

        public Builder setCapabilityBindings(Map<String, Map<String, List<String>>> bindings) {
            if (bindings != null && !bindings.isEmpty()) {
                Set<String> capabilityNames = bindings.keySet();
                final Set<String> capabilityBindings = new ArraySet<>(1);
                for (final String capabilityName : capabilityNames) {
                    Map<String, List<String>> params = bindings.get(capabilityName);
                    for (final String paramName : params.keySet()) {
                        Stream<R> map = params.get(paramName).stream().map(new Function() { // from class: android.content.pm.AppSearchShortcutInfo$Builder$$ExternalSyntheticLambda3
                            @Override // java.util.function.Function
                            public final Object apply(Object obj) {
                                return AppSearchShortcutInfo.Builder.lambda$setCapabilityBindings$3(capabilityName, paramName, (String) obj);
                            }
                        });
                        Objects.requireNonNull(capabilityBindings);
                        map.forEach(new Consumer() { // from class: android.content.pm.AppSearchShortcutInfo$Builder$$ExternalSyntheticLambda4
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                capabilityBindings.add((String) obj);
                            }
                        });
                    }
                }
                setPropertyString(AppSearchShortcutInfo.KEY_CAPABILITY, (String[]) capabilityNames.toArray(new String[0]));
                setPropertyString(AppSearchShortcutInfo.KEY_CAPABILITY_BINDINGS, (String[]) capabilityBindings.toArray(new String[0]));
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ String lambda$setCapabilityBindings$3(String capabilityName, String paramName, String v) {
            return capabilityName + "/" + paramName + "/" + v;
        }

        @Override // android.app.appsearch.GenericDocument.Builder
        public AppSearchShortcutInfo build() {
            setPropertyString("flags", (String[]) this.mFlags.toArray(new String[0]));
            return new AppSearchShortcutInfo(super.build());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] transformToByteArray(PersistableBundle extras) {
        Objects.requireNonNull(extras);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new PersistableBundle(extras).writeToStream(baos);
            byte[] byteArray = baos.toByteArray();
            baos.close();
            return byteArray;
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bundle transformToBundle(byte[] extras) {
        if (extras == null) {
            return null;
        }
        Objects.requireNonNull(extras);
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(extras);
            Bundle ret = new Bundle();
            ret.putAll(PersistableBundle.readFromStream(bais));
            bais.close();
            return ret;
        } catch (IOException e) {
            return null;
        }
    }

    private PersistableBundle transformToPersistableBundle(byte[] extras) {
        if (extras == null) {
            return null;
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(extras);
            PersistableBundle readFromStream = PersistableBundle.readFromStream(bais);
            bais.close();
            return readFromStream;
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String[] flattenFlags(int flags) {
        List<String> flattenedFlags = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            int mask = 1 << i;
            String value = flagToString(flags, mask);
            if (value != null) {
                flattenedFlags.add(value);
            }
        }
        return (String[]) flattenedFlags.toArray(new String[0]);
    }

    private static String flagToString(int flags, int mask) {
        switch (mask) {
            case 1:
                return (flags & mask) != 0 ? IS_DYNAMIC : NOT_DYNAMIC;
            case 32:
                return (flags & mask) != 0 ? IS_MANIFEST : NOT_MANIFEST;
            case 64:
                return (flags & mask) != 0 ? IS_DISABLED : NOT_DISABLED;
            case 256:
                return (flags & mask) != 0 ? IS_IMMUTABLE : NOT_IMMUTABLE;
            default:
                return null;
        }
    }

    private static int parseFlags(String[] flags) {
        if (flags == null) {
            return 0;
        }
        int ret = 0;
        for (String str : flags) {
            ret |= parseFlag(str);
        }
        return ret;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int parseFlag(String value) {
        char c;
        switch (value.hashCode()) {
            case 2372:
                if (value.equals(IS_IMMUTABLE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 68718:
                if (value.equals(IS_DISABLED)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 69209:
                if (value.equals(IS_DYNAMIC)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 77114:
                if (value.equals(IS_MANIFEST)) {
                    c = 1;
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
                return 1;
            case 1:
                return 32;
            case 2:
                return 64;
            case 3:
                return 256;
            default:
                return 0;
        }
    }

    private static Person[] parsePerson(GenericDocument[] persons) {
        if (persons == null) {
            return new Person[0];
        }
        Person[] ret = new Person[persons.length];
        for (int i = 0; i < persons.length; i++) {
            GenericDocument document = persons[i];
            if (document != null) {
                AppSearchShortcutPerson person = new AppSearchShortcutPerson(document);
                ret[i] = person.toPerson();
            }
        }
        return ret;
    }

    private static Map<String, Map<String, List<String>>> parseCapabilityBindings(String[] capabilityBindings) {
        if (capabilityBindings == null || capabilityBindings.length == 0) {
            return null;
        }
        final Map<String, Map<String, List<String>>> ret = new ArrayMap<>(1);
        Arrays.stream(capabilityBindings).forEach(new Consumer() { // from class: android.content.pm.AppSearchShortcutInfo$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AppSearchShortcutInfo.lambda$parseCapabilityBindings$3(ret, (String) obj);
            }
        });
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$parseCapabilityBindings$3(Map ret, String binding) {
        int capabilityStopIndex;
        if (TextUtils.isEmpty(binding) || (capabilityStopIndex = binding.indexOf("/")) == -1 || capabilityStopIndex == binding.length() - 1) {
            return;
        }
        String capabilityName = binding.substring(0, capabilityStopIndex);
        int paramStopIndex = binding.indexOf("/", capabilityStopIndex + 1);
        if (paramStopIndex == -1 || paramStopIndex == binding.length() - 1) {
            return;
        }
        String paramName = binding.substring(capabilityStopIndex + 1, paramStopIndex);
        String paramValue = binding.substring(paramStopIndex + 1);
        if (!ret.containsKey(capabilityName)) {
            ret.put(capabilityName, new ArrayMap(1));
        }
        Map<String, List<String>> params = (Map) ret.get(capabilityName);
        if (!params.containsKey(paramName)) {
            params.put(paramName, new ArrayList<>(1));
        }
        params.get(paramName).add(paramValue);
    }
}
