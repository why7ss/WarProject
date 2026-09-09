package me.uuun.warProject.paint;

import lombok.RequiredArgsConstructor;
import me.uuun.warProject.manager.GameManager;
import me.uuun.warProject.model.Country;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// todo: remove
// ABSOLUTELY NOT FINISHED, JUST FOR TESTING MAIN MECHANICS
// ABSOLUTELY NOT FINISHED, JUST FOR TESTING MAIN MECHANICS
// ABSOLUTELY NOT FINISHED, JUST FOR TESTING MAIN MECHANICS
// ABSOLUTELY NOT FINISHED, JUST FOR TESTING MAIN MECHANICS
// ABSOLUTELY NOT FINISHED, JUST FOR TESTING MAIN MECHANICS
@RequiredArgsConstructor
public class MapPainterCommand implements CommandExecutor, TabCompleter {
    private final MapPainter mapPainter;
    private final GameManager gameManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Использование: /mappainter <страна> | /mappainter create-city <имя> <фабрики> <население> <защита> <столица:true/false>", NamedTextColor.RED));
            return true;
        }

        if(args[0].equalsIgnoreCase("startgame")){
            gameManager.startGame(false, "ww2");
            return true;
        }

        if (args[0].equalsIgnoreCase("create-city")) {
            handleCreateCity(player, args);
            return true;
        }

        if(args[0].equalsIgnoreCase("addtocountry")){
            handleAddToCountry(player, args[1]);
            return true;
        }

        return handlePaintToggle(player, args[0]);
    }

    private void handleAddToCountry(Player player, String countryName){
        Country country = mapPainter.getCountry(countryName);
        if (country == null) {
            player.sendMessage(Component.text("Страна '" + countryName + "' не найдена.", NamedTextColor.RED));
            return;
        }

        gameManager.addInGame(player, country);
    }

    private boolean handlePaintToggle(Player player, String countryName) {
        Country country = mapPainter.getCountry(countryName);
        if (country == null) {
            player.sendMessage(Component.text("Страна '" + countryName + "' не найдена.", NamedTextColor.RED));
            return true;
        }

        Country active = mapPainter.getActiveCountry(player);
        if (active != null && active.getName().equalsIgnoreCase(countryName)) {
            mapPainter.stop(player);
        } else {
            mapPainter.start(player, country);
        }
        return true;
    }

    private void handleCreateCity(Player player, String[] args) {
        if (args.length != 6) {
            player.sendMessage(Component.text("Использование: /mappainter create-city <имя> <фабрики> <население> <защита> <столица:true/false>", NamedTextColor.RED));
            return;
        }

        if (!mapPainter.isPainting(player)) {
            player.sendMessage(Component.text("Сначала начни рисовать страну: /mappainter <страна>", NamedTextColor.RED));
            return;
        }

        String name = args[1];
        int factories;
        long population;
        int defense;
        boolean isCapital;

        try {
            factories = Integer.parseInt(args[2]);
            population = Long.parseLong(args[3]);
            defense = Integer.parseInt(args[4]);
            isCapital = Boolean.parseBoolean(args[5]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Фабрики/население/защита должны быть числами.", NamedTextColor.RED));
            return;
        }

        boolean created = mapPainter.createCityAtPlayer(player, name, factories, population, defense, isCapital);
        if (created) {
            player.sendMessage(Component.text("Город '" + name + "' создан на твоей позиции.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Не удалось создать город — нет активной сессии рисования.", NamedTextColor.RED));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.add("create-city");
            result.add("startgame");
            result.add("addtocountry");
        } else if (args.length == 6 && args[0].equalsIgnoreCase("create-city")) {
            result.add("true");
            result.add("false");
        }
        return result;
    }
}