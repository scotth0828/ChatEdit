package me.scotth0828.ChatEdit.Main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {
	
	Main main;
	
	FileConfiguration data;
	File dfile;
	
	public SettingsManager(Main main, String fileName) {
		
		this.main = main;
		
		if (!main.getDataFolder().exists()) {
			main.getDataFolder().mkdir();
		}
		
		dfile = new File(main.getDataFolder(), fileName+".yml");
		
		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + dfile.getName());
			}
		}
		
		data = YamlConfiguration.loadConfiguration(dfile);
	}
	
	public FileConfiguration getData() {
		return data;
	}
	
	public void saveData() {
		try {
			data.save(dfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save " + dfile.getName());
		}
	}
	
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}
	
	public PluginDescriptionFile getDesc() {
		return main.getDescription();
	}
}