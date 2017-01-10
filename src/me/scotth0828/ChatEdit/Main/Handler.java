package me.scotth0828.ChatEdit.Main;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class Handler implements Listener {

	Main main;

	public Handler(Main main) {
		this.main = main;
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {

		String rawMessage = e.getMessage();
		String fMessage = String.format(e.getFormat(), e.getPlayer().getDisplayName(), rawMessage);

		String type = main.users.getData().getString(e.getPlayer().getUniqueId() + ".type");

		if (type.equals(""))
			type = (String) main.getConfig().getList("ChatEdit.ChatType").get(0);

		boolean targeted = false;

		for (Player pl : e.getRecipients()) {

			if (rawMessage.toLowerCase().contains(pl.getDisplayName().toLowerCase())
					&& !e.getPlayer().getDisplayName().toLowerCase().equals(pl.getDisplayName().toLowerCase()))
				targeted = true;

			String PLType = main.users.getData().getString(pl.getUniqueId() + ".type");

			if (PLType.equals(""))
				PLType = (String) main.getConfig().getList("ChatEdit.ChatType").get(0);

			if (main.users.getData().getBoolean(pl.getUniqueId() + ".chatadmin") && !e.getPlayer().equals(pl)) {
				pl.sendMessage(ChatColor.RED + "[" + type + "] " + ChatColor.WHITE + e.getPlayer().getDisplayName()
						+ ": " + ChatColor.AQUA + rawMessage);
			} else {

				if (!targeted) {

					if (PLType.equals("nearby")) {
						if (pl.getWorld() == e.getPlayer().getWorld()
								&& pl.getPlayer().getLocation().distance(e.getPlayer().getLocation()) <= main
										.getConfig().getInt("ChatEdit.Default.Radius")) {
							if (type.equals("nearby"))
								pl.sendMessage(fMessage);
						}
					} else if (!PLType.equals("off")) {
						List<String> types = main.getConfig().getStringList("ChatEdit.ChatType");
						for (String s : types) {
							if (type.equals(s) && PLType.equals(s)) {
								pl.sendMessage(fMessage);
							}
						}
					}
				} else {
					pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 3.0F, 0.5F);
					fMessage = String.format(e.getFormat(), e.getPlayer().getDisplayName(),
							ChatColor.GOLD + rawMessage);
					pl.sendMessage(fMessage);
					targeted = false;
				}

			}

		}

		e.getRecipients().clear();

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (main.users.getData().getString(e.getPlayer().getUniqueId() + ".type") == null) {
			main.users.getData().set(e.getPlayer().getUniqueId() + ".type",
					main.getConfig().getList("ChatEdit.ChatType").get(0));
			main.users.saveData();
		}
		if (main.users.getData().getString(e.getPlayer().getUniqueId() + ".radius") == null) {
			main.users.getData().set(e.getPlayer().getUniqueId() + ".radius",
					main.getConfig().getString("ChatEdit.Default.Radius"));
			main.users.saveData();
		}

		if (main.users.getData().getString(e.getPlayer().getUniqueId() + ".chatadmin") == null) {
			main.users.getData().set(e.getPlayer().getUniqueId() + ".chatadmin", false);
			main.users.saveData();
		}

		e.getPlayer().sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN + " Your chat type is set to "
				+ ChatColor.BLUE + main.users.getData().getString(e.getPlayer().getUniqueId() + ".type"));

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if (e.getInventory().getTitle().equals(ChatColor.BLUE + "Chat Types")) {
			List<String> types = main.getTypes();

			for (int i = 0; i < types.size(); i++) {
				if (e.getSlot() == i) {
					e.setCancelled(true);
					main.users.getData().set(e.getWhoClicked().getUniqueId() + ".type", types.get(i));
					e.getWhoClicked().sendMessage(ChatColor.YELLOW + "[ChatEdit]" + ChatColor.GREEN
							+ " You will now be able to see all chat messages in " + ChatColor.BLUE + types.get(i));
					e.getWhoClicked().closeInventory();
				}
			}
		}

	}

}
