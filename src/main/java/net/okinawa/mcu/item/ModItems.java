package net.okinawa.mcu.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.okinawa.mcu.Mcu;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Mcu.MODID);

    public static final DeferredItem<MjolnirItem> MJOLNIR =
            ITEMS.registerItem("mjolnir", MjolnirItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}