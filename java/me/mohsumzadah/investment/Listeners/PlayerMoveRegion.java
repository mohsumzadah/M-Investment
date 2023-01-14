package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
        Location first_block = null;
        Location second_block = null;

        try {
            first_block = (Location) Investment.plugin.settings.get("Settings.investment_area.first_block.location");
            second_block = (Location) Investment.plugin.settings.get("Settings.investment_area.second_block.location");
        } catch (ClassCastException ignored){

        }

        if(first_block != null && second_block != null) {
            int maxX = Math.max(first_block.getBlockX(), second_block.getBlockX());
            int minX = Math.min(first_block.getBlockX(), second_block.getBlockX());

            int maxZ = Math.max(first_block.getBlockZ(), second_block.getBlockZ());
            int minZ = Math.min(first_block.getBlockZ(), second_block.getBlockZ());

            int maxY = Math.max(first_block.getBlockY(), second_block.getBlockY());
            int minY = Math.min(first_block.getBlockY(), second_block.getBlockY());
            if ((minX <= player_loc.getBlockX() && maxX >= player_loc.getBlockX())
                    && (minZ <= player_loc.getBlockZ() && maxZ >= player_loc.getBlockZ())
                    && (minY <= player_loc.getBlockY() && maxY >= player_loc.getBlockY())) {
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
