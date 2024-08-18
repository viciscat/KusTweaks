package io.github.viciscat.kustweaks.init;

import com.google.common.collect.ImmutableList;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class KusLateLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.late.kus_tweaks.json");
    }
}
