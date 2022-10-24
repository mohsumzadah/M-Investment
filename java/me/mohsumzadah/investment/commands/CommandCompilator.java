package me.mohsumzadah.investment.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandCompilator implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();
            if (args.length == 1 && args[0].length() < 3){
                List<String> list = new ArrayList<>();
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.stop")){
                    list.add(list.size(), "stop");
                }
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.area")){
                    list.add(list.size(), "area");
                }
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.create")) {
                    list.add(list.size(), "create");
                }
                if (player.hasPermission("investment.*") ||
                        player.hasPermission("investment.remove")) {
                    list.add(list.size(), "remove");
                }
                return list;

            }
            else if (args.length == 2){
                List<String> list = new ArrayList<>();
                if (args[0].equalsIgnoreCase("area")){
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.area")){
                        list.add(list.size(), "tool");
                        list.add(list.size(), "select");
                        list.add(list.size(), "remove");
                    }
                } else if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        list.add(list.size(), "investment-name");
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.remove")) {
                        list.add(list.size(), "slot");
                    }
                }
                return list;
            } else if (args.length == 3) {
                List<String> list = new ArrayList<>();
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        list.add(list.size(), "second");
                    }
                }
                return list;
            }else if (args.length == 4) {
                List<String> list = new ArrayList<>();
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        list.add(list.size(), "deposit-money");
                    }
                }
                return list;
            }else if (args.length == 5) {
                List<String> list = new ArrayList<>();
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        list.add(list.size(), "withdraw-money");
                    }
                }
                return list;
            }else if (args.length == 6) {
                List<String> list = new ArrayList<>();
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("investment.*") ||
                            player.hasPermission("investment.create")) {
                        list.add(list.size(), "item-slot");
                    }
                }
                return list;
            }
        }



        return null;
    }
}
