package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ComponentWrapper;
import ch.njol.skript.util.InventoryType;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import static ch.njol.skript.effects.EffOpenInventory.getDefaultTitle;


@Name("Virtual Inventory")
@Description("An expression to create inventories that can be used with GUIs.")
@Examples("create a gui with virtual chest 3 row inventory named \"My GUI\"")
@Since("1.0.0")
public class ExprVirtualInventory extends SimpleExpression<AbstractInventory>{

	static {
		Skript.registerExpression(ExprVirtualInventory.class, AbstractInventory.class, ExpressionType.COMBINED,
				"virtual %inventorytype% [inventory] [(named|with (name|title)) %-component%]"
		);
	}

	private Expression<InventoryType> inventoryType;
	@Nullable
	private Expression<ComponentWrapper> name;

	// The name of this inventory.
	@Nullable
	private Component invName;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		inventoryType = (Expression<InventoryType>) exprs[0];
		name = (Expression<ComponentWrapper>) exprs[1];

		if (inventoryType instanceof Literal<InventoryType> literal && literal.getSingle() == InventoryType.PLAYER) {
			Skript.error("Cannot create virtual inventory of type 'player'.");
			return false;
		}
		return true;
	}

	@Override
	protected Inventory[] get(Event e) {
		InventoryType type = inventoryType.getSingle(e);
		if (type == null || type == InventoryType.PLAYER) {
			return new Inventory[0];
		}


		Component name = ComponentWrapper.getOrElse(this.name, e, null);
		invName = name != null ? name : getDefaultTitle(type);

        //noinspection DataFlowIssue
        Inventory inventory = new Inventory(type.getMinestomType(), invName);

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
