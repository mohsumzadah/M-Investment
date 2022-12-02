package me.mohsumzadah.investment.Listeners;

import me.mohsumzadah.investment.Investment;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryListener implements Listener {



    @EventHandler
    public void clickInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        InventoryView inv = event.getView();
        if(inv.getTitle().equals(Investment.plugin.gui.getString("name"))){
            if(!Investment.plugin.getCoolDownManager().isPlayerOnMap(player)) {
                if (event.getCurrentItem() == null) return;
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
                            int investDeposit = investment_data.getInt("investDeposit");
                            player.sendMessage(Investment.plugin.returnReplaceMessage(false,
                                    "feedback-invested-money", "deposit_money", String.valueOf(investDeposit)));
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
                            int deposit_money = investment_data.getInt("investDeposit");
                            player.sendMessage(Investment.plugin.returnReplaceMessage(false,
                                    "feedback-no-enough-money","deposit_money",
                                    String.valueOf(deposit_money)));
                            event.setCancelled(true);
                            inv.close();
                        }


                    }
                }
            }
            else {
                player.sendMessage(Investment.plugin.returnMessage(false, "feedback-already-hava-inv"));
                event.setCancelled(true);
                inv.close();
            }
            event.setCancelled(true);
        }


    }
}
