package me.uuun.warProject.bug;

import me.uuun.warProject.WarProject;
import me.uuun.warProject.discord.WebhookManager;
import me.uuun.darkAPI.DarkAPI;

public class Bugger {
    private static final WarProject instance = WarProject.getInstance();

    private static void text(String text){
        if(instance != null) instance.getComponentLogger().info(DarkAPI.parse(text));
        WebhookManager.send(text);
    }

    public static void bug(String text){
        text("<#FF0000>Error: <b>" + text + "</b>");
    }

    public static void warn(String text){
        text("<#FFFF00>Warning: <b>" + text + "</b>");
    }

    public static void info(String text){
        text("<#00FF00>Info: <b>" + text + "</b>");
    }
}