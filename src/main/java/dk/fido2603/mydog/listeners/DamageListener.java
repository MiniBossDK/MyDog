package dk.fido2603.mydog.listeners;

import dk.fido2603.mydog.MyDog;

import dk.fido2603.mydog.managers.DogManager;
import dk.fido2603.mydog.objects.Dog;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    private MyDog plugin = null;

    public DamageListener(MyDog p) {
        this.plugin = p;
    }

    @EventHandler
    public void onPlayerEntityDamage(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();
        Entity attackedEntity = e.getEntity();
        if(attacker.getType() == EntityType.WOLF)
        {
            if(MyDog.getDogManager().isDog(attacker.getUniqueId()) && attackedEntity.getType() == EntityType.PLAYER) {
                // Makes sure the dog's damage to a player is not higher than the MaxPlayerDamage in the config
                Dog dog = MyDog.getDogManager().getDog(e.getDamager().getUniqueId());
                if(e.getDamage() > plugin.maxPlayerDamage) {
                    plugin.logDebug(String.format("%s dealt %s damage but was capped to %d", dog.getDogName(), e.getDamage(), plugin.maxPlayerDamage));
                    e.setDamage(plugin.maxPlayerDamage);
                }
            }
        }
    }

    @EventHandler
    public void onWolfEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.WOLF || !(MyDog.getDogManager().isDog(e.getEntity().getUniqueId()))) {
            return;
        }
        EntityType type = e.getDamager().getType();
        if (type == EntityType.PLAYER) {
            Dog wolf = MyDog.getDogManager().getDog(e.getEntity().getUniqueId());
            if (wolf.getOwnerId().equals(e.getDamager().getUniqueId())) {
                e.setCancelled(true);
            }
        }
        if (type == EntityType.ARROW && !plugin.allowArrowDamage) {
            e.setCancelled(true);
        }

        // TODO something with a Dog's equipped armor to lower damage caused
    }

    @EventHandler
    public void onEntityDeath(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            if (damageEvent.getDamager() instanceof Wolf && damageEvent.getEntity() instanceof LivingEntity) {
                if (!(MyDog.getDogManager().isDog(damageEvent.getDamager().getUniqueId())) || (damageEvent.getFinalDamage() < ((LivingEntity) damageEvent.getEntity()).getHealth())) {
                    return;
                }

                plugin.logDebug("Dog has killed " + event.getEntityType() + " with a final blow dealing " + event.getFinalDamage() + " HP!");
                if (plugin.useLevels) {
                    int gainedExp;
                    switch (event.getEntityType()) {
                        case BAT:
                        case AXOLOTL:
                        case BEE:
                        case OCELOT:
                        case CAT:
                        case PARROT:
                        case COD:
                        case CHICKEN:
                        case FOX:
                        case SILVERFISH:
                        case PUFFERFISH:
                        case RABBIT:
                        case SALMON:
                        case SHULKER:
                        case SLIME:
                        case SNOWMAN:
                        case SPIDER:
                        case ZOMBIE:
                        case SKELETON:
                        case SQUID:
                        case GLOW_SQUID:
                        case TROPICAL_FISH:
                        case TURTLE:
                            gainedExp = 5;
                            break;
                        case COW:
                        case MUSHROOM_COW:
                        case PIG:
                        case SHEEP:
                        case WOLF:
                        case WANDERING_TRADER:
                        case VILLAGER:
                        case STRAY:
                            gainedExp = 8;
                            break;
                        case CAVE_SPIDER:
                        case PANDA:
                        case CREEPER:
                        case DROWNED:
                        case DOLPHIN:
                        case WITHER_SKELETON:
                        case STRIDER:
                        case DONKEY:
                        case GOAT:
                        case WITCH:
                        case SKELETON_HORSE:
                        case VEX:
                        case VINDICATOR:
                        case HORSE:
                        case TRADER_LLAMA:
                        case HUSK:
                        case LLAMA:
                        case MULE:
                        case ZOMBIE_HORSE:
                        case ZOMBIE_VILLAGER:
                        case PIGLIN:
                        case ZOMBIFIED_PIGLIN:
                        case POLAR_BEAR:
                            gainedExp = 20;
                            break;
                        case ENDERMITE:
                        case GUARDIAN:
                        case MAGMA_CUBE:
                        case PIGLIN_BRUTE:
                        case PHANTOM:
                        case PILLAGER:
                        case ENDERMAN:
                            gainedExp = 40;
                            break;
                        case BLAZE:
                        case EVOKER:
                        case GHAST:
                        case HOGLIN:
                        case ILLUSIONER:
                            gainedExp = 60;
                            break;
                        case IRON_GOLEM:
                        case RAVAGER:
                            gainedExp = 70;
                            break;
                        case PLAYER:
                            if (plugin.allowPlayerKillExp) {
                                gainedExp = 70;
                                break;
                            } else {
                                gainedExp = 0;
                                break;
                            }
                        case ELDER_GUARDIAN:
                        case GIANT:
                            gainedExp = 90;
                            break;
                        case WITHER:
                        case ENDER_DRAGON:
                            gainedExp = 200;
                            break;
                        default:
                            gainedExp = 0;
                            break;
                    }

                    // Give the Dog the experience
                    Dog dog = MyDog.getDogManager().getDog(damageEvent.getDamager().getUniqueId());
                    plugin.logDebug("Giving " + dog.getDogName() + " " + gainedExp + " experience!");
                    dog.giveExperience(gainedExp);
                }
            }
        }
    }
}
