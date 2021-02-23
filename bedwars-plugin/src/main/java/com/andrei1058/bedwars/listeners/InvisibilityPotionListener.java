package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.sidebar.BedWarsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.andrei1058.bedwars.BedWars.nms;
import static com.andrei1058.bedwars.BedWars.plugin;

/**
 * This is used to hide and show player name tag above head when he drinks an invisibility
 * potion or when the potion is gone. It is required because it is related to scoreboards.
 */
public class InvisibilityPotionListener implements Listener {

    @EventHandler
    public void onPotion(PlayerInvisibilityPotionEvent e) {
        if (e.getTeam() == null) return;
        if (e.getType() == PlayerInvisibilityPotionEvent.Type.ADDED) {
            for (BedWarsScoreboard sb : BedWarsScoreboard.getScoreboards().values()) {
                if (sb.getArena() == null) continue;
                if (sb.getArena().equals(e.getArena())) {
                    sb.invisibilityPotion(e.getTeam(), e.getPlayer(), true);
                }
            }
        } else {
            for (BedWarsScoreboard sb : BedWarsScoreboard.getScoreboards().values()) {
                if (sb.getArena() == null) continue;
                if (sb.getArena().equals(e.getArena())) {
                    sb.invisibilityPotion(e.getTeam(), e.getPlayer(), false);
                }
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        IArena a = Arena.getArenaByPlayer(e.getPlayer());
        if (a == null) return;
        if (e.getItem().getType() != Material.POTION) return;
        // remove potion bottle
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                        nms.minusAmount(e.getPlayer(), new ItemStack(Material.GLASS_BOTTLE), 1),
                5L);
        //
        PotionMeta pm = (PotionMeta) e.getItem().getItemMeta();
        if (pm.hasCustomEffects()) {
            if (pm.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {
                        if (pe.getType().toString().contains("INVISIBILITY")) {
                            // if is already invisible
                            if (a.getShowTime().containsKey(e.getPlayer())) {
                                ITeam t = a.getTeam(e.getPlayer());
                                // increase invisibility timer
                                // keep trace of invisible players to send hide armor packet when required
                                // because potions do not hide armors
                                a.getShowTime().replace(e.getPlayer(), pe.getDuration() / 20);
                                // call custom event
                                Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, t, e.getPlayer(), t.getArena()));
                            } else {
                                // if not already invisible
                                ITeam t = a.getTeam(e.getPlayer());
                                // keep trace of invisible players to send hide armor packet when required
                                // because potions do not hide armors
                                a.getShowTime().put(e.getPlayer(), pe.getDuration() / 20);
                                //
                                for (Player p1 : e.getPlayer().getWorld().getPlayers()) {
                                    if (a.isSpectator(p1)) {
                                        // hide player armor to spectators
                                        nms.hideArmor(e.getPlayer(), p1);
                                    } else if (t != a.getTeam(p1)) {
                                        // hide player armor to other teams
                                        nms.hideArmor(e.getPlayer(), p1);
                                    }
                                }
                                // call custom event
                                Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, t, e.getPlayer(), t.getArena()));
                            }
                            break;
                        }
                    }
                }, 5L);
            }
        }
    }
}
