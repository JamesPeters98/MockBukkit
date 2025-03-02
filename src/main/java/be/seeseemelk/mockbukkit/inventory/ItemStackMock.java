package be.seeseemelk.mockbukkit.inventory;

import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock;
import be.seeseemelk.mockbukkit.world.InteractionResult;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;

public class ItemStackMock extends ItemStack
{

	private ItemType type = ItemTypeMock.AIR;
	private int amount = 1;
	private ItemMeta itemMeta = new ItemMetaMock();
	private short durability;

	private static final ItemStackMock EMPTY = new ItemStackMock((Void) null);
	private static final String ITEMMETA_INITIALIZATION_ERROR = "Failed to instanciate item meta class ";

	//Utility
	protected ItemStackMock()
	{
	}

	public ItemStackMock(@NotNull Material type)
	{
		this(type, 1);
	}

	public ItemStackMock(@NotNull ItemStack stack) throws IllegalArgumentException
	{
		this.type = stack.getType().asItemType();
		this.amount = stack.getAmount();
		this.durability = type.getMaxDurability();
		if (type.asMaterial() != Material.AIR && type.getItemMetaClass() != ItemMeta.class)
		{
			try
			{
				this.itemMeta = type.getItemMetaClass().getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				   NoSuchMethodException e)
			{
				throw new RuntimeException(ITEMMETA_INITIALIZATION_ERROR + type.getItemMetaClass(), e);
			}
		}

	}

	public ItemStackMock(@NotNull Material type, int amount)
	{
		this.type = type.asItemType();
		this.amount = amount;
		this.durability = type.getMaxDurability();
		if (type != Material.AIR && type.asItemType().getItemMetaClass() != ItemMeta.class)
		{
			try
			{
				this.itemMeta = type.asItemType().getItemMetaClass().getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				   NoSuchMethodException e)
			{
				throw new RuntimeException(ITEMMETA_INITIALIZATION_ERROR + type.asItemType().getItemMetaClass(), e);
			}
		}
	}

	private ItemStackMock(@Nullable Void v)
	{
		this.type = ItemTypeMock.AIR;
		this.amount = 0;
	}

	private ItemStackMock(@NotNull ItemType type)
	{
		this.type = type;
		this.durability = type.getMaxDurability();
		if (type.asMaterial() != Material.AIR && type.getItemMetaClass() != ItemMeta.class)
		{
			try
			{
				this.itemMeta = type.getItemMetaClass().getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				   NoSuchMethodException e)
			{
				throw new RuntimeException(ITEMMETA_INITIALIZATION_ERROR + type.getItemMetaClass(), e);
			}
		}

	}

	/**
	 * Creates a new ItemStackMock instance if it isn't already mocked
	 *
	 * @param itemStack itemStack to be mocked
	 * @return mocked itemStack
	 */
	public static ItemStackMock mock(ItemStack itemStack)
	{
		if (itemStack instanceof ItemStackMock itemStackMock)
		{
			return itemStackMock;
		}
		else
		{
			return new ItemStackMock(itemStack);
		}
	}

	@Override
	public void setType(@NotNull Material type)
	{
		this.type = type.asItemType();
	}

	@NotNull
	public Material getType()
	{
		return this.type.asMaterial();
	}

	@Override
	public int getAmount()
	{
		return this.amount;
	}

	@Override
	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	@Override
	public boolean isEmpty()
	{
		return this == EMPTY || this.type == ItemTypeMock.AIR || this.amount <= 0;
	}

	@Override
	public boolean setItemMeta(@org.jetbrains.annotations.Nullable ItemMeta itemMeta)
	{
		if (this.type == ItemTypeMock.AIR) return false;
		this.itemMeta = itemMeta;
		return true;
	}

	@Override
	public ItemMeta getItemMeta()
	{
		return this.itemMeta;
	}

	@Override
	public boolean hasItemMeta()
	{
		return this.itemMeta != null && !Bukkit.getItemFactory().equals(itemMeta, null);
	}

	@Override
	public int getMaxStackSize()
	{
		return this.type.getMaxStackSize();
	}

