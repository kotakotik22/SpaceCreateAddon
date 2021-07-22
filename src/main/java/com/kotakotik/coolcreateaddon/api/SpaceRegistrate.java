package com.kotakotik.coolcreateaddon.api;

import com.kotakotik.coolcreateaddon.api.registrate.dimension.SpaceDimensionBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import mod.kotakotik.coolcreateaddon.BuildConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class SpaceRegistrate extends CreateRegistrate {
    protected SpaceRegistrate(String modid) {
        super(modid);
    }

    public static CreateRegistrate create(String modid) {
        return new SpaceRegistrate(modid).registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static CreateRegistrate create() {
        return create(BuildConfig.MODID);
    }


    public SpaceDimensionBuilder dimension(String name) {
        return new SpaceDimensionBuilder(this, name);
    }
}
