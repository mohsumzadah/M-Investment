package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
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
            if (stick_meta.getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Investment Tool") &&
            stick_meta.hasLore()){
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.tool")) {

                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        Investment.plugin.block1 = event.getClickedBlock().getLocation();
                        player.sendMessage(name + ChatColor.GRAY + "First block selected to (" +
                                Investment.plugin.block1.getBlockX() + ", " +
                                Investment.plugin.block1.getBlockY() + ", " +
                                Investment.plugin.block1.getBlockZ() + ").");
                    } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        Investment.plugin.block2 = event.getClickedBlock().getLocation();
                        player.sendMessage(name + ChatColor.GRAY + "Second block selected to (" +
                                Investment.plugin.block2.getBlockX() + ", " +
                                Investment.plugin.block2.getBlockY() + ", " +
                                Investment.plugin.block2.getBlockZ() + ").");
                    }
                }else {
                    player.getInventory().remove(stick);
                    Bukkit.broadcastMessage(name + ChatColor.WHITE
                            +"Investment tool removed from '" + player.getDisplayName() + "' inventory");
                    player.sendMessage(name + ChatColor.RED +"You don't have permissions for that!");
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
