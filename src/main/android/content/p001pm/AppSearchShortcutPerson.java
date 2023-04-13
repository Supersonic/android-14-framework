package android.content.p001pm;

import android.app.Person;
import android.app.appsearch.AppSearchSchema;
import android.app.appsearch.GenericDocument;
import android.graphics.drawable.Icon;
import android.net.UriCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
/* renamed from: android.content.pm.AppSearchShortcutPerson */
/* loaded from: classes.dex */
public class AppSearchShortcutPerson extends GenericDocument {
    private static final String KEY_ICON = "icon";
    private static final String KEY_KEY = "key";
    private static final String KEY_NAME = "name";
    public static final String SCHEMA_TYPE = "ShortcutPerson";
    private static final String KEY_IS_BOT = "isBot";
    private static final String KEY_IS_IMPORTANT = "isImportant";
    public static final AppSearchSchema SCHEMA = new AppSearchSchema.Builder(SCHEMA_TYPE).addProperty(new AppSearchSchema.StringPropertyConfig.Builder("name").setCardinality(2).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.StringPropertyConfig.Builder("key").setCardinality(2).setTokenizerType(0).setIndexingType(0).build()).addProperty(new AppSearchSchema.BooleanPropertyConfig.Builder(KEY_IS_BOT).setCardinality(3).build()).addProperty(new AppSearchSchema.BooleanPropertyConfig.Builder(KEY_IS_IMPORTANT).setCardinality(3).build()).addProperty(new AppSearchSchema.BytesPropertyConfig.Builder("icon").setCardinality(2).build()).build();

    public AppSearchShortcutPerson(GenericDocument document) {
        super(document);
    }

    public static AppSearchShortcutPerson instance(Person person) {
        String id;
        Objects.requireNonNull(person);
        if (person.getUri() != null) {
            id = person.getUri();
        } else {
            id = UUID.randomUUID().toString();
        }
        return new Builder(id).setName(person.getName()).setKey(person.getKey()).setIsBot(person.isBot()).setIsImportant(person.isImportant()).setIcon(transformToByteArray(person.getIcon())).build();
    }

    public Person toPerson() {
        String uri;
        try {
            uri = UriCodec.decode(getId(), false, StandardCharsets.UTF_8, true);
        } catch (IllegalArgumentException e) {
            uri = null;
        }
        return new Person.Builder().setName(getPropertyString("name")).setUri(uri).setKey(getPropertyString("key")).setBot(getPropertyBoolean(KEY_IS_BOT)).setImportant(getPropertyBoolean(KEY_IS_IMPORTANT)).setIcon(transformToIcon(getPropertyBytes("icon"))).build();
    }

    /* renamed from: android.content.pm.AppSearchShortcutPerson$Builder */
    /* loaded from: classes.dex */
    public static class Builder extends GenericDocument.Builder<Builder> {
        public Builder(String id) {
            super("", id, AppSearchShortcutPerson.SCHEMA_TYPE);
        }

        public Builder setName(CharSequence name) {
            if (name != null) {
                setPropertyString("name", name.toString());
            }
            return this;
        }

        public Builder setKey(String key) {
            if (key != null) {
                setPropertyString("key", key);
            }
            return this;
        }

        public Builder setIsBot(boolean isBot) {
            setPropertyBoolean(AppSearchShortcutPerson.KEY_IS_BOT, isBot);
            return this;
        }

        public Builder setIsImportant(boolean isImportant) {
            setPropertyBoolean(AppSearchShortcutPerson.KEY_IS_IMPORTANT, isImportant);
            return this;
        }

        public Builder setIcon(byte[] icon) {
            if (icon != null) {
                setPropertyBytes("icon", icon);
            }
            return this;
        }

        @Override // android.app.appsearch.GenericDocument.Builder
        public AppSearchShortcutPerson build() {
            return new AppSearchShortcutPerson(super.build());
        }
    }

    private static byte[] transformToByteArray(Icon icon) {
        if (icon == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            icon.writeToStream(baos);
            byte[] byteArray = baos.toByteArray();
            baos.close();
            return byteArray;
        } catch (IOException e) {
            return null;
        }
    }

    private Icon transformToIcon(byte[] icon) {
        if (icon == null) {
            return null;
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(icon);
            Icon createFromStream = Icon.createFromStream(bais);
            bais.close();
            return createFromStream;
        } catch (IOException e) {
            return null;
        }
    }
}
