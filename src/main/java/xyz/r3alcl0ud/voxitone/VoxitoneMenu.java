package xyz.r3alcl0ud.voxitone;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class VoxitoneMenu implements ModMenuApi {


    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
                .setTitle(new TranslatableText("title.voxitone.config"));
            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.voxitone.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // toggle close on path
            BooleanToggleBuilder cT = entryBuilder.startBooleanToggle(new TranslatableText("option.voxitone.close"),
                Voxitone.config.closeOnPath);
            cT.setTooltip(new TranslatableText("option.voxitone.close.tooltip"),
                new LiteralText("§7Default: True"));
            cT.setDefaultValue(true);
            cT.setSaveConsumer(newValue -> {
                Voxitone.config.closeOnPath = newValue;
            });
            general.addEntry(cT.build());


            // toggle add temp waypoint
            cT = entryBuilder.startBooleanToggle(new TranslatableText("option.voxitone.tempwaypoint"),
                Voxitone.config.tempWaypoints);
            cT.setTooltip(new TranslatableText("option.voxitone.tempwaypoint.tooltip"),
                new LiteralText("§7Default: True"));
            cT.setDefaultValue(true);
            cT.setSaveConsumer(newValue -> {
                Voxitone.config.tempWaypoints = newValue;
            });
            general.addEntry(cT.build());

            // toggle enable temp waypoint
            cT = entryBuilder.startBooleanToggle(new TranslatableText("option.voxitone.enablewaypoint"),
                Voxitone.config.shouldWaypointEnable);
            cT.setTooltip(new TranslatableText("option.voxitone.enablewaypoint.tooltip"),
                new LiteralText("§7Default: True"));
            cT.setDefaultValue(true);
            cT.setSaveConsumer(newValue -> {
                Voxitone.config.shouldWaypointEnable = newValue;
            });
            general.addEntry(cT.build());

            // toggle draw on minimap
            cT = entryBuilder.startBooleanToggle(new TranslatableText("option.voxitone.drawonminimap"),
                Voxitone.config.drawPathOnMinimap);
            cT.setTooltip(new TranslatableText("option.voxitone.drawonminimap.tooltip"),
                new LiteralText("§7Default: True"));
            cT.setDefaultValue(true);
            cT.setSaveConsumer(newValue -> {
                Voxitone.config.shouldWaypointEnable = newValue;
            });
            general.addEntry(cT.build());
            
            builder.setSavingRunnable(() -> {
                Voxitone.saveConfig();
            });
            return builder.build();
        };
    }


    @Override
    public String getModId() {
        return "voxitone";
    }

    // public static class VoxitoneConfigScreenFactory implements
    // ConfigScreenFactory<Screen> {
    //
    // @Override
    // public Screen create(Screen parent) {
    // // TODO Auto-generated method stub
    // return new VoxitoneConfigScreen(parent);
    // }
    //
    //
    // }

}
