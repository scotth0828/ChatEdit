package me.scotth0828.ChatEdit.Main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	SettingsManager users;
	Message msg;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new Handler(this), this);
		loadConfiguration();

		msg = new Message(this);
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
					msg.send(player, ChatColor.RED + "You must input one of the chat types.");
					return false;
				} else if (args.length == 0) {
					msg.send(player, "Your current chat type is set to " + ChatColor.BLUE
							+ users.getData().getString(player.getUniqueId() + ".type"));
					return true;
				}

				if (args[0].toLowerCase().equals("nearby")) {
					users.getData().set(player.getUniqueId() + ".type", "nearby");
					msg.send(player, "You will now be able to see all nearby chat messages within your set radius!");
				} else if (args[0].toLowerCase().equals("off")) {
					users.getData().set(player.getUniqueId() + ".type", "off");
					msg.send(player, "You will now see no chat messages!");
				} else {

					List<String> types = getConfig().getStringList("ChatEdit.ChatType");

					for (String s : types) {
						if (args[0].toLowerCase().equals(s)) {
							users.getData().set(player.getUniqueId() + ".type", s);
							msg.send(player, "You will now be able to see all chat messages in " + ChatColor.BLUE + s);
							return true;
						}
					}

					msg.send(player,
							ChatColor.RED + "That chat type does not exist! Use one of the chat types as a value!");
				}

				users.saveData();

				return true;

			}

			if (cmd.getName().equalsIgnoreCase("ChatR")) {

				if (args.length > 1) {
					msg.send(player, ChatColor.RED + "You must input the radius you want, the default is "
							+ getConfig().getString("ChatEdit.Default.Radius") + ".");
					return false;
				} else if (args.length == 0) {
					msg.send(player, "Your current radius is set to " + ChatColor.BLUE
							+ users.getData().getString(player.getUniqueId() + ".radius"));
					return true;
				}

				if (isStringInt(args[0])) {
					int num = Integer.parseInt(args[0]);
					users.getData().set(player.getUniqueId() + ".radius", num);
					msg.send(player, "Your radius has been set to " + args[0]);
					users.saveData();

					return true;
				} else {
					msg.send(player, ChatColor.RED + "Your radius must be a number!");
					return false;
				}

			}

			if (cmd.getName().equalsIgnoreCase("ChatEditReload")) {
				reloadConfiguration();
				msg.send(player, "Reloaded Successfully!");

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

						List<String> types = getConfig().getStringList("ChatEdit.ChatType");

						types.add("nearby");
						types.add("off");

						for (int i = 0; i < types.size(); i++) {

							if (type.equals(types.get(i))) {
								break;
							} else if (i == types.size() - 1 && !type.equals(types.get(i))) {
								msg.send(player, ChatColor.RED + "You must input one of the chat types.");
								return true;
							}
						}

						users.getData().set(p.getUniqueId() + ".type", type);
						msg.send(player, "You have set " + ChatColor.GOLD + p.getDisplayName() + ChatColor.GREEN
								+ " to " + ChatColor.BLUE + type);
						msg.send(p, "Your chat type has been set to " + ChatColor.BLUE + type);
						return true;
					}
				}
				msg.send(player, ChatColor.RED + "That is not a valid player!");
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("ChatAdmin")) {
				if (users.getData().getBoolean(player.getUniqueId() + ".chatadmin")) {
					users.getData().set(player.getUniqueId() + ".chatadmin", false);
					msg.send(player, "You will now only see your current chat type!");
				} else {
					users.getData().set(player.getUniqueId() + ".chatadmin", true);
					msg.send(player, "You will now view all chat types!");
				}
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("ChatL")) {
				List<String> types = getTypes();

				ItemStack t = new ItemStack(Material.ENCHANTED_BOOK);
				ItemMeta meta = t.getItemMeta();

				Inventory inv = Bukkit.createInventory(null, 36, ChatColor.BLUE + "Chat Types");

				for (int i = 0; i < types.size(); i++) {
					meta.setDisplayName(ChatColor.DARK_PURPLE + types.get(i));
					List<String> r = new ArrayList<>();
					int amount = 0;
					int online = getServer().getOnlinePlayers().size();
					for (Player p : getServer().getOnlinePlayers()) {
						if (users.getData().getString(p.getUniqueId() + ".type").equals(types.get(i))) {
							amount++;
						}
					}
					if (!types.get(i).equals("nearby") && !types.get(i).equals("off"))
						r.add(amount + "/" + online + " in chat");
					meta.setLore(r);
					t.setItemMeta(meta);
					inv.setItem(i, t);
				}

				player.openInventory(inv);

				return true;
			}
		}

		return false;
	}
	
	public boolean hasPerm(Player ply, String permission) {
		if(ply.hasPermission(permission)) {
			return true;
		}
		return false;
	}

	public List<String> getTypes() {
		List<String> types = getConfig().getStringList("ChatEdit.ChatType");

		types.add("nearby");
		types.add("off");

		return types;
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
		saveDefaultConfig();
		users = new SettingsManager(this, "users");
		users.saveData();
	}

	public void reloadConfiguration() {
		this.reloadConfig();
		users.reloadData();
	}

}
