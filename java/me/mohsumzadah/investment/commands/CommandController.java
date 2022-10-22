package me.mohsumzadah.investment.commands;

import me.mohsumzadah.investment.Investment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandController implements CommandExecutor {
    private String name = Investment.plugin.pluginName;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (command.getName().equalsIgnoreCase("investment")) {
                // Open Start gui
                if (args.length == 0) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.gui")) {
                        FileConfiguration gui = Investment.plugin.gui;
                        Inventory inventory = Bukkit.createInventory(player, gui.getInt("size"),
                                gui.getString("name"));

                        ConfigurationSection idle_items = gui.getConfigurationSection("items.idle-items.white-glasses");
                        for (Integer number : idle_items.getIntegerList("slots")) {
                            createInventory(idle_items.getString("type"), idle_items.getInt("amount"),
                                    idle_items.getString("name"),
                                    idle_items.getStringList("lore"), inventory, number, idle_items.getBoolean("glow"));
                        }

                        ConfigurationSection investments_items = gui.getConfigurationSection("items.investments-items");
                        for (String invest_type : investments_items.getKeys(false)) {
                            ConfigurationSection itc = investments_items.getConfigurationSection(invest_type);
                            createInventory(itc.getString("type"), itc.getInt("amount"),
                                    itc.getString("name"),
                                    itc.getStringList("lore"), inventory, itc.getInt("slot"),
                                    itc.getBoolean("glow"));
                        }

                        player.openInventory(inventory);

                    } else {
                        player.sendMessage(name + ChatColor.RED + "You don't have permissions for that!");
                    }
                }
                
                // Investment Stop
                else if (args[0].equalsIgnoreCase("stop")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.stop")) {
                         String invest_type = Investment.plugin.player_data.getString("players." +
                                player.getUniqueId() +".invest_type");
                         int refund_money = Investment.plugin.invest.getInt("investments." +
                                 invest_type+".investDeposit") * Investment.plugin.config
                                 .getInt("Settings.refund-percentage") / 100;
                         Investment.getEconomy().depositPlayer(player,refund_money);


                         Investment.plugin.getCoolDownManager().removePlayerToMap(player);
                         player.sendMessage(Investment.plugin.pluginName + "You invest plan canceled and " +
                                 Investment.plugin.config.getInt("Settings.refund-percentage")+
                                 " percent of your money is back - " +refund_money+"$");

                    }
                }

                // Select area or remove
                else if (args[0].equalsIgnoreCase("area") && args.length == 2) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.area")) {
                        if (args[1].equalsIgnoreCase("tool")) {
                            ItemStack tool = new ItemStack(Material.STICK);
                            ItemMeta meta = tool.getItemMeta();

                            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Investment Tool");

                            List<String> lore = new ArrayList<>();
                            lore.add(0, ChatColor.WHITE + "For choosing area");
                            lore.add(1, ChatColor.WHITE + "Left click - first block");
                            lore.add(2, ChatColor.WHITE + "Right click - second block");
                            meta.setLore(lore);

                            meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                            tool.setItemMeta(meta);

                            player.getInventory().addItem(tool);

                        }
                        else if (args[1].equalsIgnoreCase("select")) {
                            Location first = Investment.plugin.block1;
                            Location second = Investment.plugin.block2;
                            if (first != null && second != null) {
                                Investment.plugin.config.set("Settings.investment_area.first_block.location",
                                        first);
                                Investment.plugin.config.set("Settings.investment_area.second_block.location",
                                        second);
                                Investment.plugin.saveDataConfig();
                                Investment.plugin.block1 = null;
                                Investment.plugin.block2 = null;
                                player.sendMessage(name + ChatColor.GRAY + "Investment area selected!");
                            } else if (first == null && second == null) {
                                player.sendMessage(name + ChatColor.GRAY + "Please select two block with tool");
                            } else if (first == null) {
                                player.sendMessage(name + ChatColor.GRAY + "Please select first block with tool");
                            } else if (second == null) {
                                player.sendMessage(name + ChatColor.GRAY + "Please select second block with tool");
                            }
                        }
                        else if (args[1].equalsIgnoreCase("remove")) {
                            Investment.plugin.config.set("Settings.investment_area.first_block.location",
                                    "");
                            Investment.plugin.config.set("Settings.investment_area.second_block.location",
                                    "");
                            Investment.plugin.saveDataConfig();
                            Investment.plugin.block1 = null;
                            Investment.plugin.block2 = null;

                            Investment.plugin.getCoolDownManager().setAllPlayersIsNotOnRegion();
                            player.sendMessage(name + ChatColor.WHITE + "Investment area removed!");
                        }
                    } else {
                        player.sendMessage(name + ChatColor.RED + "You don't have 'investment.area' permissions for that!");
                    }
                }

                // Create investment plan or remove
                else if (args[0].equalsIgnoreCase("create") && args.length == 6) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        String inv_name = args[1];
                        int inv_second = Integer.parseInt(args[2]);
                        int inv_deposit = Integer.parseInt(args[3]);
                        int inv_withdraw = Integer.parseInt(args[4]);
                        int inv_slot = Integer.parseInt(args[5]) - 1;

                        int time_hours = inv_second / 3600;
                        int time_minute = (inv_second % 3600) / 60;
                        int time_second = inv_second % 60;


                        List<String> lores = new ArrayList<>();
                        lores.add(0, " ");
                        lores.add(1, "&b» Deposit " +"&F"+ inv_deposit + "$");
                        lores.add(2, "&b« Withdraw " +"&F"+ inv_withdraw + "$");
                        lores.add(3, " ");
                        lores.add(4, "&bStay in the area for " +
                                "&F" + time_hours + "h " + time_minute + "m " + time_second + "s ");
                        lores.add(5, "&bto get your reward");
                        lores.add(6, " ");
                        lores.add(7, "&7Slot: " + args[5]);

                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".type", "IRON_AXE");
                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".name",
                                ChatColor.GOLD + (time_hours + "h " + time_minute + "m " + time_second + "s "));
                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".lore", lores);
                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".amount", 1);
                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".slot", inv_slot);
                        Investment.plugin.gui.set("items.investments-items." + inv_name + ".enchant", true);
                        Investment.plugin.saveGuiConfig();

                        Investment.plugin.invest.set("investments." + inv_name + ".stayTime", inv_second);
                        Investment.plugin.invest.set("investments." + inv_name + ".investDeposit", inv_deposit);
                        Investment.plugin.invest.set("investments." + inv_name + ".investWithdraw", inv_withdraw);

                        Investment.plugin.saveInvestConfig();

                        player.sendMessage(Investment.plugin.pluginName+"'"+inv_name+"' investment plan created.");

                    } else {
                        player.sendMessage(name + ChatColor.RED + "You don't have 'investment.create' permissions for that!");
                    }
                }
                else if (args[0].equalsIgnoreCase("remove") && args.length == 2){
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.remove")) {
                        int slot = Integer.parseInt(args[1])-1;

                        ConfigurationSection investments_items = Investment.plugin.gui.getConfigurationSection("items.investments-items");
                        for (String invest_type : investments_items.getKeys(false)) {

                            if(investments_items.getInt(invest_type+".slot") == slot){
                                Investment.plugin.gui.set("items.investments-items."+invest_type, null);
                                Investment.plugin.invest.set("investments."+invest_type, null);
                                player.sendMessage(Investment.plugin.pluginName+"'"+invest_type+"' investment plan removed.");
                            }
                        }


                    }else {
                        player.sendMessage(name + ChatColor.RED + "You don't have 'investment.remove' permission for that!");
                    }
                }


            }
        }
        return false;
    }


    private void createInventory(String item_type, Integer item_amount, String name, List<String> lores,
                                 Inventory inventory, Integer slot, Boolean bool){
        ItemStack item = new ItemStack(Material.getMaterial(item_type), item_amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));

        List<String> new_lore = new ArrayList<>();
        for (String lore : lores){
            new_lore.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        itemMeta.setLore(new_lore);

        if (bool){
            itemMeta.addEnchant(Enchantment.PROTECTION_FALL, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }


        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);

    }

}
