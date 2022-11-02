package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveRegion implements Listener {

    @EventHandler
    public void playerMoveRegion(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location player_loc = player.getLocation();
        Location block1 = null;
        Location block2 = null;

        try {
            block1 = (Location) Investment.plugin.config.get("Settings.investment_area.first_block.location");
            block2 = (Location) Investment.plugin.config.get("Settings.investment_area.second_block.location");
        } catch (ClassCastException ignored){

        }

        if(block1 != null && block2 != null) {
            int maxX = Math.max(block1.getBlockX(), block2.getBlockX());
            int minX = Math.min(block1.getBlockX(), block2.getBlockX());

            int maxZ = Math.max(block1.getBlockZ(), block2.getBlockZ());
            int minZ = Math.min(block1.getBlockZ(), block2.getBlockZ());
            if ((minX <= player_loc.getBlockX() && maxX >= player_loc.getBlockX())
                    && (minZ <= player_loc.getBlockZ() && maxZ >= player_loc.getBlockZ())) {
                if (Investment.plugin.getCoolDownManager().isPlayerOnMap(player)) {
                    if (!Investment.plugin.getCoolDownManager().isPlayerOnRegion(player)) {
                        Investment.plugin.getCoolDownManager().setplayerIsOnRegion(player);
                    }
                }else {
                    player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            new TextComponent(Investment.plugin
                                    .returnMessage(true,"area-no-inv-progress")));
                }
            }
            else {
                if(Investment.plugin.getCoolDownManager().isPlayerOnMap(player)) {
                    if (Investment.plugin.getCoolDownManager().isPlayerOnRegion(player)) {
                        Investment.plugin.getCoolDownManager().setplayerIsNotOnRegion(player);
                        player.sendMessage(Investment.plugin
                                .returnMessage(false,"area-not-in-region"));
                    }
                }
            }
        }
    }

}
