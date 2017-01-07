package me.scotth0828.ChatEdit.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
			type = "global";

		for (Player pl : e.getRecipients()) {

			String PLType = main.users.getData().getString(pl.getUniqueId() + ".type");

			if (PLType.equals(""))
				PLType = "global";

			if (PLType.equals("global")) {
				pl.sendMessage(fMessage);
			} else if (PLType.equals("nearby")) {
				if (pl.getPlayer().getLocation().distance(e.getPlayer().getLocation()) <= main.getConfig()
						.getInt("ChatEdit.Default.Radius")) {
					pl.sendMessage(fMessage);
				}
			} else if (PLType.equals("off")) {
				return;
			}

		}

		e.getRecipients().clear();

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (main.users.getData().getString(e.getPlayer().getUniqueId() + ".type") == null) {
			main.users.getData().set(e.getPlayer().getUniqueId() + ".type",
					main.getConfig().getString("ChatEdit.Default.ChatType"));
			main.users.saveData();
		}
		if (main.users.getData().getString(e.getPlayer().getUniqueId() + ".radius") == null) {
			main.users.getData().set(e.getPlayer().getUniqueId() + ".radius",
					main.getConfig().getString("ChatEdit.Default.Radius"));
			main.users.saveData();
		}

	}

}
