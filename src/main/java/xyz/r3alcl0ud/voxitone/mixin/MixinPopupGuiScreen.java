package xyz.r3alcl0ud.voxitone.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.gui.overridden.PopupGuiScreen;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;

@Mixin(value = PopupGuiScreen.class,remap = false)
public class MixinPopupGuiScreen {

    @Inject(remap = false,at = @At("HEAD"), method = "createPopup")
    public void onCreatePopup(int x, int y, int dX, int dY, ArrayList<Popup.PopupEntry> entries,CallbackInfo info) {
        if ((Object) this instanceof GuiPersistentMap) {
            entries.add(entries.size() == 4 ? 2 : 3, new Popup.PopupEntry("Path To", 420, true, true));
        }
    }

}
