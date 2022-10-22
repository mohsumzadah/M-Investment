package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import me.mohsumzadah.investment.manager.CoolDownManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class InventoryListener implements Listener {



    @EventHandler
    public void clickInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        FileConfiguration gui = Investment.plugin.gui;

        InventoryView inv = event.getView();
        if(inv.getTitle().equals(gui.getString("name"))){
//            Investment.plugin.player_data.getConfigurationSection("players")
//                    .contains(String.valueOf(player.getUniqueId()))
            if(!Investment.plugin.getCoolDownManager().isPlayerOnMap(player)) {
                String invest_type_name = event.getCurrentItem().getItemMeta().getDisplayName();
                for (String invest_name : Investment.plugin.invest
                        .getConfigurationSection("investments").getKeys(false)) {
                    if (ChatColor.translateAlternateColorCodes('&', Investment.plugin.gui.
                            getString("items.investments-items." + invest_name + ".name")).equals(invest_type_name)) {

                        ConfigurationSection investment_data = Investment.plugin.invest
                                .getConfigurationSection("investments." + invest_name);

                        if (Investment.getEconomy().getBalance(player) >= investment_data.getInt("investDeposit")) {

                            Investment.getEconomy().withdrawPlayer(player, investment_data.getInt("investDeposit"));
                            Investment.plugin.getCoolDownManager()
                                    .addPlayerToMap(player, investment_data.getInt("stayTime"), invest_name);
                            player.sendMessage(Investment.plugin.pluginName + "You invested " +
                                    investment_data.getInt("investDeposit") + "$. Please go to investment area.");
                            event.setCancelled(true);
                            inv.close();

                            Location player_loc = player.getLocation();
                            Location block1 = null;
                            Location block2 = null;

                            try {
                                block1 = (Location) Investment.plugin.config.get("Settings.investment_area.first_block.location");
                                block2 = (Location) Investment.plugin.config.get("Settings.investment_area.second_block.location");
                            } catch (ClassCastException ignored) {

                            }

                            if (block1 != null && block2 != null) {
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
                                    }
                                }
                            }

                        } else {
                            player.sendMessage(Investment.plugin.pluginName + "You don't have " +
                                    investment_data.getInt("investDeposit") + "$ for deposit.");
                            event.setCancelled(true);
                            inv.close();
                        }


                    }
                }
            }
            else {
                player.sendMessage(Investment.plugin.pluginName+ChatColor.RED+"You have investment plan. You can't choose again.");
                event.setCancelled(true);
                inv.close();
            }
            event.setCancelled(true);
        }


    }
}
