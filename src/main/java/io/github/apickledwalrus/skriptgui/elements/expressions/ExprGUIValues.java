package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.events.wrapper.InventoryCloseWrapper;
import ch.njol.skript.events.wrapper.InventoryOpenWrapper;
import ch.njol.skript.events.wrapper.InventoryPreClickWrapper;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ClickType;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecGUIOpenClose;
import io.github.apickledwalrus.skriptgui.elements.sections.SecMakeGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.click.Click;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("GUI Values")
@Description("Different utility values for a GUI. Some are available in vanilla Skript. Not all values are available for the GUI close section.")
@Examples({
		"create a gui with virtual chest 3 row inventory:",
		"\tmake gui 10 with water bucket:",
		"\t\tset the gui cursor item to lava bucket"
})
@Since("1.0.0")
public class ExprGUIValues extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprGUIValues.class, Object.class, ExpressionType.SIMPLE,
				"[the] gui slot",
				"[the] gui hotbar slot",
				"[the] gui inventory",
				"[the] gui click (type|action)",
				"[the] gui cursor [item]",
				"[the] gui [(clicked|current)] item",
				"[the] gui player",
				"[the] gui (viewer|player)s",
				"[the] gui slot id",
				"[the] gui"
		);
	}

	private int pattern;
	private boolean isDelayed;
	// Whether the expression is being used in an open/close section
	private boolean openClose;

	private String toString = "gui values";

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		SkriptEvent skriptEvent = getParser().getCurrentSkriptEvent();
		if (!(matchedPattern == 9 && getParser().isCurrentSection(SecCreateGUI.class)) && !(skriptEvent instanceof SectionSkriptEvent && ((SectionSkriptEvent) skriptEvent).isSection(SecMakeGUI.class, SecGUIOpenClose.class))) {
			Skript.error("You can't use '" + parseResult.expr + "' outside of a GUI make or open/close section.");
			return false;
		}

		openClose = skriptEvent instanceof SectionSkriptEvent && ((SectionSkriptEvent) skriptEvent).isSection(SecGUIOpenClose.class);

		pattern = matchedPattern;
		if (openClose && matchedPattern != 2 && matchedPattern != 6 && matchedPattern != 7 && matchedPattern != 9) {
			Skript.error("You can't use '" + parseResult.expr + "' in a GUI open/close section.");
			return false;
		}

		this.isDelayed = !isDelayed.isFalse(); // TRUE or UNKNOWN
		toString = parseResult.expr;

		return true;
	}

	@Override
	protected Object[] get(Event event) {
		if (pattern == 9) {
			GUI gui = SkriptGUI.getGUIManager().getGUI(event);
			return gui != null ? new GUI[]{gui} : new GUI[0];
		}

		if (openClose) {
			InventoryEvent e = getInventoryEvent(event);
			switch (pattern) {
				case 2:
					return new AbstractInventory[]{e.getInventory()};
				case 6:
					// Ugly but oh well
					return new Player[]{getEventOwner(e)};
				case 7:
					return e.getInventory().getViewers().toArray();
			}
		} else {
			InventoryPreClickEvent e = ((InventoryPreClickWrapper) event).getEvent();
			Click click = e.getClick();
			switch (pattern) {
				case 0:
					return new Number[]{e.getSlot()};
				case 1:
					int num = -1;
					if (click instanceof Click.HotbarSwap(int hotbarSlot, _)) num = hotbarSlot;
					return new Number[]{num};
				case 2:
					return new AbstractInventory[]{e.getInventory()};
				case 3:
					ClickType type = ClickType.getType(e.getClick());
					return type != null ? new ClickType[]{type} : new ClickType[0];
				case 4:
					return new Item[]{new Item(e.getPlayer().getInventory().getCursorItem())};
				case 5:
					return new Slot[]{new Slot(e.getClickedItem(), e.getInventory(), e.getSlot())};
				case 6:
					return new Player[]{e.getPlayer()};
				case 7:
					return e.getInventory().getViewers().toArray();
				case 8:
					GUI gui = SkriptGUI.getGUIManager().getGUI(event);
					return gui != null ? new String[]{"" + gui.convert(e.getSlot())} : new String[0];
			}
		}
		return new Object[0];
	}

	private InventoryEvent getInventoryEvent(Event event) {
		if (event instanceof InventoryOpenWrapper wrapper) return wrapper.getEvent();
		else return ((InventoryCloseWrapper) event).getEvent();
	}

	private Player getEventOwner(InventoryEvent event) {
		if (event instanceof InventoryOpenEvent e) return e.getPlayer();
		else return ((InventoryCloseEvent) event).getPlayer();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (isDelayed) {
			Skript.error("You can't set the '" + toString + "' when the event is already passed.");
			return null;
		}

		if (mode == ChangeMode.SET && pattern == 4) {
			return CollectionUtils.array(Item.class);
		}

		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		if (delta == null || !(event instanceof InventoryPreClickWrapper wrapper)) {
			return;
		}
		wrapper.getEvent().getPlayer().getInventory().setCursorItem(((Item) delta[0]).getItem());
	}

	@Override
	public boolean isSingle() {
		return pattern != 7;
	}

	@Override
	public Class<?> getReturnType() {
        return switch (pattern) {
            case 0, 1 -> Number.class;
            case 3 -> ClickType.class;
            case 4 -> Item.class;
            case 5 -> Slot.class;
            case 6, 7 -> Player.class;
            case 8 -> String.class;
            case 9 -> GUI.class;
            default -> Object.class;
        };
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return toString;
	}

}
