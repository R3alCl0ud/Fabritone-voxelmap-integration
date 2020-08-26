package xyz.r3alcl0ud.voxitone;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class VoxitoneMenu implements ModMenuApi {


    private ConfigScreenFactory<?> screenFactory = new VoxitoneConfigScreenFactory();

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screenFactory;
    }

    public static class VoxitoneConfigScreenFactory implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            // TODO Auto-generated method stub
            return new VoxitoneConfigScreen(parent);
        }


    }
}
