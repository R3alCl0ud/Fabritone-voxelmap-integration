package xyz.r3alcl0ud.voxitone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Voxitone implements ClientModInitializer {
    public static VoxitoneConfig config;

    @Override
    public void onInitializeClient() {
        System.out.println("This is a test made by an insane person");
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "voxitone.json");
        Gson gson = new Gson();
        if (configFile.exists()) {
            try {
                config = gson.fromJson(new FileReader(configFile), VoxitoneConfig.class);
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                e.printStackTrace();
                config = new VoxitoneConfig();
                saveConfig();
            }
        } else {
            config = new VoxitoneConfig();
            saveConfig();
        }
    }

    public static void saveConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "voxitone.json");
        Gson gson = new Gson();
        try (FileWriter fw = new FileWriter(configFile)) {
            fw.write(gson.toJson(config));
        } catch (IOException e1) {
            e1.printStackTrace();
            System.err.println("Hooo boi");
        }
    }


}