	@Override
	public short getDurability()
	{
		return this.durability;
	}

	@Override
	public void setDurability(short durability)
	{
		this.durability = (short) Math.min(Math.max(durability, 0), this.type.getMaxDurability());
	}

	@Override
	public void addUnsafeEnchantment(@NotNull Enchantment ench, int level)
	{
		Preconditions.checkArgument(ench != null, "Enchantment cannot be null");

		final ItemMeta meta = this.getItemMeta();
		if (meta != null)
		{
			meta.addEnchant(ench, level, true);
			this.setItemMeta(meta);
		}
	}

	@Override
	public boolean isSimilar(@org.jetbrains.annotations.Nullable ItemStack stack)
	{
		if (stack == null) return false;
		if (!(stack instanceof final ItemStackMock bukkit)) return false;
		if (this == bukkit) return true;
		return this.type == bukkit.type;
	}

	public static ItemStackMock empty()
	{
		return EMPTY;
	}

	@Override
	public @NotNull ItemStack clone()
	{
		ItemStackMock clone = new ItemStackMock(this.type);

		clone.setAmount(this.amount);
		clone.setDurability(this.durability);
		clone.setItemMeta(this.itemMeta);
		return clone;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;
		if (!(obj instanceof final ItemStackMock bukkit)) return false;
		return isSimilar(bukkit) && this.amount == bukkit.getAmount();
	}

	@NotNull
	public static ItemStack deserialize(@NotNull Map<String, Object> args)
	{
		int version = (args.containsKey("v")) ? ((Number) args.get("v")).intValue() : -1;
		short damage = 0;
		String damageKey = "damage";

		if (args.containsKey(damageKey))
		{
			damage = ((Number) args.get(damageKey)).shortValue();
		}

		Material type = Bukkit.getUnsafe().getMaterial((String) args.get("type"), version);

		ItemStack result = new ItemStackMock(type);

		if (args.containsKey("enchantments"))
		{
			handleLegacyEnchantmentsForDeserialization(args, result);
		}
		else if (args.containsKey("meta"))
		{
			handleMetaForDeserialization(args, version, result);
		}

		if (version < 0 && args.containsKey(damageKey))
		{
			// Set damage again incase meta overwrote it
			result.setDurability(damage);
		}
		return result;
	}

	public InteractionResult simulateUseItemOn(PlayerMock playerMock, Location clickedPos, EquipmentSlot hand)
	{
		throw new UnimplementedOperationException();
	}

	private static void handleMetaForDeserialization(@NotNull Map<String, Object> args, int version, ItemStack result)
	{
		// We cannot and will not have meta when enchantments (pre-ItemMeta) exist
		Object raw = args.get("meta");
		if (raw instanceof ItemMeta)
		{
			((ItemMeta) raw).setVersion(version);
			// Paper start - for pre 1.20.5 itemstacks, add HIDE_STORED_ENCHANTS flag if HIDE_ADDITIONAL_TOOLTIP is set
			if (version < 3837 && ((ItemMeta) raw).hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP))
			{ // 1.20.5
				((ItemMeta) raw).addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
			}
			// Paper end
			result.setItemMeta((ItemMeta) raw);
		}
	}

	private static void handleLegacyEnchantmentsForDeserialization(@NotNull Map<String, Object> args, ItemStack result)
	{
		// Backward compatiblity, @deprecated
		Object raw = args.get("enchantments");

		if (raw instanceof Map<?, ?> map)
		{

			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				String stringKey = entry.getKey().toString();
				stringKey = Bukkit.getUnsafe().get(Enchantment.class, stringKey);
				NamespacedKey key = NamespacedKey.fromString(stringKey.toLowerCase(Locale.ROOT));

				Enchantment enchantment = Bukkit.getUnsafe().get(Registry.ENCHANTMENT, key);

				if ((enchantment != null) && (entry.getValue() instanceof Integer))
				{
					result.addUnsafeEnchantment(enchantment, (Integer) entry.getValue());
				}
			}
		}
	}

}
