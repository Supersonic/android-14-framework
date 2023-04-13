package com.android.internal.telephony.phonenumbers.metadata;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.metadata.init.ClassPathResourceMetadataLoader;
import com.android.internal.telephony.phonenumbers.metadata.init.MetadataParser;
import com.android.internal.telephony.phonenumbers.metadata.source.FormattingMetadataSource;
import com.android.internal.telephony.phonenumbers.metadata.source.FormattingMetadataSourceImpl;
import com.android.internal.telephony.phonenumbers.metadata.source.MetadataSource;
import com.android.internal.telephony.phonenumbers.metadata.source.MetadataSourceImpl;
import com.android.internal.telephony.phonenumbers.metadata.source.MultiFileModeFileNameProvider;
import com.android.internal.telephony.phonenumbers.metadata.source.PhoneMetadataFileNameProvider;
import com.android.internal.telephony.phonenumbers.metadata.source.RegionMetadataSource;
import com.android.internal.telephony.phonenumbers.metadata.source.RegionMetadataSourceImpl;
/* loaded from: classes.dex */
public final class DefaultMetadataDependenciesProvider {
    private static final DefaultMetadataDependenciesProvider INSTANCE = new DefaultMetadataDependenciesProvider();
    private final PhoneMetadataFileNameProvider alternateFormatsMetadataFileNameProvider;
    private final FormattingMetadataSource alternateFormatsMetadataSource;
    private final MetadataLoader metadataLoader;
    private final MetadataParser metadataParser;
    private final PhoneMetadataFileNameProvider phoneNumberMetadataFileNameProvider;
    private final MetadataSource phoneNumberMetadataSource;
    private final PhoneMetadataFileNameProvider shortNumberMetadataFileNameProvider;
    private final RegionMetadataSource shortNumberMetadataSource;

    public String getCarrierDataDirectory() {
        return "/com/android/internal/telephony/phonenumbers/carrier/data/";
    }

    public String getGeocodingDataDirectory() {
        return "/com/android/internal/telephony/phonenumbers/geocoding/data/";
    }

    public static DefaultMetadataDependenciesProvider getInstance() {
        return INSTANCE;
    }

    private DefaultMetadataDependenciesProvider() {
        MetadataParser newLenientParser = MetadataParser.newLenientParser();
        this.metadataParser = newLenientParser;
        ClassPathResourceMetadataLoader classPathResourceMetadataLoader = new ClassPathResourceMetadataLoader();
        this.metadataLoader = classPathResourceMetadataLoader;
        MultiFileModeFileNameProvider multiFileModeFileNameProvider = new MultiFileModeFileNameProvider("/com/android/internal/telephony/phonenumbers/data/PhoneNumberMetadataProto");
        this.phoneNumberMetadataFileNameProvider = multiFileModeFileNameProvider;
        this.phoneNumberMetadataSource = new MetadataSourceImpl(multiFileModeFileNameProvider, classPathResourceMetadataLoader, newLenientParser);
        MultiFileModeFileNameProvider multiFileModeFileNameProvider2 = new MultiFileModeFileNameProvider("/com/android/internal/telephony/phonenumbers/data/ShortNumberMetadataProto");
        this.shortNumberMetadataFileNameProvider = multiFileModeFileNameProvider2;
        this.shortNumberMetadataSource = new RegionMetadataSourceImpl(multiFileModeFileNameProvider2, classPathResourceMetadataLoader, newLenientParser);
        MultiFileModeFileNameProvider multiFileModeFileNameProvider3 = new MultiFileModeFileNameProvider("/com/android/internal/telephony/phonenumbers/data/PhoneNumberAlternateFormatsProto");
        this.alternateFormatsMetadataFileNameProvider = multiFileModeFileNameProvider3;
        this.alternateFormatsMetadataSource = new FormattingMetadataSourceImpl(multiFileModeFileNameProvider3, classPathResourceMetadataLoader, newLenientParser);
    }

    public MetadataParser getMetadataParser() {
        return this.metadataParser;
    }

    public MetadataLoader getMetadataLoader() {
        return this.metadataLoader;
    }

    public PhoneMetadataFileNameProvider getPhoneNumberMetadataFileNameProvider() {
        return this.phoneNumberMetadataFileNameProvider;
    }

    public MetadataSource getPhoneNumberMetadataSource() {
        return this.phoneNumberMetadataSource;
    }

    public PhoneMetadataFileNameProvider getShortNumberMetadataFileNameProvider() {
        return this.shortNumberMetadataFileNameProvider;
    }

    public RegionMetadataSource getShortNumberMetadataSource() {
        return this.shortNumberMetadataSource;
    }

    public PhoneMetadataFileNameProvider getAlternateFormatsMetadataFileNameProvider() {
        return this.alternateFormatsMetadataFileNameProvider;
    }

    public FormattingMetadataSource getAlternateFormatsMetadataSource() {
        return this.alternateFormatsMetadataSource;
    }
}
