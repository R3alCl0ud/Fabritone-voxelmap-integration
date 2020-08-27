package xyz.r3alcl0ud.voxitone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.persistent.PersistentMap;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.calc.IPath;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.client.world.ClientWorld;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(value = PersistentMap.class, remap = false)
public class MixinPersistentMap {

    @Unique
    private static IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();

    @Inject(at = @At("TAIL"), method = "getPixelColor", remap = false, cancellable = true)
    public void getPixelColor(AbstractMapData mapData, ClientWorld world, MutableBlockPos blockPos,
        MutableBlockPos loopBlockPos, boolean underground, int multi, int startX, int startZ, int imageX, int imageY,
        CallbackInfoReturnable<Integer> info) {


        if (Voxitone.config.drawPathOnMap && baritone.getPathingBehavior().isPathing()) {
            // System.out.println("Jesus is taking the wheel");
            int x = startX + imageX, z = startZ + imageY;
            IPath path = baritone.getPathingBehavior().getCurrent().getPath();
            for (BetterBlockPos pos : path.positions()) {
                if (pos.x == x && pos.z == z) {
                    info.setReturnValue(0xFF000000 | BaritoneAPI.getSettings().colorCurrentPath.value.getRGB());
                    info.cancel();
                    return;
                }
            }
        }
    }

}
