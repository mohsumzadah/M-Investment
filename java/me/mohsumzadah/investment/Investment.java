package me.mohsumzadah.investment;

import me.mohsumzadah.investment.Listeners.InventoryListener;
import me.mohsumzadah.investment.Listeners.PlayerMoveRegion;
import me.mohsumzadah.investment.Listeners.ToolListener;
import me.mohsumzadah.investment.commands.CommandCompilator;
import me.mohsumzadah.investment.commands.CommandController;
import me.mohsumzadah.investment.manager.CoolDownManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class Investment extends JavaPlugin {

    public String pluginName = ChatColor.WHITE + "(" +
            ChatColor.GOLD + "M" +
            ChatColor.GRAY + "-" +
            ChatColor.AQUA + "INVESTMENT" +
            ChatColor.WHITE + ") : ";

    public static Investment plugin;
    private static Economy econ = null;

    CoolDownManager coolDownManager;


    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            System.out.println("Vault plugin not found. Disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        plugin = this;
        coolDownManager = new CoolDownManager(plugin);


        createSettings();
        createGuiFile();
        createInvestFile();
        createPlayerDataFile();
        createMessageFile();


        getCommand("investment").setExecutor(new CommandController());
        getCommand("investment").setTabCompleter(new CommandCompilator());

        getServer().getPluginManager().registerEvents(new ToolListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveRegion(), this);


    }

    public Location block1 = null;
    public Location block2 = null;

    //CONFIG FILE
    private File settingsF;
    public FileConfiguration settings;

    private void createSettings(){
        settingsF = new File(getDataFolder(), "settings.yml");
        if(!settingsF.exists()){
            settingsF.getParentFile().mkdirs();
            saveResource("settings.yml",false);
        }
        settings = new YamlConfiguration();

        try {
            settings.load(settingsF);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public void saveSettings(){
        try {
            settings.save(settingsF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GUI FILE
    private File guif;
    public FileConfiguration gui;
    private void createGuiFile(){
        guif = new File(getDataFolder(), "gui.yml");
        if(!guif.exists()){
            guif.getParentFile().mkdirs();
            saveResource("gui.yml",false);
        }
        gui = new YamlConfiguration();
        try {
            gui.load(guif);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public void saveGuiConfig(){
        try {
            gui.save(guif);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // INVESTMENT FILE
    private File investf;
    public FileConfiguration invest;
    private void createInvestFile(){
        investf = new File(getDataFolder(), "investments.yml");
        if(!investf.exists()){
            investf.getParentFile().mkdirs();
            saveResource("investments.yml",false);
        }
        invest = new YamlConfiguration();
        try {
            invest.load(investf);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public void saveInvestConfig(){
        try {
            invest.save(investf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File messagef;
    public FileConfiguration message;
    private void createMessageFile(){
        messagef = new File(getDataFolder(), "messages.yml");
        if(!messagef.exists()){
            messagef.getParentFile().mkdirs();
            saveResource("messages.yml",false);
        }
        message = new YamlConfiguration();
        try {
            message.load(messagef);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }

    // PLAYER_DATA FILE
    private File player_dataf;
    public FileConfiguration player_data;
    private void createPlayerDataFile(){
        player_dataf = new File(getDataFolder(), "players_data.yml");
        if(!player_dataf.exists()){
            player_dataf.getParentFile().mkdirs();
            saveResource("players_data.yml",false);
        }
        player_data = new YamlConfiguration();
        try {
            player_data.load(player_dataf);
            if(player_data.getConfigurationSection("players") != null
                    && player_data.getConfigurationSection("players").getKeys(false).size() > 0) {

                for (String uuid : player_data.getConfigurationSection("players").getKeys(false)) {
                    coolDownManager.playerCooldownMap.put(UUID.fromString(uuid),
                            player_data.getInt("players." + uuid + ".second_left"));
                    coolDownManager.playerInRegionBooleanMap.put(UUID.fromString(uuid),
                            player_data.getBoolean("players." + uuid + ".boolean"));
                    coolDownManager.playerInvestmentType.put(UUID.fromString(uuid),
                            player_data.getString("players." + uuid + ".invest_type"));
                }
            }
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public void savePlayerDataConfig(){
        try {
            player_data.save(player_dataf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public CoolDownManager getCoolDownManager(){
        return coolDownManager;
    }

    public String returnReplaceMessage(Boolean skipName, String path, String replace_key, String replace_text){
        if (skipName){
            return (ChatColor.translateAlternateColorCodes('&',
                            Investment.plugin.message
                                    .getString(path)
                                    .replaceAll("%"+replace_key+"%", replace_text)));
        }else {
            return (pluginName + ChatColor.translateAlternateColorCodes('&',
                            Investment.plugin.message
                                    .getString(path)
                                    .replaceAll("%"+replace_key+"%", replace_text)));
        }
    }
    public String returnMessage(Boolean skipName,String path){
        if(skipName){
            return ChatColor.translateAlternateColorCodes('&',
                    Investment.plugin.message
                            .getString(path));
        }else {
            return pluginName + ChatColor.translateAlternateColorCodes('&',
                    Investment.plugin.message
                            .getString(path));
        }

    }
}
