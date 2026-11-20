package ziyue.tjmetro.render;

import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.world.level.Level;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Client-side door animation clock for Tianjin platform doors (Beta feature).
 * <p>
 * MTR3 animates its platform doors inside {@code TileEntityPSDAPGDoorBase#getOpen(float)}
 * with a step of {@code delta * 0.95}, and {@code delta} is the frame duration in ticks,
 * so a door reaches its target in roughly one tick — it looks like an instant snap.
 * <p>
 * This class keeps its own clock instead: every game tick each known door moves towards
 * its {@code isOpen()} target by {@link #OPEN_STEP_PER_TICK}, and the renderers interpolate
 * between the previous and the current value with the partial tick. The animation speed is
 * therefore independent of the frame rate and of MTR's own open value, which is what the
 * upstream {@code codex/client-door-animation-tick} branch does for MTR 4.
 *
 * @author __Lingxii__
 * @since 1.1.3.2-EXP
 */
public final class DoorAnimationState
{
    /**
     * How much of the door travel is covered per game tick, {@code 1} being fully open.
     * {@code 0.1} means a full open/close takes ten ticks (half a second).
     */
    public static final float OPEN_STEP_PER_TICK = 0.1F;

    private static final Map<BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase, Entry> VALUES = new IdentityHashMap<>();
    private static long lastGameTime = Long.MIN_VALUE;

    private DoorAnimationState()
    {
    }

    /**
     * Returns the animated open value of the door for the current frame.
     *
     * @param door      the door block entity being rendered
     * @param world     the client level the door lives in
     * @param tickDelta partial tick of the current frame, in {@code [0, 1)}
     * @return open value in {@code [0, 1]}
     */
    public static float getInterpolatedOpen(BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase door, Level world, float tickDelta)
    {
        final Entry entry = getEntry(door, world);
        if (entry == null) {
            return door.getOpen(1) > 0 ? 1 : 0;
        }
        return entry.previous + (entry.current - entry.previous) * clamp(tickDelta);
    }

    /**
     * Returns the animated open value of the door without interpolation.
     *
     * @param door  the door block entity being rendered
     * @param world the client level the door lives in
     * @return open value in {@code [0, 1]}
     */
    public static float getOpen(BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase door, Level world)
    {
        final Entry entry = getEntry(door, world);
        return entry == null ? (door.getOpen(1) > 0 ? 1 : 0) : entry.current;
    }

    private static Entry getEntry(BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase door, Level world)
    {
        if (world == null) {
            return null;
        }

        tick(world);

        Entry entry = VALUES.get(door);
        if (entry == null) {
            // Start from the state the door is already in, so that loading a world with
            // open doors does not play a fake animation.
            final float value = door.isOpen() ? 1 : 0;
            entry = new Entry(door, value, value);
            VALUES.put(door, entry);
        }
        return entry;
    }

    private static void tick(Level world)
    {
        final long gameTime = world.getGameTime();
        if (gameTime == lastGameTime) {
            return;
        }
        if (gameTime < lastGameTime) {
            // The level time went backwards, which means another world (or a new session).
            VALUES.clear();
        }
        lastGameTime = gameTime;

        final Iterator<Entry> iterator = VALUES.values().iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.door.isRemoved()) {
                iterator.remove();
                continue;
            }
            entry.previous = entry.current;
            final float target = entry.door.isOpen() ? 1 : 0;
            if (entry.current < target) {
                entry.current = Math.min(target, entry.current + OPEN_STEP_PER_TICK);
            } else if (entry.current > target) {
                entry.current = Math.max(target, entry.current - OPEN_STEP_PER_TICK);
            }
        }

        if (VALUES.size() > 8192) {
            // Safety valve for very long sessions, the entries are rebuilt on demand.
            VALUES.clear();
        }
    }

    private static float clamp(float value)
    {
        return value < 0 ? 0 : Math.min(value, 1);
    }

    private static final class Entry
    {
        private final BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase door;
        private float previous;
        private float current;

        private Entry(BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase door, float previous, float current)
        {
            this.door = door;
            this.previous = previous;
            this.current = current;
        }
    }
}
