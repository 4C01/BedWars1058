package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import static com.andrei1058.bedwars.BedWars.config;

public class PlayerPickUpListener implements Listener {
    @EventHandler
    public void onPickUp(PlayerGeneratorCollectEvent event){
        Player player = event.getPlayer();
        Material material = event.getItem().getItemStack().getType();
        if (Arena.getArenaByPlayer(player).getConfig().getBoolean("xp")){
            if (material == Material.IRON_INGOT) {
                player.setLevel(player.getLevel() + event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_IRON_PRICE));
                event.getItem().remove();
                event.setCancelled(true);
            }
            if (material == Material.GOLD_INGOT) {
                player.setLevel(player.getLevel() + event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_GOLD_PRICE));
                event.getItem().remove();
                event.setCancelled(true);
            }
            if (material == Material.EMERALD) {
                player.setLevel(player.getLevel() + event.getItem().getItemStack().getAmount() * config.getInt(ConfigPath.CURRENCY_EMERALD_PRICE));
                event.getItem().remove();
                event.setCancelled(true);
            }
        }
    }
}
