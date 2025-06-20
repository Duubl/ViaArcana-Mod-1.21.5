package com.duubl.via_arcana.items;

import java.util.function.Supplier;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.items.accessories.*;
import com.duubl.via_arcana.items.weapons.magic.*;
import com.duubl.via_arcana.items.weapons.melee.TheSlab;
import com.duubl.via_arcana.items.materials.ManaCrystal;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ViaArcana.MODID);

    // Accessories
    public static final DeferredItem<HasteTreads> HASTE_TREADS = ITEMS.registerItem("haste_treads", HasteTreads::new);
    public static final DeferredItem<MoonShoes> MOON_SHOES = ITEMS.registerItem("moon_shoes", MoonShoes::new);
    public static final DeferredItem<MoonstriderBoots> MOONSTRIDER_BOOTS = ITEMS.registerItem("moonstrider_boots", MoonstriderBoots::new);
    public static final DeferredItem<SnowShoes> SNOW_SHOES = ITEMS.registerItem("snow_shoes", SnowShoes::new);
    public static final DeferredItem<AgilityScarf> AGILITY_SCARF = ITEMS.registerItem("agility_scarf", AgilityScarf::new);
    public static final DeferredItem<Item> MANA_SHARD = ITEMS.registerItem("mana_shard", Item::new);
    public static final DeferredItem<ManaCrystal> MANA_CRYSTAL = ITEMS.registerItem("mana_crystal", ManaCrystal::new);

    // Weapons
    // Magic
    public static final DeferredItem<WandOfSplinters> WAND_OF_SPLINTERS = ITEMS.registerItem("wand_of_splinters", WandOfSplinters::new);

    // Melee
    public static final Supplier<TheSlab> THE_SLAB = ITEMS.registerItem("the_slab", TheSlab::new);
}
