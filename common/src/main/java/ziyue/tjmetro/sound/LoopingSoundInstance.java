package ziyue.tjmetro.sound;

import mtr.mappings.RegistryUtilities;
import mtr.mappings.SoundInstanceMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import ziyue.tjmetro.Reference;

/**
 * A looping (repeating) sound instance bound to a block position, used for e.g. the Tianjin smoke alarm.
 * <p>Ported from the MTR4 Tianjin Metro addon ({@code ziyue.tjmetro.mod.sound.LoopingSoundInstance})
 * to the MTR3 API surface (MTR3 uses {@link net.minecraft} classes directly instead of the
 * {@code org.mtr.mapping.holder} wrappers).</p>
 *
 * @author ZiYueCommentary
 * @see mtr.sound.LoopingSoundInstance
 * @since 1.1.x
 */
public class LoopingSoundInstance extends SoundInstanceMapper implements TickableSoundInstance {

	private static final int MAX_DISTANCE = 32;

	public LoopingSoundInstance(String soundId) {
		super(RegistryUtilities.createSoundEvent(new ResourceLocation(Reference.MOD_ID, soundId)), SoundSource.BLOCKS);
		looping = true;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public void tick() {
	}

	/**
	 * Update the sound source position / start or stop the loop based on player distance.
	 *
	 * @param pos       the block position of the emitting block
	 * @param isRemoved whether the block (or its listener) was removed
	 */
	public void setPos(BlockPos pos, boolean isRemoved) {
		if (isRemoved) {
			if (x == pos.getX() && y == pos.getY() && z == pos.getZ()) {
				x = 0;
				y = Integer.MAX_VALUE;
				z = 0;
			}
		} else {
			final LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) {
				return;
			}

			final BlockPos playerPos = player.blockPosition();
			final double distance = playerPos.distManhattan(pos);

			if (distance <= MAX_DISTANCE) {
				final double currentDistance = playerPos.distManhattan(new BlockPos((int) x, (int) y, (int) z));

				if (distance < currentDistance) {
					x = pos.getX();
					y = pos.getY();
					z = pos.getZ();
				}

				final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
				if (soundManager != null && !soundManager.isActive(this)) {
					soundManager.play(this);
				}
			}
		}
	}
}
