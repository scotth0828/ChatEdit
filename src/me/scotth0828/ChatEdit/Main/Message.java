package me.scotth0828.ChatEdit.Main;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

	Main main;

	HashMap<String, ChatColor> colors = new HashMap<>();

	public Message(Main main) {
		this.main = main;
		addColors();
	}

	public void send(Player ply, String msg) {
		ply.sendMessage(ChatColor.GOLD + "[ChatEdit] " + ChatColor.GREEN + msg);
	}

	public String ColoredMsg(String str) {

		char[] c = str.toCharArray();
		for (int x = 0; x < c.length; x++) {

			if (c[x] == '[') {

				for (int y = x; y < c.length; y++) {

					if (c[y] == ']') {
						if (colors.containsKey(str.substring(x + 1, y).toLowerCase())) {
							str = str.substring(0, x) + colors.get(str.substring(x + 1, y).toLowerCase())
									+ str.substring(y + 1, str.length());
							c = str.toCharArray();
							x = 0;
						}
					}

				}

			}

		}

		return str;
	}

	private void addColors() {
		colors.put("aqua", ChatColor.AQUA);
		colors.put("black", ChatColor.BLACK);
		colors.put("blue", ChatColor.BLUE);
		colors.put("darkaqua", ChatColor.DARK_AQUA);
		colors.put("darkblue", ChatColor.DARK_BLUE);
		colors.put("darkgray", ChatColor.DARK_GRAY);
		colors.put("darkgreen", ChatColor.DARK_GREEN);
		colors.put("darkpurple", ChatColor.DARK_PURPLE);
		colors.put("darkred", ChatColor.DARK_RED);
		colors.put("gold", ChatColor.GOLD);
		colors.put("gray", ChatColor.GRAY);
		colors.put("green", ChatColor.GREEN);
		colors.put("lightpurple", ChatColor.LIGHT_PURPLE);
		colors.put("red", ChatColor.RED);
		colors.put("white", ChatColor.WHITE);
		colors.put("yellow", ChatColor.YELLOW);
	}

}
