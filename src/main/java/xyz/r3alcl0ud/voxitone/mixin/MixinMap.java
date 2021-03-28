package xyz.r3alcl0ud.voxitone.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mamiyaotaru.voxelmap.Map;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(value = Map.class, remap = false)
public class MixinMap {

    @Unique
    private static IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();

    @Inject(at = @At("TAIL"), method = "renderMap", remap = false)
    public void renderMap(int x, int y, int scale, CallbackInfo info) {
        // System.out.printf("X: %d, Y: %d, Scale: %d", x, y, scale);
    }

    @Inject(at = @At("TAIL"), method = "renderMapFull", remap = false)
    public void renderMap(MatrixStack stack, int width, int height, CallbackInfo info) {
        // System.out.printf("X: %d, Y: %d", width, height);

    }

    @Inject(at = @At("RETURN"), method = "getPixelColor", remap = false, cancellable = true)
    public void getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean caves, ClientWorld world, int multi, int startX, int startZ, int imageX, int imageY, CallbackInfoReturnable<Integer> cir) {
        if (Voxitone.config.drawPathOnMinimap && baritone.getPathingBehavior().isPathing()) {
            int x = startX + imageX, z = startZ + imageY;
            for (BetterBlockPos pos : baritone.getPathingBehavior().getCurrent().getPath().positions()) {
                if (pos.x == x && pos.z == z) {
                    cir.setReturnValue(0xFF000000 | BaritoneAPI.getSettings().colorCurrentPath.value.getRGB());
                    cir.cancel();
                    return;
                }
            }
        }
    }
}
