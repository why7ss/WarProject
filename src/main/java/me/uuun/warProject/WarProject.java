package me.uuun.warProject;

import lombok.Getter;
import me.uuun.warProject.bug.Bugger;
import me.uuun.warProject.data.MapLoader;
import me.uuun.warProject.manager.GameManager;
import me.uuun.warProject.manager.HotbarManager;
import me.uuun.warProject.manager.ResearchManager;
import me.uuun.warProject.paint.MapPainter;
import me.uuun.warProject.paint.MapPainterCommand;
import org.bukkit.plugin.java.JavaPlugin;
import me.uuun.darkAPI.DarkAPI;

public final class WarProject extends JavaPlugin {
    private static WarProject instance;
    @Getter private GameManager gameManager;
    private final ResearchManager researchManager = new ResearchManager();

    @Override
    public void onEnable() {
        instance = this;
        DarkAPI.registerPlugin(this, "");

        MapLoader mapLoader = new MapLoader(this);

        gameManager = new GameManager(this, mapLoader, researchManager);

        getServer().getPluginManager().registerEvents(new HotbarManager(this), this);
        getServer().getPluginManager().registerEvents(researchManager, this);

        MapPainter mapPainter = new MapPainter(this, gameManager);
        getCommand("mappainter").setExecutor(new MapPainterCommand(mapPainter, gameManager));
    }

    @Override
    public void onDisable() {

    }

    public static WarProject getInstance(){
        if(instance == null){
            Bugger.bug("GOT instance when it is null");
            return null;
        }
        return instance;
    }
}