package com.kotakotik.coolcreateaddon.register;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import mod.kotakotik.coolcreateaddon.BuildConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItems {
    public static ItemGroup itemGroup = new ItemGroup(BuildConfig.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AllItems.WRENCH.get());
        }
    };

    public static ItemEntry<Item> COAL_POWDER;
    public static ItemEntry<Item> STEEL_POWDER;
    public static ItemEntry<Item> STEEL_INGOT;
    public static ItemEntry<Item> STEEL_NUGGET;
    public static ItemEntry<Item> STEEL_PLATE;

    public static ItemEntry<Item> ANORTHITE_CRYSTAL;
    public static ItemEntry<Item> ANORTHITE_POWDER;

    public static ItemEntry<Item> ALLUMINUM_INGOT;
    public static ItemEntry<Item> ALLUMINUM_NUGGET;
    public static ItemEntry<Item> ALLUMINUM_PLATE;

    public static ItemEntry<Item> PURPLE_QUARTZ;
    public static ItemEntry<Item> POLISHED_PURPLE_QUARTZ;
    public static ItemEntry<Item> INTEGRATED_GUIDANCE_CIRCUIT;

    public static ItemEntry<Item> BLUE_QUARTZ;
    public static ItemEntry<Item> POLISHED_BLUE_QUARTZ;
    public static ItemEntry<Item> INTEGRATED_LSS_CIRCUIT;

    public static void register(CreateRegistrate registrate) {
        registrate.itemGroup(()->itemGroup, BuildConfig.DISPLAY_NAME);
    }
}
