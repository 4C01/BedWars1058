package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import static com.andrei1058.bedwars.BedWars.*;

public class PlayerPickUpListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPickUp(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        Material material = event.getItem().getItemStack().getType();
        if(Arena.getArenaByPlayer(player) == null){
            return;
        }
        int xps = 0;
        if (Arena.getArenaByPlayer(player).getConfig().getBoolean("xp")) {
            if (nms.materialExperienceBottle().equals(material)){
                xps = event.getItem().getItemStack().getAmount() * 10;
                event.getItem().remove();
                event.setCancelled(true);
            }
            else
            {
                switch (material) {
                    case IRON_INGOT:
                        xps = event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_IRON_PRICE);
                        event.getItem().remove();
                        event.setCancelled(true);
                        break;
                    case GOLD_INGOT:
                        xps = event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_GOLD_PRICE);
                        event.getItem().remove();
                        event.setCancelled(true);
                        break;
                    case EMERALD:
                        xps = event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_EMERALD_PRICE);
                        event.getItem().remove();
                        event.setCancelled(true);
                        break;
                }
            }
            if(xps != 0) {
                player.giveExpLevels(xps);
                player.playSound(player.getLocation(), Sound.valueOf(BedWars.getForCurrentVersion("SUCCESSFUL_HIT", "ENTITY_EXPERIENCE_ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP")), 0.6f, 1.3f);
            }
        }
    }
}
