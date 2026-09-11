package net.okinawa.mcu.event;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.okinawa.mcu.item.MjolnirItem;

@EventBusSubscriber(modid = "mcu")
public class MjolnirLightningImmunityHandler {

    @SubscribeEvent
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {

        // Make sure the entity being damaged is a player
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Make sure the damage is specifically lightning damage
        if (!event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            return;
        }

        // Check the main hand
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof MjolnirItem) {
            event.setInvulnerable(true);
            return;
        }

        // Check the offhand
        ItemStack offHand = player.getOffhandItem();

        if (offHand.getItem() instanceof MjolnirItem) {
            event.setInvulnerable(true);
        }
    }
}