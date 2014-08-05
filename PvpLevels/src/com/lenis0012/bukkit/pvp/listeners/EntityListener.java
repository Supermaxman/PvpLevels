package com.lenis0012.bukkit.pvp.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.lenis0012.bukkit.pvp.PvpLevels;
import com.lenis0012.bukkit.pvp.PvpPlayer;

public class EntityListener implements Listener {
	private PvpLevels plugin;
	private Map<String, String> attackers = new HashMap<String, String>();
	private Map<String, String> killer = new HashMap<String, String>();
	
	public EntityListener(PvpLevels plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.isCancelled())
			return;
		
		Entity a = event.getEntity();
		Entity b = event.getDamager();
		
		if(a instanceof Player && b instanceof Player) {
			Player defender = (Player) a;
			Player attacker = (Player) b;
			this.attackers.put(defender.getName(), attacker.getName());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if(entity instanceof Player) {
			Player defender = (Player) entity;
			String dname = defender.getName();
			String aname = this.attackers.get(dname);
			Player attacker = aname == null ? null : Bukkit.getPlayer(aname);
			
			if(attacker != null && attacker.isOnline()) {
				PvpPlayer pp = plugin.getPlayer(attacker);
				PvpPlayer dpp = plugin.getPlayer(defender);
				
				if(killer.containsKey(aname)) {
					String value = killer.get(aname);
					String[] data = value.split(";");
					int allowed = plugin.getConfig().getInt("settings.kill-session");
					
					String cname = data[0];
					int current = Integer.valueOf(data[1]);
					
					if(dname.equals(cname)) {
						if(current >= allowed)
							return;
						else
							killer.put(aname, dname+';'+String.valueOf((current + 1)));
					} else
						killer.put(aname, dname+';'+'1');
				} else {
					killer.put(aname, dname+';'+'1');
				}
				
				int kills = pp.getKills();
				int killstreak = pp.getKillstreak() + 1;
				int lvl = pp.getLevel();
				kills += 1;
				pp.setKills(kills);
				pp.setKillstreak(killstreak);
				dpp.setDeaths(dpp.getDeaths() + 1);
				dpp.setKillstreak(0);
				//TODO custom messages
				double d = killstreak / 5.0D;
				
				if(String.valueOf(d).endsWith(".0") && PvpLevels.ENABLE_KILLSTREAK_MESSAGES) {
					//Bukkit.broadcastMessage("\247a" + aname + " has reached a killstreak of \2477" + killstreak + "\247a!");
					String message = plugin.getConfig().getString("killstreak-message", "&a[NAME] has reached a killstreak of &7[KILLSTREAK]&a!");
					message = format(plugin, message, Integer.toString(lvl), aname, Integer.toString(killstreak));
					Bukkit.broadcastMessage(message);
				}
				
				if(plugin.levelList.contains(kills)) {
					lvl += 1;
					pp.setLevel(lvl);
					pp.reward(attacker);
					if(PvpLevels.ENABLE_LEVEL_MESSAGES) {
						String message = plugin.getConfig().getString("new-level-message", "&a[NAME] has reached level &7[LEVEL]&a!");
						message = format(plugin, message, Integer.toString(lvl), aname, Integer.toString(killstreak));
						Bukkit.broadcastMessage(message);
					}
				}
			}
		}
	}
	
	public static String format(PvpLevels plugin, String s, String level, String name, String killstreak){
		String levelReplace = plugin.getConfig().getString("replace-string", "[LEVEL]");
		String nameReplace = plugin.getConfig().getString("replace-string2", "[NAME]");
		String killStreakReplace = plugin.getConfig().getString("replace-string3", "[KILLSTREAK]");
		return ChatColor.translateAlternateColorCodes('&', s).replace(levelReplace, level).replace(nameReplace, name).replace(killStreakReplace, killstreak);
	}
}