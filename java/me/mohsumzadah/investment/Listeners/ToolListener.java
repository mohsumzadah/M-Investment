package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolListener implements Listener {
    private String name = Investment.plugin.pluginName;

    @EventHandler
    public void toolClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        if(player.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
            ItemStack stick = player.getInventory().getItemInMainHand();
            ItemMeta stick_meta = stick.getItemMeta();
            if (stick_meta.getDisplayName().equals(Investment.plugin.returnMessage(true,"tool-name")) &&
            stick_meta.hasLore()){
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.tool")) {

                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        Investment.plugin.block1 = event.getClickedBlock().getLocation();
                        String X = String.valueOf(Investment.plugin.block1.getBlockX());
                        String Y = String.valueOf(Investment.plugin.block1.getBlockY());
                        String Z = String.valueOf(Investment.plugin.block1.getBlockZ());

                        player.sendMessage(Investment.plugin
                                .returnReplaceMessage(false,"tool-first-block-selected",
                                        "XYZ",
                                        X+", "+Y+", "+Z));
                    } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        Investment.plugin.block2 = event.getClickedBlock().getLocation();
                        String X = String.valueOf(Investment.plugin.block2.getBlockX());
                        String Y = String.valueOf(Investment.plugin.block2.getBlockY());
                        String Z = String.valueOf(Investment.plugin.block2.getBlockZ());

                        player.sendMessage(Investment.plugin
                                .returnReplaceMessage(false,"tool-second-block-selected",
                                        "XYZ",
                                        X+", "+Y+", "+Z));
                    }
                }
                else {
                    player.getInventory().remove(stick);
                    Bukkit.broadcastMessage(Investment.plugin
                            .returnReplaceMessage(false,"tool-noperm-usage",
                                    "player_name",
                                    player.getDisplayName()));


                    player.sendMessage(Investment.plugin
                            .returnReplaceMessage(true,"dont-have-permission",
                            "permission",
                            "investment.area"));
                }
            }
        }

    }

    @EventHandler
    public void cancelBlockBreak(BlockBreakEvent event){
        if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            ItemStack stick = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta stick_meta = stick.getItemMeta();
            if (stick_meta.getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Investment Tool") &&
                    stick_meta.hasLore()) {
                event.setCancelled(true);

            }
        }
    }

}
