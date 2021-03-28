package xyz.r3alcl0ud.voxitone;

import java.util.TreeSet;

import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IDimensionManager;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.BlockInteractEvent;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.PathEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.events.RotationMoveEvent;
import baritone.api.event.events.SprintStateEvent;
import baritone.api.event.events.TabCompleteEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.WorldEvent;
import baritone.api.event.listener.IGameEventListener;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class BaritoneEventListener implements IGameEventListener {
    private static boolean stuffLoaded = false;
    protected static MinecraftClient mc = MinecraftClient.getInstance();
    public static IWaypointManager waypointManager;
    public static IDimensionManager dimensionManager;
    public static Waypoint goalWP = null;
    public static IBaritone baritone = null;

    public static IPath oldPath = null;

    public static void loadStuff() {
        baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        if (!stuffLoaded) {
            waypointManager = AbstractVoxelMap.instance.getWaypointManager();
            dimensionManager = AbstractVoxelMap.instance.getDimensionManager();
            stuffLoaded = true;
        }
    }

    public static Waypoint genWaypoint() {
        goalWP = new Waypoint("^Baritone Goal", 0, 0, 0, Voxitone.config.shouldWaypointEnable, 0F, 1F, 0F, "star",
            waypointManager.getCurrentSubworldDescriptor(false), new TreeSet<>());
        World world;
        if ((world = (World) mc.world) != null) {
            DimensionContainer dim = dimensionManager.getDimensionContainerByWorld(world);
            goalWP.dimensions.add(dim);
        }
        return goalWP;
    }

    protected static boolean setPos(int x, int z, double scale) {
        if (goalWP == null)
            genWaypoint();
        // cancel if not actually the baritone goal wp
        if (!goalWP.name.equals("^Baritone Goal")) return false;
        goalWP.x = (int) (x * scale);
        goalWP.z = (int) (z * scale);
        return true;
    }

    public static boolean setPos(int x, int z) {
        World world;
        if ((world = (World) mc.world) != null) {
            return setPos(x, z, world.getDimension().getCoordinateScale());
        } else {
            return setPos(x, z, 1D);
        }
    }

    public static boolean setPos(int x, int y, int z) {
        if (setPos(x, z)) {
            goalWP.y = (int) y;
            return true;
        }
        return false;
    }

    @Override
    public void onPathEvent(PathEvent arg0) {

        synchronized (this) {
            loadStuff();

            // arg0.
            if (baritone.getPathingBehavior().getCurrent() != null) {
                IPath path = baritone.getPathingBehavior().getCurrent().getPath();
                if (oldPath == null || !oldPath.equals(path)) {
                    oldPath = path;
                    AbstractVoxelMap.instance.getMap().forceFullRender(true);
                }
            }


            // remove temp waypoint if config disables them, shortcut rest of function
            if (!Voxitone.config.tempWaypoints && goalWP != null && goalWP.name.equals("^Baritone Goal")) {
                waypointManager.deleteWaypoint(goalWP);
                return;
            }

            if (goalWP == null) genWaypoint();

            // update dimension list for goal wp
            if (goalWP.name.equals("^Baritone Goal")) {
                World world;
                if ((world = (World) mc.world) != null) {
                    DimensionContainer dim = AbstractVoxelMap.getInstance().getDimensionManager()
                        .getDimensionContainerByWorld(world);
                    goalWP.dimensions.add(dim);
                }
                goalWP.enabled = Voxitone.config.shouldWaypointEnable;
            }

            // update goal location
            Goal g;
            if ((g = baritone.getCustomGoalProcess().getGoal()) != null) {
                if (g instanceof GoalBlock) {
                    setPos(((GoalBlock) g).x, ((GoalBlock) g).y, ((GoalBlock) g).z);

                    // add waypoint
                    if (!waypointManager.getWaypoints().contains(goalWP))
                        waypointManager.addWaypoint(goalWP);
                    return;
                } else if (g instanceof GoalXZ) {
                    setPos(((GoalXZ) g).getX(), ((GoalXZ) g).getZ());

                    // add waypoint
                    if (!waypointManager.getWaypoints().contains(goalWP))
                        waypointManager.addWaypoint(goalWP);
                    return;
                }
            }

            // remove waypoint since goal isn't handled or is null
            if (goalWP.name.equals("^Baritone Goal")) {
                waypointManager.deleteWaypoint(goalWP);
            } else {
                goalWP = null;
            }
        }
    }


    // We need these to complete the interface

    @Override
    public void onBlockInteract(BlockInteractEvent arg0) {}

    @Override
    public void onChunkEvent(ChunkEvent arg0) {}

    @Override
    public void onPlayerDeath() {}

    @Override
    public void onPlayerRotationMove(RotationMoveEvent arg0) {}

    @Override
    public void onPlayerSprintState(SprintStateEvent arg0) {}

    @Override
    public void onPlayerUpdate(PlayerUpdateEvent arg0) {}

    @Override
    public void onPreTabComplete(TabCompleteEvent arg0) {}

    @Override
    public void onReceivePacket(PacketEvent arg0) {}

    @Override
    public void onRenderPass(RenderEvent arg0) {}

    @Override
    public void onSendChatMessage(ChatEvent arg0) {}

    @Override
    public void onSendPacket(PacketEvent arg0) {}

    @Override
    public void onTick(TickEvent arg0) {}

    @Override
    public void onWorldEvent(WorldEvent arg0) {}

}
