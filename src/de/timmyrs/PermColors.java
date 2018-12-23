package de.timmyrs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PermColors extends JavaPlugin implements CommandExecutor, Listener
{
	private final static HashMap<String, Scheme> schemes = new HashMap<>();
	private final static HashMap<Player, Scheme> playerCache = new HashMap<>();

	static String applyColor(String message)
	{
		return message == null ? "" : message.replace("&", "§").replace("§§", "§");
	}

	@Override
	public void onEnable()
	{
		getConfig().addDefault("schemes.default.color", "");
		getConfig().addDefault("schemes.default.prefix", "");
		getConfig().addDefault("schemes.op.color", "&6");
		getConfig().addDefault("schemes.op.prefix", "OP ");
		getConfig().addDefault("playerList.enabled", true);
		getConfig().addDefault("playerList.format", "%color%%player%");
		getConfig().addDefault("chat.enabled", true);
		getConfig().addDefault("chat.message", "%color%%prefix%&r&e%player%&r %message%");
		getConfig().addDefault("join.enabled", true);
		getConfig().addDefault("join.message", "%color%%prefix%&r&e%player% joined the game");
		getConfig().addDefault("quit.enabled", true);
		getConfig().addDefault("quit.message", "%color%%prefix%&r&e%player% left the game");
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadPermColorsConfig();
		getCommand("permcolors").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	private void reloadPermColorsConfig()
	{
		synchronized(schemes)
		{
			synchronized(playerCache)
			{
				schemes.clear();
				playerCache.clear();
				reloadConfig();
				for(String scheme : getConfig().getConfigurationSection("schemes").getValues(false).keySet())
				{
					schemes.put(scheme, new Scheme(getConfig().getString("schemes." + scheme + ".color"), getConfig().getString("schemes." + scheme + ".prefix")));
				}
				for(Player p : getServer().getOnlinePlayers())
				{
					applyColor(p);
				}
			}
		}
	}

	private Scheme getScheme(Player p)
	{
		if(p.isOp())
		{
			return schemes.get("op");
		}
		synchronized(playerCache)
		{
			if(playerCache.containsKey(p))
			{
				return playerCache.get(p);
			}
		}
		Scheme scheme = null;
		synchronized(schemes)
		{
			for(Map.Entry<String, Scheme> entry : schemes.entrySet())
			{
				if(!entry.getKey().equals("default") && !entry.getKey().equals("op") && p.hasPermission("permcolors.scheme." + entry.getKey()))
				{
					scheme = entry.getValue();
					break;
				}
			}
		}
		if(scheme == null)
		{
			scheme = schemes.get("default");
		}
		synchronized(playerCache)
		{
			playerCache.put(p, scheme);
		}
		return scheme;
	}

	private void applyColor(Player p)
	{
		if(getConfig().getBoolean("playerList.enabled"))
		{
			p.setPlayerListName(applyColor("playerList.format", p));
		}
	}

	private String applyColor(String path, Player p)
	{
		return getScheme(p).apply(applyColor(getConfig().getString(path))).replace("%player%", p.getName());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		applyColor(e.getPlayer());
		if(getConfig().getBoolean("join.enabled"))
		{
			e.setJoinMessage(applyColor("join.message", e.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(getConfig().getBoolean("quit.enabled"))
		{
			e.setQuitMessage(applyColor("quit.message", e.getPlayer()));
		}
		synchronized(playerCache)
		{
			playerCache.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e)
	{
		if(!e.isCancelled() && getConfig().getBoolean("chat.enabled"))
		{
			e.setFormat(getScheme(e.getPlayer()).apply(applyColor(getConfig().getString("chat.message"))).replace("%player%", "%1$s").replace("%message%", e.getPlayer().hasPermission("permcolors.colorfulchatmessages") ? applyColor(e.getMessage()) : "%2$s"));
		}
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a)
	{
		if(a.length > 0 && a[0].equalsIgnoreCase("reload") && s.hasPermission("permcolors.reload"))
		{
			reloadPermColorsConfig();
			s.sendMessage("§aPermColors has been reloaded.");
		}
		else if(a.length > 1 && a[0].equalsIgnoreCase("recolor") && s.hasPermission("permcolors.recolor"))
		{
			final Player p = getServer().getPlayer(a[1]);
			if(p != null && p.isOnline())
			{
				synchronized(playerCache)
				{
					playerCache.remove(p);
				}
				applyColor(p);
				s.sendMessage("§a" + p.getName() + " has been recolored.");
			}
			else
			{
				s.sendMessage("§4'" + a[0] + "' is not online.");
			}
		}
		else
		{
			s.sendMessage("https://github.com/timmyrs/PermColors");
		}
		return true;
	}
}

class Scheme
{
	private final String color;
	private final String prefix;

	Scheme(String color, String prefix)
	{
		this.color = PermColors.applyColor(color);
		this.prefix = PermColors.applyColor(prefix);
	}

	String apply(String message)
	{
		return message.replace("%color%", color).replace("%prefix%", prefix);
	}
}
