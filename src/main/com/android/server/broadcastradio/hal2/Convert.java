package com.android.server.broadcastradio.hal2;

import android.hardware.broadcastradio.V2_0.AmFmBandRange;
import android.hardware.broadcastradio.V2_0.AmFmRegionConfig;
import android.hardware.broadcastradio.V2_0.DabTableEntry;
import android.hardware.broadcastradio.V2_0.Metadata;
import android.hardware.broadcastradio.V2_0.MetadataKey;
import android.hardware.broadcastradio.V2_0.ProgramFilter;
import android.hardware.broadcastradio.V2_0.ProgramIdentifier;
import android.hardware.broadcastradio.V2_0.ProgramInfo;
import android.hardware.broadcastradio.V2_0.ProgramListChunk;
import android.hardware.broadcastradio.V2_0.Properties;
import android.hardware.broadcastradio.V2_0.Result;
import android.hardware.broadcastradio.V2_0.VendorKeyValue;
import android.hardware.radio.Announcement;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioMetadata;
import android.os.ParcelableException;
import android.util.Slog;
import com.android.server.audio.AudioService$$ExternalSyntheticLambda0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class Convert {
    public static final Map<Integer, MetadataDef> metadataKeys;

    /* loaded from: classes.dex */
    public enum MetadataType {
        INT,
        STRING
    }

    public static int halResultToTunerResult(int i) {
        if (i != 0) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            return i != 6 ? 7 : 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public static int identifierTypeToProgramType(int i) {
        switch (i) {
            case 1:
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
            case 11:
            default:
                if (i < 1000 || i > 1999) {
                    return 0;
                }
                return i;
            case 5:
            case 6:
            case 7:
            case 8:
            case 14:
                return 5;
            case 9:
            case 10:
                return 6;
            case 12:
            case 13:
                return 7;
        }
    }

    public static void throwOnError(String str, int i) {
        String str2 = str + ": " + Result.toString(i);
        switch (i) {
            case 0:
                return;
            case 1:
            case 2:
            case 6:
                throw new ParcelableException(new RuntimeException(str2));
            case 3:
                throw new IllegalArgumentException(str2);
            case 4:
                throw new IllegalStateException(str2);
            case 5:
                throw new UnsupportedOperationException(str2);
            default:
                throw new ParcelableException(new RuntimeException(str + ": unknown error (" + i + ")"));
        }
    }

    public static ArrayList<VendorKeyValue> vendorInfoToHal(Map<String, String> map) {
        if (map == null) {
            return new ArrayList<>();
        }
        ArrayList<VendorKeyValue> arrayList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            VendorKeyValue vendorKeyValue = new VendorKeyValue();
            vendorKeyValue.key = entry.getKey();
            String value = entry.getValue();
            vendorKeyValue.value = value;
            if (vendorKeyValue.key == null || value == null) {
                Slog.w("BcRadio2Srv.convert", "VendorKeyValue contains null pointers");
            } else {
                arrayList.add(vendorKeyValue);
            }
        }
        return arrayList;
    }

    public static Map<String, String> vendorInfoFromHal(List<VendorKeyValue> list) {
        String str;
        if (list == null) {
            return Collections.emptyMap();
        }
        HashMap hashMap = new HashMap();
        for (VendorKeyValue vendorKeyValue : list) {
            String str2 = vendorKeyValue.key;
            if (str2 == null || (str = vendorKeyValue.value) == null) {
                Slog.w("BcRadio2Srv.convert", "VendorKeyValue contains null pointers");
            } else {
                hashMap.put(str2, str);
            }
        }
        return hashMap;
    }

    public static int[] identifierTypesToProgramTypes(int[] iArr) {
        HashSet hashSet = new HashSet();
        for (int i : iArr) {
            int identifierTypeToProgramType = identifierTypeToProgramType(i);
            if (identifierTypeToProgramType != 0) {
                hashSet.add(Integer.valueOf(identifierTypeToProgramType));
                if (identifierTypeToProgramType == 2) {
                    hashSet.add(1);
                }
                if (identifierTypeToProgramType == 4) {
                    hashSet.add(3);
                }
            }
        }
        return hashSet.stream().mapToInt(new AudioService$$ExternalSyntheticLambda0()).toArray();
    }

    public static RadioManager.BandDescriptor[] amfmConfigToBands(AmFmRegionConfig amFmRegionConfig) {
        if (amFmRegionConfig == null) {
            return new RadioManager.BandDescriptor[0];
        }
        ArrayList arrayList = new ArrayList(amFmRegionConfig.ranges.size());
        Iterator<AmFmBandRange> it = amFmRegionConfig.ranges.iterator();
        while (it.hasNext()) {
            AmFmBandRange next = it.next();
            FrequencyBand band = Utils.getBand(next.lowerBound);
            if (band == FrequencyBand.UNKNOWN) {
                Slog.e("BcRadio2Srv.convert", "Unknown frequency band at " + next.lowerBound + "kHz");
            } else if (band == FrequencyBand.FM) {
                arrayList.add(new RadioManager.FmBandDescriptor(0, 1, next.lowerBound, next.upperBound, next.spacing, true, true, true, true, true));
            } else {
                arrayList.add(new RadioManager.AmBandDescriptor(0, 0, next.lowerBound, next.upperBound, next.spacing, true));
            }
        }
        return (RadioManager.BandDescriptor[]) arrayList.toArray(new RadioManager.BandDescriptor[arrayList.size()]);
    }

    public static Map<String, Integer> dabConfigFromHal(List<DabTableEntry> list) {
        if (list == null) {
            return null;
        }
        return (Map) list.stream().collect(Collectors.toMap(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String str;
                str = ((DabTableEntry) obj).label;
                return str;
            }
        }, new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$dabConfigFromHal$1;
                lambda$dabConfigFromHal$1 = Convert.lambda$dabConfigFromHal$1((DabTableEntry) obj);
                return lambda$dabConfigFromHal$1;
            }
        }));
    }

    public static /* synthetic */ Integer lambda$dabConfigFromHal$1(DabTableEntry dabTableEntry) {
        return Integer.valueOf(dabTableEntry.frequency);
    }

    public static RadioManager.ModuleProperties propertiesFromHal(int i, String str, Properties properties, AmFmRegionConfig amFmRegionConfig, List<DabTableEntry> list) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(properties);
        int[] array = properties.supportedIdentifierTypes.stream().mapToInt(new AudioService$$ExternalSyntheticLambda0()).toArray();
        return new RadioManager.ModuleProperties(i, str, 0, properties.maker, properties.product, properties.version, properties.serial, 1, 1, false, false, amfmConfigToBands(amFmRegionConfig), true, identifierTypesToProgramTypes(array), array, dabConfigFromHal(list), vendorInfoFromHal(properties.vendorInfo));
    }

    public static void programIdentifierToHal(ProgramIdentifier programIdentifier, ProgramSelector.Identifier identifier) {
        programIdentifier.type = identifier.getType();
        programIdentifier.value = identifier.getValue();
    }

    public static ProgramIdentifier programIdentifierToHal(ProgramSelector.Identifier identifier) {
        ProgramIdentifier programIdentifier = new ProgramIdentifier();
        programIdentifierToHal(programIdentifier, identifier);
        return programIdentifier;
    }

    public static ProgramSelector.Identifier programIdentifierFromHal(ProgramIdentifier programIdentifier) {
        if (programIdentifier.type == 0) {
            return null;
        }
        return new ProgramSelector.Identifier(programIdentifier.type, programIdentifier.value);
    }

    public static android.hardware.broadcastradio.V2_0.ProgramSelector programSelectorToHal(ProgramSelector programSelector) {
        android.hardware.broadcastradio.V2_0.ProgramSelector programSelector2 = new android.hardware.broadcastradio.V2_0.ProgramSelector();
        programIdentifierToHal(programSelector2.primaryId, programSelector.getPrimaryId());
        Stream map = Arrays.stream(programSelector.getSecondaryIds()).map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Convert.programIdentifierToHal((ProgramSelector.Identifier) obj);
            }
        });
        final ArrayList<ProgramIdentifier> arrayList = programSelector2.secondaryIds;
        Objects.requireNonNull(arrayList);
        map.forEachOrdered(new Consumer() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.add((ProgramIdentifier) obj);
            }
        });
        return programSelector2;
    }

    public static boolean isEmpty(android.hardware.broadcastradio.V2_0.ProgramSelector programSelector) {
        ProgramIdentifier programIdentifier = programSelector.primaryId;
        return programIdentifier.type == 0 && programIdentifier.value == 0 && programSelector.secondaryIds.size() == 0;
    }

    public static ProgramSelector programSelectorFromHal(android.hardware.broadcastradio.V2_0.ProgramSelector programSelector) {
        if (isEmpty(programSelector)) {
            return null;
        }
        int identifierTypeToProgramType = identifierTypeToProgramType(programSelector.primaryId.type);
        ProgramSelector.Identifier programIdentifierFromHal = programIdentifierFromHal(programSelector.primaryId);
        Objects.requireNonNull(programIdentifierFromHal);
        return new ProgramSelector(identifierTypeToProgramType, programIdentifierFromHal, (ProgramSelector.Identifier[]) programSelector.secondaryIds.stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Convert.programIdentifierFromHal((ProgramIdentifier) obj);
            }
        }).map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ProgramSelector.Identifier identifier = (ProgramSelector.Identifier) obj;
                Objects.requireNonNull(identifier);
                return identifier;
            }
        }).toArray(new IntFunction() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda6
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                ProgramSelector.Identifier[] lambda$programSelectorFromHal$2;
                lambda$programSelectorFromHal$2 = Convert.lambda$programSelectorFromHal$2(i);
                return lambda$programSelectorFromHal$2;
            }
        }), (long[]) null);
    }

    public static /* synthetic */ ProgramSelector.Identifier[] lambda$programSelectorFromHal$2(int i) {
        return new ProgramSelector.Identifier[i];
    }

    /* loaded from: classes.dex */
    public static class MetadataDef {
        public String key;
        public MetadataType type;

        public MetadataDef(MetadataType metadataType, String str) {
            this.type = metadataType;
            this.key = str;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        metadataKeys = hashMap;
        MetadataType metadataType = MetadataType.STRING;
        hashMap.put(1, new MetadataDef(metadataType, "android.hardware.radio.metadata.RDS_PS"));
        MetadataType metadataType2 = MetadataType.INT;
        hashMap.put(2, new MetadataDef(metadataType2, "android.hardware.radio.metadata.RDS_PTY"));
        hashMap.put(3, new MetadataDef(metadataType2, "android.hardware.radio.metadata.RBDS_PTY"));
        hashMap.put(4, new MetadataDef(metadataType, "android.hardware.radio.metadata.RDS_RT"));
        hashMap.put(5, new MetadataDef(metadataType, "android.hardware.radio.metadata.TITLE"));
        hashMap.put(6, new MetadataDef(metadataType, "android.hardware.radio.metadata.ARTIST"));
        hashMap.put(7, new MetadataDef(metadataType, "android.hardware.radio.metadata.ALBUM"));
        hashMap.put(8, new MetadataDef(metadataType2, "android.hardware.radio.metadata.ICON"));
        hashMap.put(9, new MetadataDef(metadataType2, "android.hardware.radio.metadata.ART"));
        hashMap.put(10, new MetadataDef(metadataType, "android.hardware.radio.metadata.PROGRAM_NAME"));
        hashMap.put(11, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_ENSEMBLE_NAME"));
        hashMap.put(12, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_ENSEMBLE_NAME_SHORT"));
        hashMap.put(13, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_SERVICE_NAME"));
        hashMap.put(14, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_SERVICE_NAME_SHORT"));
        hashMap.put(15, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_COMPONENT_NAME"));
        hashMap.put(16, new MetadataDef(metadataType, "android.hardware.radio.metadata.DAB_COMPONENT_NAME_SHORT"));
    }

    public static RadioMetadata metadataFromHal(ArrayList<Metadata> arrayList) {
        RadioMetadata.Builder builder = new RadioMetadata.Builder();
        Iterator<Metadata> it = arrayList.iterator();
        while (it.hasNext()) {
            Metadata next = it.next();
            MetadataDef metadataDef = metadataKeys.get(Integer.valueOf(next.key));
            if (metadataDef == null) {
                Slog.i("BcRadio2Srv.convert", "Ignored unknown metadata entry: " + MetadataKey.toString(next.key));
            } else if (metadataDef.type == MetadataType.STRING) {
                builder.putString(metadataDef.key, next.stringValue);
            } else {
                builder.putInt(metadataDef.key, (int) next.intValue);
            }
        }
        return builder.build();
    }

    public static RadioManager.ProgramInfo programInfoFromHal(ProgramInfo programInfo) {
        ProgramSelector programSelectorFromHal = programSelectorFromHal(programInfo.selector);
        Objects.requireNonNull(programSelectorFromHal);
        return new RadioManager.ProgramInfo(programSelectorFromHal, programIdentifierFromHal(programInfo.logicallyTunedTo), programIdentifierFromHal(programInfo.physicallyTunedTo), (Collection) programInfo.relatedContent.stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ProgramSelector.Identifier lambda$programInfoFromHal$3;
                lambda$programInfoFromHal$3 = Convert.lambda$programInfoFromHal$3((ProgramIdentifier) obj);
                return lambda$programInfoFromHal$3;
            }
        }).collect(Collectors.toList()), programInfo.infoFlags, programInfo.signalQuality, metadataFromHal(programInfo.metadata), vendorInfoFromHal(programInfo.vendorInfo));
    }

    public static /* synthetic */ ProgramSelector.Identifier lambda$programInfoFromHal$3(ProgramIdentifier programIdentifier) {
        ProgramSelector.Identifier programIdentifierFromHal = programIdentifierFromHal(programIdentifier);
        Objects.requireNonNull(programIdentifierFromHal);
        return programIdentifierFromHal;
    }

    public static ProgramFilter programFilterToHal(ProgramList.Filter filter) {
        if (filter == null) {
            filter = new ProgramList.Filter();
        }
        final ProgramFilter programFilter = new ProgramFilter();
        Stream stream = filter.getIdentifierTypes().stream();
        final ArrayList<Integer> arrayList = programFilter.identifierTypes;
        Objects.requireNonNull(arrayList);
        stream.forEachOrdered(new Consumer() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.add((Integer) obj);
            }
        });
        filter.getIdentifiers().stream().forEachOrdered(new Consumer() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Convert.lambda$programFilterToHal$4(ProgramFilter.this, (ProgramSelector.Identifier) obj);
            }
        });
        programFilter.includeCategories = filter.areCategoriesIncluded();
        programFilter.excludeModifications = filter.areModificationsExcluded();
        return programFilter;
    }

    public static /* synthetic */ void lambda$programFilterToHal$4(ProgramFilter programFilter, ProgramSelector.Identifier identifier) {
        programFilter.identifiers.add(programIdentifierToHal(identifier));
    }

    public static ProgramList.Chunk programListChunkFromHal(ProgramListChunk programListChunk) {
        return new ProgramList.Chunk(programListChunk.purge, programListChunk.complete, (Set) programListChunk.modified.stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                RadioManager.ProgramInfo programInfoFromHal;
                programInfoFromHal = Convert.programInfoFromHal((ProgramInfo) obj);
                return programInfoFromHal;
            }
        }).collect(Collectors.toSet()), (Set) programListChunk.removed.stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.Convert$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ProgramSelector.Identifier lambda$programListChunkFromHal$6;
                lambda$programListChunkFromHal$6 = Convert.lambda$programListChunkFromHal$6((ProgramIdentifier) obj);
                return lambda$programListChunkFromHal$6;
            }
        }).collect(Collectors.toSet()));
    }

    public static /* synthetic */ ProgramSelector.Identifier lambda$programListChunkFromHal$6(ProgramIdentifier programIdentifier) {
        ProgramSelector.Identifier programIdentifierFromHal = programIdentifierFromHal(programIdentifier);
        Objects.requireNonNull(programIdentifierFromHal);
        return programIdentifierFromHal;
    }

    public static Announcement announcementFromHal(android.hardware.broadcastradio.V2_0.Announcement announcement) {
        ProgramSelector programSelectorFromHal = programSelectorFromHal(announcement.selector);
        Objects.requireNonNull(programSelectorFromHal);
        return new Announcement(programSelectorFromHal, announcement.type, vendorInfoFromHal(announcement.vendorInfo));
    }

    public static <T> ArrayList<T> listToArrayList(List<T> list) {
        if (list == null) {
            return null;
        }
        return list instanceof ArrayList ? (ArrayList) list : new ArrayList<>(list);
    }
}
