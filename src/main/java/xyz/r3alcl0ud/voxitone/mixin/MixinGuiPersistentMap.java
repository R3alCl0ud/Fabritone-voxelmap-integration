package xyz.r3alcl0ud.voxitone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.client.MinecraftClient;
import xyz.r3alcl0ud.voxitone.BaritoneEventListener;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(value = GuiPersistentMap.class, remap = false)
public class MixinGuiPersistentMap {

    @Shadow(remap = false)
    private IWaypointManager waypointManager;
    
    @Shadow(remap = false)
    float mapCenterZ;

    @Shadow(remap = false)
    float mapCenterX;

    @Shadow(remap = false)
    int centerY;

    @Shadow(remap = false)
    int centerX;

    @Shadow(remap = false)
    private int top;

    @Shadow(remap = false)
    private float mouseDirectToMap;

    @Shadow(remap = false)
    private float guiToDirectMouse;

    @Shadow(remap = false)
    private float guiToMap;

    @Shadow(remap = false)
    private boolean oldNorth;

    @Shadow(remap = false)
    private IPersistentMap persistentMap;

    @Shadow(remap = false)
    private MinecraftClient mc;

    @Shadow(remap = false)
    private Waypoint getHovered(float x, float z) {
        return null;
    }

    @Inject(remap = false, cancellable = true, at = @At("TAIL"), method = "popupAction")
    private void onPopupAction(Popup popup, int action, CallbackInfo info) {
        if (action == 420) {
            synchronized (Voxitone.l) {
                float x, z;
                if (this.oldNorth) {
                    x = (popup.clickedDirectY - this.top * this.guiToDirectMouse) * this.mouseDirectToMap + this.mapCenterZ
                        - this.centerY * this.guiToMap;
                    z = -(popup.clickedDirectX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap);
                } else {
                    x = popup.clickedDirectX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap;
                    z = (popup.clickedDirectY - this.top * this.guiToDirectMouse) * this.mouseDirectToMap + this.mapCenterZ
                        - this.centerY * this.guiToMap;
                }
                int y = this.persistentMap.getHeightAt((int) x, (int) z);
    
                Waypoint hovered = getHovered(x, z);
                IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
                Goal gb;
                if (hovered != null) {
                    if (BaritoneEventListener.goalWP != null && BaritoneEventListener.goalWP.name == "^Baritone Goal")
                        waypointManager.deleteWaypoint(BaritoneEventListener.goalWP);
                    BaritoneEventListener.goalWP = hovered;
                    
                    gb = new GoalBlock(hovered.x, hovered.y, hovered.z);
                } else {
                    if (BaritoneEventListener.goalWP != null && Voxitone.config.tempWaypoints) {
                        if (!(BaritoneEventListener.goalWP.name == "^Baritone Goal")) {
                            BaritoneEventListener.genWaypoint();
                            waypointManager.addWaypoint(BaritoneEventListener.goalWP);
                        }
                        BaritoneEventListener.goalWP.x = (int)x;
                        BaritoneEventListener.goalWP.y = (int)y;
                        BaritoneEventListener.goalWP.z = (int)z;
                    }
                    
                    gb = y == 0 ? new GoalXZ(new BetterBlockPos((int) x, y, (int) z)) : new GoalBlock((int) x, y, (int) z);
                }
                try {
                    baritone.getCustomGoalProcess().setGoalAndPath(gb);
                } catch (Exception e) {}
                if (Voxitone.config.closeOnPath) {
                    mc.openScreen(null);
                }
            }
        }
    }
}
