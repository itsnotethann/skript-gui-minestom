package io.github.apickledwalrus.skriptgui;

import io.github.apickledwalrus.skriptgui.gui.GUI;
import net.minestom.server.inventory.AbstractInventory;
import org.skriptlang.skript.lang.converter.Converters;

public class SkriptConverters {

	public SkriptConverters() {

		Converters.registerConverter(GUI.class, AbstractInventory.class, GUI::getInventory);

	}

}
