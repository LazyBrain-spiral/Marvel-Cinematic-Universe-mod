package net.okinawa.mcu.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.okinawa.mcu.item.MjolnirItem;

@EventBusSubscriber(modid = "mcu")
public class MjolnirProjectileHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {

        // Make sure the projectile is a trident
        if (!(event.getProjectile() instanceof ThrownTrident trident)) {
            return;
        }

        // Make sure this trident was thrown using Mjolnir
        if (!(trident.getWeaponItem().getItem() instanceof MjolnirItem)) {
            return;
        }

        // Get the level through the Entity getter
        var level = event.getEntity().level();

        // Only summon lightning on the server
        if (level.isClientSide()) {
            return;
        }

        // Get the exact point where the trident hit
        HitResult hit = event.getRayTraceResult();

        double x = hit.getLocation().x;
        double y = hit.getLocation().y;
        double z = hit.getLocation().z;

        // Get the lightning bolt entity type from the registry
        EntityType<?> lightningType =
                BuiltInRegistries.ENTITY_TYPE.getValue(
                        Identifier.withDefaultNamespace("lightning_bolt")
                );

        // Create the lightning entity
        Entity lightningEntity =
                lightningType.create(
                        level,
                        EntitySpawnReason.TRIGGERED
                );

        // Make sure it actually created a lightning bolt
        if (lightningEntity instanceof LightningBolt lightning) {

            // Put the lightning exactly where Mjolnir landed
            lightning.setPos(x, y, z);

            // Spawn it into the world
            level.addFreshEntity(lightning);
        }
    }
}