package xyz.r3alcl0ud.voxitone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import baritone.api.BaritoneAPI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import xyz.r3alcl0ud.voxitone.BaritoneEventListener;
import xyz.r3alcl0ud.voxitone.Voxitone;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    
    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void jsmacros_onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        if (Voxitone.listener == null) {
            Voxitone.listener = new BaritoneEventListener();
        }
        BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().registerEventListener(Voxitone.listener);
    }
}
