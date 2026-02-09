package io.github.apickledwalrus.skriptgui;

import java.io.IOException;

import io.github.apickledwalrus.skriptgui.gui.events.GUIEvents;
import io.github.apickledwalrus.skriptgui.gui.events.RecipeEvent;
import net.minestom.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;

public class SkriptGUI extends JavaPlugin {

	@SuppressWarnings("NotNullFieldNotInitialized")
	private static SkriptGUI instance;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static GUIManager manager;

	@Override
	public void onEnable() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		Plugin skript = pluginManager.getPlugin("Skript");
		//Version minimumSupportedVersion = new Version(2, 10, 2);
		if (skript == null) {
			// Skript doesn't exist within the server plugins folder
			getLogger().severe("Could not find Skript! Make sure you have it installed. Disabling...");
			/*getLogger().severe("skript-gui requires Skript " + minimumSupportedVersion + " or newer! Download Skript releases at https://github.com/SkriptLang/Skript/releases");*/
			setEnabled(false);
			return;
		} else if (!skript.isEnabled()) {
			// Skript is disabled on the server
			getLogger().severe("Skript failed to properly enable and is disabled on the server. Disabling...");
			setEnabled(false);
			return;
		}
		instance = this;

		SkriptAddon addon = Skript.registerAddon(this);
		try {
			addon.loadClasses("io.github.apickledwalrus.skriptgui.elements");
			addon.setLanguageFileDirectory("lang");
			new SkriptClasses(); // Register ClassInfos
			new SkriptConverters(); // Register Converters
		} catch (IOException e) {
			getLogger().severe("An error occured while trying to load the addon's elements. The addon will be disabled.");
			getLogger().severe("Printing StackTrace:");
			e.printStackTrace();
			setEnabled(false);
			return;
		}

		// Register manager and events
		manager = new GUIManager();
		GUIEvents.register(MinecraftServer.getGlobalEventHandler());

	}

	public static SkriptGUI getInstance() {
		return instance;
	}

	public static GUIManager getGUIManager() {
		return manager;
	}

}
