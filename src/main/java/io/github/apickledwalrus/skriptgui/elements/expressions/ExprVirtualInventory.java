package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import static ch.njol.skript.effects.EffOpenInventory.getDefaultTitle;


@Name("Virtual Inventory")
@Description("An expression to create inventories that can be used with GUIs.")
@Examples("create a gui with virtual chest inventory with 3 rows named \"My GUI\"")
@Since("1.0.0")
public class ExprVirtualInventory extends SimpleExpression<AbstractInventory>{

	static {
		Skript.registerExpression(ExprVirtualInventory.class, AbstractInventory.class, ExpressionType.SIMPLE,
				"virtual %inventorytype% [inventory] [(named|with (name|title)) %-component%]"
		);
	}

	private Expression<InventoryType> inventoryType;
	@Nullable
	private Expression<Component> name;

	// The name of this inventory.
	@Nullable
	private Component invName;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		name = (Expression<Component>) exprs[1];
		return true;
	}

	@Override
	protected Inventory[] get(Event e) {
		InventoryType type = inventoryType.getSingle(e);
		if (type == null) {
			return new Inventory[0];
		}

		Component name = this.name != null ? this.name.getSingle(e) : null;
		invName = name != null ? name : getDefaultTitle(type);

		Inventory inventory = new Inventory(type, invName);

		return new Inventory[]{inventory};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends AbstractInventory> getReturnType() {
		return AbstractInventory.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "virtual " + inventoryType.toString(e, debug) + " inventory" + (name == null ? "" : " named " + name.toString(e, debug));
	}

	/**
	 * @return The name of this inventory. If {@link #invName} is null
	 * when this method is called, an empty string will be returned.
	 */
	public Component getName() {
		return invName != null ? invName : Component.empty();
	}

}
