package io.github.apickledwalrus.skriptgui;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.InventoryType;
import io.github.apickledwalrus.skriptgui.gui.GUI;

public class SkriptClasses {

	public SkriptClasses() {

		Classes.registerClass(new ClassInfo<>(GUI.class, "guiinventory")
			.user("gui inventor(y|ies)?")
			.name("GUI")
			.description("Represents a skript-gui GUI")
			.examples("See the GUI creation section.")
			.since("1.0")
			.parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext ctx) {
                    return false;
                }

                @Override
                public String toString(GUI gui, int flags) {
                    return Classes.toString(InventoryType.of(gui.getInventory().getInventoryType()))
                        + " gui named " + Classes.toString(gui.getName())
                        + " and shape " + gui.getRawShape();
                }

                @Override
                public String toVariableNameString(GUI gui) {
                    return toString(gui, 0);
                }
            })
		);

	}

}
