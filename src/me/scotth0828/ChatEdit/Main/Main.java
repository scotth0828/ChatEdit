package me.scotth0828.ChatEdit.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	SettingsManager users;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new Handler(this), this);
		loadConfiguration();
	}

	@Override
	public void onDisable() {
		saveConfig();
		users.saveData();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("ChatType")) {

				if (args.length > 1) {
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED
							+ " You must either input global, nearby, or off as the value.");
					return false;
				} else if (args.length == 0) {
					player.sendMessage(
							ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " Your current chat type is set to "
									+ ChatColor.BLUE + users.getData().getString(player.getUniqueId() + ".type"));
					return true;
				}

				if (args[0].toLowerCase().equals("global")) {
					users.getData().set(player.getUniqueId() + ".type", "global");
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN
							+ " You will now be able to see all chat messages!");
				} else if (args[0].toLowerCase().equals("nearby")) {
					users.getData().set(player.getUniqueId() + ".type", "nearby");
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN
							+ " You will now be able to see all nearby chat messages within your set radius!");
				} else if (args[0].toLowerCase().equals("off")) {
					users.getData().set(player.getUniqueId() + ".type", "off");
					player.sendMessage(
							ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " You will now see no chat messages!");
				} else {
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED
							+ " That chat type does not exist! Either use global, nearby, or off as a value!");
				}

				users.saveData();

				return true;

			}

			if (cmd.getName().equalsIgnoreCase("ChatR")) {

				if (args.length > 1) {
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED
							+ " You must input the radius you want, the default is "
							+ getConfig().getString("ChatEdit.Default.Radius") + ".");
					return false;
				} else if (args.length == 0) {
					player.sendMessage(
							ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " Your current radius is set to "
									+ ChatColor.BLUE + users.getData().getString(player.getUniqueId() + ".radius"));
					return true;
				}

				if (isStringInt(args[0])) {
					int num = Integer.parseInt(args[0]);
					users.getData().set(player.getUniqueId() + ".radius", num);
					player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN
							+ " Your radius has been set to " + args[0]);
					users.saveData();

					return true;
				} else {
					player.sendMessage(
							ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED + " Your radius must be a number!");
					return false;
				}

			}

			if (cmd.getName().equalsIgnoreCase("ChatEditReload")) {

				loadConfiguration();
				player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " Reloaded Successfully!");

				return true;
			}

			if (cmd.getName().equalsIgnoreCase("ChatF")) {
				if (args.length > 2 || args.length < 2) {
					return false;
				}

				String target = args[0];
				String type = args[1].toLowerCase();

				for (Player p : getServer().getOnlinePlayers()) {
					if (p.getDisplayName().equals(target)) {

						if (!type.equals("global") && !type.equals("nearby") && !type.equals("off")) {
							player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED
									+ " Your must either use global, nearby, or off as a type!");
							return true;
						}

						users.getData().set(p.getUniqueId() + ".type", type);
						player.sendMessage(
								ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " You have set " + ChatColor.GOLD
										+ p.getDisplayName() + ChatColor.GREEN + " to " + ChatColor.BLUE + type);
						p.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN
								+ " Your chat type has been set to " + ChatColor.BLUE + type);
						return true;
					} else {
						if (player.getDisplayName().equals(target)) {
							player.sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED
									+ " Change your type using the proper command!");
						} else {
							player.sendMessage(
									ChatColor.YELLOW + "[ChatEdit]" + ChatColor.RED + " That is not a valid player!");
						}
					}
					return true;
				}
				return true;
			}
		}

		return false;
	}

	public boolean isStringInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public void loadConfiguration() {
		getConfig().addDefault("ChatEdit.Default.ChatType", "global");
		getConfig().addDefault("ChatEdit.Default.Radius", 200);
		getConfig().options().copyDefaults(true);
		saveConfig();
		users = new SettingsManager(this, "users");
		users.saveData();
	}

}
