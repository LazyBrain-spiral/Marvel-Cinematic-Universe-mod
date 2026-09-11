package net.okinawa.mcu.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class MjolnirItem extends TridentItem {

    private static final int THROW_THRESHOLD = 10;

    public MjolnirItem(Properties properties) {
        super(properties);
    }

    // Speed III while Mjolnir is in the main hand
    @Override
    public void inventoryTick(
            ItemStack stack,
            ServerLevel level,
            Entity entity,
            EquipmentSlot slot
    ) {
        super.inventoryTick(stack, level, entity, slot);

        if (entity instanceof Player player
                && slot == EquipmentSlot.MAINHAND) {

            player.addEffect(new MobEffectInstance(
                    MobEffects.SPEED,
                    10,
                    2,
                    false,
                    false,
                    true
            ));
        }
    }

    // Right-click starts the normal Trident charging
    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        // Make sure Mjolnir always has Loyalty III
        if (!level.isClientSide()) {

            var enchantments =
                    level.registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT);

            var loyalty =
                    enchantments.getOrThrow(Enchantments.LOYALTY);

            EnchantmentHelper.updateEnchantments(
                    stack,
                    mutable -> mutable.set(loyalty, 3)
            );
        }

        return super.use(level, player, hand);
    }

    // Called when the player releases right-click
    @Override
    public boolean releaseUsing(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft
    ) {

        if (!(entity instanceof Player player)) {
            return false;
        }

        int useTime =
                getUseDuration(stack, player) - timeLeft;

        /*
         * QUICK RIGHT-CLICK
         * Less than 0.5 seconds = lightning
         */
        if (useTime < THROW_THRESHOLD) {

            if (!level.isClientSide()) {

                BlockHitResult hit =
                        player.pick(
                                30.0,
                                0.0F,
                                false
                        ) instanceof BlockHitResult blockHit
                                ? blockHit
                                : null;

                if (hit != null) {

                    EntityType<?> lightningType =
                            net.minecraft.core.registries.BuiltInRegistries
                                    .ENTITY_TYPE
                                    .getValue(
                                            net.minecraft.resources.Identifier
                                                    .withDefaultNamespace(
                                                            "lightning_bolt"
                                                    )
                                    );

                    Entity lightningEntity =
                            lightningType.create(
                                    level,
                                    EntitySpawnReason.TRIGGERED
                            );

                    if (lightningEntity instanceof LightningBolt lightning) {

                        lightning.setPos(
                                hit.getBlockPos().getX() + 0.5,
                                hit.getBlockPos().getY() + 1.0,
                                hit.getBlockPos().getZ() + 0.5
                        );

                        level.addFreshEntity(lightning);
                    }
                }
            }

            return false;
        }

        /*
         * LONG RIGHT-CLICK
         * Let TridentItem create the thrown trident.
         *
         * Loyalty III is already on the stack,
         * so the thrown trident can read it.
         */
        return super.releaseUsing(
                stack,
                level,
                player,
                timeLeft
        );
    }
}