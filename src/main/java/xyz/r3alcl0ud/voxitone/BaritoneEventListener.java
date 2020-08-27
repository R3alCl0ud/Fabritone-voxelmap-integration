package xyz.r3alcl0ud.voxitone;

import java.util.TreeSet;

import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
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
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class BaritoneEventListener implements IGameEventListener {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static Waypoint goalWP = null;
    public static IBaritone baritone = null;
    
    public static Waypoint genWaypoint() {
        goalWP = new Waypoint("^Baritone Goal", 0, 0, 0, Voxitone.config.shouldWaypointEnable, 0F, 1F, 0F, "", AbstractVoxelMap.instance.getWaypointManager().getCurrentSubworldDescriptor(false), new TreeSet<>());
        World world;
        if ((world = (World)mc.world) != null) {
            DimensionContainer dim = AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld(world);
            goalWP.dimensions.add(dim);
        }
        return goalWP;
    }
    
    @Override
    public void onBlockInteract(BlockInteractEvent arg0) {}

    @Override
    public void onChunkEvent(ChunkEvent arg0) {}

    @Override
    public void onPathEvent(PathEvent arg0) {
        synchronized (this) {
            if (!Voxitone.config.tempWaypoints) {
                if (goalWP != null && goalWP.name == "^Baritone Goal")
                    AbstractVoxelMap.instance.getWaypointManager().deleteWaypoint(goalWP);
                return;
            }
            if (goalWP == null) {
                genWaypoint();
                baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            }
            if (goalWP.name == "^Baritone Goal") {
                World world;
                if ((world = (World)mc.world) != null) {
                    DimensionContainer dim = AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld(world);
                    goalWP.dimensions.add(dim);
                }
                goalWP.enabled = Voxitone.config.shouldWaypointEnable;
            }
            
            Goal g;
            if ((g = baritone.getCustomGoalProcess().getGoal()) != null) {
                if (g instanceof GoalBlock) {
                    goalWP.x = ((GoalBlock)g).x;
                    goalWP.y = ((GoalBlock)g).y;
                    goalWP.z = ((GoalBlock)g).z;
                    if (!AbstractVoxelMap.instance.getWaypointManager().getWaypoints().contains(goalWP))
                        AbstractVoxelMap.instance.getWaypointManager().addWaypoint(goalWP);
                    return;
                } else if (g instanceof GoalXZ) {
                    goalWP.x = ((GoalXZ)g).getX();
                    goalWP.z = ((GoalXZ)g).getZ();
                    if (!AbstractVoxelMap.instance.getWaypointManager().getWaypoints().contains(goalWP))
                        AbstractVoxelMap.instance.getWaypointManager().addWaypoint(goalWP);
                    return;
                }
            }
            if (goalWP.name == "^Baritone Goal") {
                AbstractVoxelMap.instance.getWaypointManager().deleteWaypoint(goalWP);
            } else {
                goalWP = null;
            }
        }
    }

    @Override
    public void onPlayerDeath() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPlayerRotationMove(RotationMoveEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPlayerSprintState(SprintStateEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPlayerUpdate(PlayerUpdateEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPreTabComplete(TabCompleteEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onReceivePacket(PacketEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRenderPass(RenderEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSendChatMessage(ChatEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSendPacket(PacketEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTick(TickEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onWorldEvent(WorldEvent arg0) {
        // TODO Auto-generated method stub
        
    }

}
