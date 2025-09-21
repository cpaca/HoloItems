package xyz.holocons.mc.holoitemsrevamp.enchantment;

import com.strangeone101.holoitemsapi.enchantment.CustomEnchantment;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp;
import xyz.holocons.mc.holoitemsrevamp.Util;

import java.util.HashSet;
import java.util.Set;

public class Comet extends CustomEnchantment {

    private final HoloItemsRevamp plugin;

    public Comet(HoloItemsRevamp plugin) {
        super(plugin, "comet");
        this.plugin = plugin;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event, ItemStack itemStack) {
        // Cancel if not right click
        final var action = event.getAction();
        if(!(action==Action.RIGHT_CLICK_AIR || action==Action.RIGHT_CLICK_BLOCK) ||
                event.useInteractedBlock() == Event.Result.ALLOW)
            return;

        // Cancel if owner is affected by Weakness
        Player player = event.getPlayer();
        if(player.hasPotionEffect(PotionEffectType.WEAKNESS)){
            player.sendMessage("§7The axe weighs heavily on your arms");
            return;
        }

        // Cancel if on cooldown
        // TODO: Reimplement
//        ItemStack item = event.getItem();
//        if(Utility.onCooldown(item))
//            return;
//        Utility.cooldown(item, 20);

        double damage = 4 + 3 * (Util.checkPotionEffect(player, PotionEffectType.STRENGTH));

        Location location = player.getEyeLocation();
        World world = player.getWorld();

        final double maxDistance = 50;
        double distance = maxDistance;

        // Normalized vector
        Vector direction = location.getDirection();

        Set<LivingEntity> targets = new HashSet<>();

        // Raytrace to find entities in the way. If piercing is applied, do it multiple times
        for(int i=0; i<1+itemStack.getEnchantmentLevel(Enchantment.PIERCING); i++) {
            RayTraceResult result = world.rayTrace(location, direction, maxDistance,
                    FluidCollisionMode.NEVER, true, 0.5,
                    entity -> (entity != player &&
                            entity instanceof LivingEntity && !(entity instanceof ArmorStand) &&
                            !targets.contains(entity))); // Skip previously raytraced entities
            if (result != null) {
                LivingEntity entity = (LivingEntity) result.getHitEntity();
                if (entity != null) {
                    distance = location.distance(result.getHitEntity().getLocation());
                    targets.add(entity);
                }
                else if (result.getHitBlock() != null) {
                    distance = location.distance(result.getHitBlock().getLocation());
                    break;
                }
            }
        }

        // Offset axe according to player's hand
        int rotation;
        if (event.getHand() == EquipmentSlot.HAND) {
            rotation = -1;
        } else {
            rotation = 1;
        }

        ItemDisplay axeDisplay = world.spawn(location, ItemDisplay.class, entity -> {
            // This block runs before entity is ticked, meaning it won't show a mark in minimap right as the axe is spawned
            entity.setInvisible(true); // Remove mark in minimaps
            entity.setPersistent(false); // Remove if chunk unloads
            entity.setItemStack(itemStack);
            entity.setViewRange((float)maxDistance);

            Transformation currentTransformation = entity.getTransformation();
            currentTransformation.getLeftRotation()
                    .rotateLocalY((float) Math.toRadians(-90)) // Rotate vertically (to face forward pointing frontwards)
                    .rotateLocalZ((float) Math.toRadians(15 * rotation)); // Slant inwards
            currentTransformation.getTranslation().add(0.4f * rotation, -0.25f, 0.5f); // Move closer to hand
            entity.setTransformation(currentTransformation);
        });

        // Check if SpaceBreadSplash is applied
        // TODO: Reimplement. We can do this entirely with datapacks now.
//        String enchant = item.getItemMeta().getPersistentDataContainer().get(Utility.enchant, PersistentDataType.STRING);
//        boolean bread = enchant!=null && enchant.contains(SpaceBreadSplash.name);
        double height = player.getLocation().getY();

        // Consume durability
        // TODO: Remove durability properly
//        if (player.getGameMode()!= GameMode.CREATIVE)
//            Utility.addDurability(item, -1, player);

        // Set vector speed as 3 blocks/tick
        final double speed = 3;
        final double maxIteration = distance / (double) speed;

        final var runnable = new BukkitRunnable() {
            double increment = 0;
            boolean crit = player.getLocation().getY()<height;
            Quaternionf rotationPerTick = new Quaternionf().rotateZ((float) Math.toRadians(-60));

            public void run(){
                try {
                    if(increment >= maxIteration) {
                        if(!targets.isEmpty()) {
                            // Restore half durability
                            // TODO: Reimplement durability properly
//                            if (player.getGameMode()!=GameMode.CREATIVE)
//                                Utility.addDurability(item, 0.5, player);

                            // Prepare for Utility.damage()
                            ItemStack itemForDamage = itemStack;
                            // TODO: Reimplement bread
//                            if(bread) {
//                                itemForDamage = item.clone();
//                                itemForDamage.addUnsafeEnchantment(Enchantment.SMITE, 5);
//                                itemForDamage.addUnsafeEnchantment(Enchantment.BANE_OF_ARTHROPODS, 5);
//                                itemForDamage.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
//                            }
                            for (LivingEntity target : targets) {
                                if (target.isValid() && (!(target instanceof Player) || !((Player) target).isBlocking())) {
                                    // TODO: reimplement or find a builtin for it
                                    //   This doesn't process smite or bane-of-arthro.
//                                    Utility.damage(itemForDamage, damage, crit, player, target, false, true, false);
                                    target.damage(damage, player);
                                }
                            }
                        }
                        axeDisplay.remove();
                        cancel();
                        return;
                    }


                    if (increment != 0) {
                        Transformation currentTransformation = axeDisplay.getTransformation();
                        currentTransformation.getLeftRotation().mul(rotationPerTick); // Spin
                        currentTransformation.getTranslation().add(0, 0, (float)speed); // Move forward
                        axeDisplay.setTransformation(currentTransformation);
                        axeDisplay.setInterpolationDelay(0);
                        axeDisplay.setInterpolationDuration(1);
                    }

                    ++increment;
                } catch (Exception e) {
                    // Avoid being in loop logging errors in case of exception
                    plugin.getLogger().warning("Error in Comet ability: " + e.getMessage());
                    axeDisplay.remove();
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 1, 1);
    }
}
