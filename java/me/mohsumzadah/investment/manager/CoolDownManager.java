package me.mohsumzadah.investment.manager;

import me.mohsumzadah.investment.Investment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CoolDownManager {
    Economy economy = Investment.getEconomy();

    public final Map<UUID, Integer> playerCooldownMap = new HashMap<>();
    public final Map<UUID, Boolean> playerInRegionBooleanMap = new HashMap<>();

    public final Map<UUID, String> playerInvestmentType = new HashMap<>();
    public List<UUID> removeList = new ArrayList<>();

    public CoolDownManager(Investment plugin){
         new BukkitRunnable(){
             @Override
             public void run() {
                 for(UUID uuid : playerCooldownMap.keySet()){
                     if(playerInRegionBooleanMap.get(uuid)) {
                         try {
                             if (playerCooldownMap.get(uuid) == 1) {
                                 Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
                                 Integer withdraw_money = Investment.plugin.invest.getInt("investments."
                                         + playerInvestmentType.get(uuid) + ".investWithdraw");
                                 economy.depositPlayer(player, withdraw_money);
                                 player.sendMessage(Investment.plugin
                                         .returnReplaceMessage(false, "feedback-earned-money",
                                                 "withdraw_money",
                                                 withdraw_money.toString()));
                                 removePlayerToMap(player);
                                 continue;
                             }
                             playerCooldownMap.put(uuid, (playerCooldownMap.get(uuid) - 1));
                             int time = playerCooldownMap.get(uuid);
                             Investment.plugin.player_data.set("players." + uuid + ".second_left", time);
                             Investment.plugin.savePlayerDataConfig();

                             int time_hours = time / 3600;
                             int time_minute = (time % 3600) / 60;
                             int time_second = time % 60;

                             String str = Investment.plugin.returnReplaceMessage(true, "action-bar-message",
                                             "time_hours", String.valueOf(time_hours))
                                     .replaceAll("%time_minute%", String.valueOf(time_minute))
                                     .replaceAll("%time_second%", String.valueOf(time_second));
                             Bukkit.getOfflinePlayer(uuid).getPlayer().spigot().sendMessage(
                                     ChatMessageType.ACTION_BAR, new TextComponent(str));


                         } catch (NullPointerException e){
                            playerInRegionBooleanMap.put(uuid, false);
                         }
                     }
                 }
             }
         }.runTaskTimer(plugin, 0, 20);
    }

    public void addPlayerToMap(Player player, Integer time, String investment){
        playerCooldownMap.put(player.getUniqueId(), time);
        playerInRegionBooleanMap.put(player.getUniqueId(), false);
        playerInvestmentType.put(player.getUniqueId(), investment);
        Investment.plugin.player_data.set("players."+player.getUniqueId()+".second_left", time);
        Investment.plugin.player_data.set("players."+player.getUniqueId() +".boolean", false);
        Investment.plugin.player_data.set("players."+player.getUniqueId() +".invest_type", investment);
        Investment.plugin.savePlayerDataConfig();

    }

    public void removePlayerToMap(Player player){
        playerCooldownMap.remove(player.getUniqueId());
        playerInRegionBooleanMap.remove(player.getUniqueId());
        playerInvestmentType.remove(player.getUniqueId());
        Investment.plugin.player_data.set("players."+player.getUniqueId(), null);
        Investment.plugin.savePlayerDataConfig();
    }
    
    public boolean isPlayerOnMap(Player player){
        if(playerCooldownMap.containsKey(player.getUniqueId())){
            return true;
        }else {
            return false;
        }
    }


    public void setplayerIsOnRegion(Player player){
        playerInRegionBooleanMap.put(player.getUniqueId(), true);
    }

    public void setplayerIsNotOnRegion(Player player){
        playerInRegionBooleanMap.put(player.getUniqueId(), false);
    }

    public boolean isPlayerOnRegion(Player player){
        return playerInRegionBooleanMap.get(player.getUniqueId());
    }

    public void setAllPlayersIsNotOnRegion(){
        for (UUID uuid : playerInRegionBooleanMap.keySet()){
            playerInRegionBooleanMap.put(uuid, false);
        }
    }




}
