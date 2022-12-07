package be.seeseemelk.mockbukkit.entity;

import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LargeFireballMock extends SizedFireballMock implements LargeFireball
{

	/**
	 * Constructs a new {@link ProjectileMock} on the provided {@link ServerMock} with a specified {@link UUID}.
	 *
	 * @param server The server to create the entity on.
	 * @param uuid   The UUID of the entity.
	 */
	public LargeFireballMock(@NotNull ServerMock server, @NotNull UUID uuid)
	{
		super(server, uuid);
	}

	@Override
	public @NotNull EntityType getType()
	{
		return EntityType.FIREBALL;
	}

}
