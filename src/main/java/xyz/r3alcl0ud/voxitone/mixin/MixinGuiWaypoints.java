package xyz.r3alcl0ud.voxitone.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.r3alcl0ud.voxitone.BaritoneEventListener;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(value = GuiWaypoints.class, remap = false)
public abstract class MixinGuiWaypoints extends Screen {
    
    @Unique
    private ButtonWidget pathTo;
    
    @Unique
    private static IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
    
    @Shadow(remap = false)
    @Final
    protected IWaypointManager waypointManager;
    
    @Shadow(remap = false)
    protected Waypoint selectedWaypoint;
    
    @Inject(at = @At("TAIL"), method = "method_25426", remap = false)
    public void init(CallbackInfo info) {
        MinecraftClient mc = MinecraftClient.getInstance();
        pathTo = addButton(new ButtonWidget(width / 2 + 158, height - 28, 74, 20, new LiteralText("Path To"), (b) -> {
            if (BaritoneEventListener.goalWP != null && BaritoneEventListener.goalWP.name == "^Baritone Goal")
                waypointManager.deleteWaypoint(BaritoneEventListener.goalWP);
            BaritoneEventListener.goalWP = selectedWaypoint;
            baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(selectedWaypoint.x, selectedWaypoint.y, selectedWaypoint.z));
            if (Voxitone.config.closeOnPath) mc.openScreen(null);
            
        }));
        pathTo.active = selectedWaypoint != null;
    }
    
    @Inject(at = @At("TAIL"), method = "setSelectedWaypoint", remap = false)
    protected void setSelectedWaypoint(Waypoint waypoint, CallbackInfo info) {
        pathTo.active = waypoint != null;
    }

    
    
    //ignore
    protected MixinGuiWaypoints(Text title) {
        super(title);
    }
}
