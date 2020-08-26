package xyz.r3alcl0ud.voxitone.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import net.minecraft.client.MinecraftClient;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(value = GuiPersistentMap.class, remap = false)
public class MixinGuiPersistentMap {

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
    private Waypoint getHovered(float x, float z) {
        return null;
    }

    @Inject(remap = false, cancellable = true, at = @At("TAIL"), method = "popupAction")
    private void onPopupAction(Popup p, int action, CallbackInfo info) {
        if (action == 420) {
            System.out.println("Help me");
            int mouseX = p.clickedDirectX, mouseY = (int) Math.floor(p.clickedDirectY - top * guiToDirectMouse);
            float actualX, actualZ;
            if (this.oldNorth) {
                actualX = mouseY * mouseDirectToMap + mapCenterZ - centerY * guiToMap;
                actualZ = -(mouseX * mouseDirectToMap + mapCenterZ - centerX * guiToMap);
            } else {
                actualX = mouseX * mouseDirectToMap + mapCenterX - centerX * guiToMap;
                actualZ = mouseY * mouseDirectToMap + mapCenterZ - centerY * guiToMap;
            }

            Waypoint hovered = getHovered(actualX, actualZ);
            IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            if (hovered != null) {
                System.out.println("Holy fuck bros! We've got a waypoint");
                System.out.printf("X: %d, Y:%d, Z:%d\n", hovered.x, hovered.y, hovered.z);
                GoalBlock gb = new GoalBlock(hovered.x, hovered.y, hovered.z);
                baritone.getCustomGoalProcess().setGoalAndPath(gb);
            } else {
                System.out.println("Aww, no waypoint :(");
                try {
                    System.out.printf("X: %f, Z:%f\n", actualX, actualZ);
                    GoalXZ gb = new GoalXZ((int) Math.floor(actualX), (int) Math.floor(actualZ));
                    baritone.getCustomGoalProcess().setGoalAndPath(gb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (Voxitone.config.closeOnPath) {
                MinecraftClient.getInstance().openScreen(null);
            }
            // WaypointManager wm = (WaypointManager)
            // AbstractVoxelMap.instance.getWaypointManager();
            info.cancel();
        }
    }


}
