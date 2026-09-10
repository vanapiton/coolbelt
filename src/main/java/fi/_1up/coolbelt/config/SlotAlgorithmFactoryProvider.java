package fi._1up.coolbelt.config;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.EnumConfigEntryHandler;

import java.lang.reflect.*;
import java.util.function.*;

public class SlotAlgorithmFactoryProvider implements ConfigFactoryProvider {

    private static int toOrdinal(Object value) {
        if (value instanceof Integer ordinal) return ordinal;
        return ((SlotAlgorithm) value).ordinal();
    }

    @Override
    public void provideLoadFactories(
            // What even is this type...
            ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder
    ) {
        immutableBuilder.put(
            SlotAlgorithm.class,
            (id, entry, pField, pObject, mpSynced, enumOrInt, defaultEnum) -> new EnumConfigEntryHandler<>(
                id, entry, pField, pObject, mpSynced,
                toOrdinal(enumOrInt),
                toOrdinal(defaultEnum),
                SlotAlgorithm.class
            )
        );
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(SlotAlgorithm.class, enumEntry -> enumEntry);
    }
}
