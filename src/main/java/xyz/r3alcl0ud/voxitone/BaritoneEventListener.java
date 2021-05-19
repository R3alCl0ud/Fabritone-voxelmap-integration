package xyz.r3alcl0ud.voxitone;

import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import baritone.api.cache.IWaypoint;
import baritone.api.cache.IWaypointCollection;
import baritone.api.event.events.type.EventState;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.utils.BetterBlockPos;
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
    public static double coordScale = 1;
    public static IWaypointManager vmWaypointManager;
    public static IDimensionManager dimensionManager;
    public static IWaypointCollection btWaypointManager;
    public static Waypoint goalWP = null;
    public static IBaritone baritone = null;

    public static IPath oldPath = null;

    public static void loadStuff() {
        if (!stuffLoaded) {
            baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            vmWaypointManager = AbstractVoxelMap.instance.getWaypointManager();
            dimensionManager = AbstractVoxelMap.instance.getDimensionManager();
            btWaypointManager = baritone.getWorldProvider().getCurrentWorld().getWaypoints();
            if (MinecraftClient.getInstance().world != null)
                coordScale = MinecraftClient.getInstance().world.getDimension().getCoordinateScale();
            stuffLoaded = true;
        }
    }

    public static Waypoint genWaypoint() {
        goalWP = new Waypoint("^Baritone Goal", 0, 0, 0, Voxitone.config.shouldWaypointEnable, 0F, 1F, 0F, "star",
            vmWaypointManager.getCurrentSubworldDescriptor(false), new TreeSet<>());
        World world;
        if ((world = mc.world) != null) {
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
        Optional<Waypoint> wp = vmWaypointManager.getWaypoints().stream().filter(e -> e.getX() == x && e.getZ() == z).findFirst();
        if (wp.isPresent()) {
            goalWP = wp.get();
            return false;
        }
        return setPos(x, z, coordScale);
    }

    public static boolean setPos(int x, int y, int z) {
        Optional<Waypoint> wp = vmWaypointManager.getWaypoints().stream().filter(e -> e.getX() == x && e.getY() == y && e.getZ() == z).findFirst();
        if (wp.isPresent()) {
            goalWP = wp.get();
            return false;
        }
        if (setPos(x, z, coordScale)) {
            goalWP.y = y;
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
                vmWaypointManager.deleteWaypoint(goalWP);
                return;
            }

            if (goalWP == null) genWaypoint();

            // update dimension list for goal wp
            if (goalWP.name.equals("^Baritone Goal")) {
                World world;
                if ((world = mc.world) != null) {
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
                    if (!vmWaypointManager.getWaypoints().contains(goalWP))
                        vmWaypointManager.addWaypoint(goalWP);
                    return;
                } else if (g instanceof GoalXZ) {
                    if (setPos(((GoalXZ) g).getX(), ((GoalXZ) g).getZ())) {
                        goalWP.y = 64;
                    }

                    // add waypoint
                    if (!vmWaypointManager.getWaypoints().contains(goalWP))
                        vmWaypointManager.addWaypoint(goalWP);
                    return;
                } else if (g instanceof GoalGetToBlock) {
                    setPos(((GoalGetToBlock) g).x, ((GoalGetToBlock) g).y, ((GoalGetToBlock) g).z);

                    // add waypoint
                    if (!vmWaypointManager.getWaypoints().contains(goalWP))
                        vmWaypointManager.addWaypoint(goalWP);
                    return;
                }
            }

            // remove waypoint since goal isn't handled or is null
            if (goalWP.name.equals("^Baritone Goal")) {
                vmWaypointManager.deleteWaypoint(goalWP);
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

    public boolean isDeathWp(Waypoint wp) {
        return wp.name.equals("Latest Death") || wp.name.matches("Previous Death( \\d+)?");
    }

    @Override
    public void onTick(TickEvent arg0) {
        Map<BetterBlockPos, Waypoint> vmWaypoints = vmWaypointManager.getWaypoints().stream().filter(Waypoint::isActive).collect(Collectors.toMap(e -> new BetterBlockPos(e.x / coordScale, e.y, e.z / coordScale), Function.identity(), (a, b) -> a));
        Map<BetterBlockPos, IWaypoint> btWaypoints = btWaypointManager.getAllWaypoints().stream().collect(Collectors.toMap(IWaypoint::getLocation, Function.identity(), (a,b) -> a));
        for (Map.Entry<BetterBlockPos, Waypoint> vmWp : vmWaypoints.entrySet()) {
            if (!btWaypoints.containsKey(vmWp.getKey())) {
                if (!vmWp.getValue().name.startsWith("^")) {
                    if (Voxitone.config.dontSyncDeathWaypoints && isDeathWp(vmWp.getValue())) continue;

                    btWaypointManager.addWaypoint(new baritone.api.cache.Waypoint(vmWp.getValue().name.replaceAll("\\s", "_") + "_voxelmap", IWaypoint.Tag.USER, vmWp.getKey()));
                } else if (!vmWp.getValue().name.equals("^Baritone Goal")) {
                    vmWaypointManager.deleteWaypoint(vmWp.getValue());
                }
            }
        }
        for (Map.Entry<BetterBlockPos, IWaypoint> btWp : btWaypoints.entrySet()) {
            if (!vmWaypoints.containsKey(btWp.getKey())) {
                if (!btWp.getValue().getName().endsWith("_voxelmap")) {
                    if (Voxitone.config.dontSyncDeathWaypoints && btWp.getValue().getTag() == IWaypoint.Tag.DEATH) continue;

                    BetterBlockPos val = btWp.getKey();

                    Waypoint wp = new Waypoint("^" + btWp.getValue().getName(), (int) (val.x * coordScale), (int) (val.z * coordScale), val.y, true, 0, 1, 0, "", vmWaypointManager.getCurrentSubworldDescriptor(false), new TreeSet<>());
                    World world;
                    if ((world = (World) mc.world) != null) {
                        DimensionContainer dim = dimensionManager.getDimensionContainerByWorld(world);
                        goalWP.dimensions.add(dim);
                    }
                    vmWaypointManager.addWaypoint(wp);
                } else {
                    btWaypointManager.removeWaypoint(btWp.getValue());
                }
            }
        }
    }

    @Override
    public void onWorldEvent(WorldEvent arg0) {
        if (arg0.getState().equals(EventState.POST)) {
            btWaypointManager = baritone.getWorldProvider().getCurrentWorld().getWaypoints();
            coordScale = arg0.getWorld().getDimension().getCoordinateScale();
        }
    }

}
