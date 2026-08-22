package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.andrei1058.bedwars.BedWars.config;
import static com.andrei1058.bedwars.BedWars.plugin;

public class PlayerGeneratorCollectListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCollect(PlayerGeneratorCollectEvent event) {
        Item item = event.getItem();
        Material material = item.getItemStack().getType();
        Player player = event.getPlayer();
        if (item.getItemStack().hasItemMeta()) {
            //noinspection ConstantConditions
            if (item.getItemStack().getItemMeta().hasDisplayName()) {
                if (item.getItemStack().getItemMeta().getDisplayName().contains("custom") && plugin.getConfig().getBoolean(ConfigPath.GENERAL_CONFIGURATION_ENABLE_GEN_SPLIT)) {
                    if (!(material == Material.IRON_INGOT || material == Material.GOLD_INGOT))
                        return;
                    ItemMeta itemMeta = new ItemStack(material).getItemMeta();
                    item.getItemStack().setItemMeta(itemMeta);
                    Location location = event.getPlayer().getLocation();
                    for (Entity entity : player.getWorld().getNearbyEntities(location, 2.0D, 2.0D, 2.0D)){
                        if (entity instanceof Player && entity != player){
                            Player nearby = (Player) entity;
                            if (!Arena.getArenaByPlayer(player).getTeam(player).isMember(nearby))
                                continue;
                            if (Arena.getArenaByPlayer(player).getConfig().getBoolean("xp")) {
                                int xps = 0;
                                switch (material) {
                                    case IRON_INGOT:
                                        xps = event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_IRON_PRICE);
                                        break;
                                    case GOLD_INGOT:
                                        xps = event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_GOLD_PRICE);
                                        break;
                                }
                                if(xps != 0) {
                                    nearby.giveExpLevels(xps);
                                    nearby.playSound(nearby.getLocation(), Sound.valueOf(BedWars.getForCurrentVersion("SUCCESSFUL_HIT", "ENTITY_EXPERIENCE_ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP")), 0.6f, 1.3f);
                                }
                            }
                            else {
                                nearby.getInventory().addItem(item.getItemStack());
                                nearby.playSound(player.getLocation(), Sound.valueOf(BedWars.getForCurrentVersion("ITEM_PICKUP", "ENTITY_ITEM_PICKUP", "ENTITY_ITEM_PICKUP")), 0.6f, 1.3f);
                            }
                        }
                    }
                }
            }
        }
    }
}
