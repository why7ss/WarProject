package me.uuun.warProject.manager;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.RequiredArgsConstructor;
import me.uuun.warProject.WarProject;
import me.uuun.warProject.bug.Bugger;
import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Game;
import me.uuun.warProject.model.Justification;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import me.uuun.darkAPI.DarkAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiplomacyManager implements Listener {
    private final GameManager gameManager;

    public void showHubDialog(Player player, @Nullable String search){
        List<ActionButton> buttons = new ArrayList<>(List.of(
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(DarkAPI.parse("<yellow>✎ <white>Очистить <yellow>ввод"))
                        .tooltip(DarkAPI.parse("<white>Нажмите, для <yellow>очистки"))
                        .width(128)
                        .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search_clear"), null))
                        .build(),
                ActionButton.builder(DarkAPI.parse("<green>\uD83C\uDFA3 <white>Поиск <green>страны"))
                        .tooltip(DarkAPI.parse("<white>Нажмите, для <green>поиска"))
                        .width(128)
                        .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null))
                        .build(),
                ActionButton.builder(DarkAPI.parse("<#FF0000>✘ <white>Закрыть <#FF0000>меню"))
                        .tooltip(DarkAPI.parse("<white>Нажмите, чтобы <#FF0000>закрыть"))
                        .width(128)
                        .build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build(),
                ActionButton.builder(Component.empty()).width(1).action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null)).build()
        ));

        buttons.addAll(collectButtonsWithSearch(search));

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(DarkAPI.parse("Дипломатия"))
                        .body(List.of(DialogBody.plainMessage(DarkAPI.parse("ʙʙᴇдитᴇ <green>ʜᴀзʙᴀʜиᴇ<white> ᴄтᴘᴀʜы\nдля <yellow>пᴏиᴄᴋᴀ"))))
                        .inputs(List.of(
                                        DialogInput.text(
                                                        "search",
                                                        DarkAPI.parse("Введите поиск")
                                                )
                                                .labelVisible(false)
                                                .maxLength(50)
                                                .initial(search != null ? search : "")
                                                .width(150)
                                                .build()
                        ))
                        .build())
                .type(DialogType.multiAction(buttons).columns(7).build()
                ));
        player.showDialog(dialog);
    }

    public void showCountryDialog(Player player, Country country){
        Country playerCountry = gameManager.getPlayerCountry(player);

        List<ActionButton> buttons = new ArrayList<>(List.of(
                ActionButton.builder(DarkAPI.parse("<yellow>\uD83D\uDEE1 <white>Предложить <yellow>альянс"))
                        .tooltip(DarkAPI.parse("<white>Нажмите, для <yellow>отправки"))
                        .width(150)
                        .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_alliance_" + country.getName()), null))
                        .build()
        ));

        boolean found = false;

        if(playerCountry != null && !playerCountry.getJustifications().isEmpty()){
            for(Justification justification : playerCountry.getJustifications()) {
                if (justification.getTarget().equals(country)) {
                    found = true;

                    if (justification.getProgress() >= 100) {
                        buttons.add(ActionButton.builder(DarkAPI.parse("<#FF0000>\uD83D\uDDE1 <white>Объявить <#FF0000>войну"))
                                .tooltip(DarkAPI.parse("<white>Нажмите, для <#FF0000>объявления"))
                                .width(150)
                                .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_justification_" + country.getName()), null))
                                .build());
                    } else {
                        buttons.add(ActionButton.builder(DarkAPI.parse("<#FF0000>\uD83C\uDFF9 <white>Оправдание: <#FF0000>" + justification.getProgress() + "%"))
                                .tooltip(Component.empty())
                                .width(150)
                                .action(DialogAction.customClick(
                                        (response, audience) -> showCountryDialog(player, country),
                                        ClickCallback.Options.builder().build()
                                ))
                                .build());
                    }
                }
            };
        }

        if(!found){
            buttons.add(ActionButton.builder(DarkAPI.parse("<#FF0000>\uD83C\uDFF9 <white>Оправдать <#FF0000>войну<white> (<gray>25<white>ꐕ)"))
                    .tooltip(Component.empty())
                    .width(150)
                    .action(DialogAction.customClick(
                            (response, audience) -> justify(playerCountry, country),
                            ClickCallback.Options.builder().build()
                    ))
                    .build());
        }

        buttons.add(ActionButton.builder(DarkAPI.parse("<#FF0000>✘ <white>Вернутся"))
                    .tooltip(DarkAPI.parse("<white>Нажмите, чтобы <#FF0000>вернутся"))
                    .width(150)
                    .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_search"), null))
                .build());

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(DarkAPI.parse("Дипломатия"))
                        .body(List.of(
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(Component.empty()),
                                DialogBody.plainMessage(DarkAPI.parse("<b><" + country.getColor().toString().toLowerCase() + ">" + country.getDisplayName() + "</b>")),
                                DialogBody.plainMessage(DarkAPI.parse("Игрок: <gold>" + (country.getPlayer() != null ? country.getPlayer().getName() : "<#FF0000>нет\n" +
                                        "<blue>Демократия")))
                        ))
                        .build())
                .type(DialogType.multiAction(buttons).columns(1).build()
                ));
        player.showDialog(dialog);
    }

    private void justify(Country initiator, Country target){
        if(initiator == null) return;

        boolean found = false;
        for(Justification justification : initiator.getJustifications()){
            if(justification.getTarget().equals(target)){
                found = true;
                break;
            }
        }

        if(!found) {
            if(initiator.getPoliticalPower() >= 25) {
                initiator.setPoliticalPower(initiator.getPoliticalPower() - 25);
                initiator.getJustifications().add(new Justification(initiator, target));
            }
        }

        showCountryDialog(initiator.getPlayer(), target);
    }

    private List<ActionButton> collectButtonsWithSearch(@Nullable String search){
        List<ActionButton> buttons = new ArrayList<>();

        Game game = gameManager.getGame();
        if(game == null) return List.of();

        Set<Country> countries = game.getCountries();

        if(search != null && !search.isEmpty()){
            countries = countries.stream().filter(country -> country.getDisplayName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toSet());
        }

        countries.forEach(country -> {
            String color = country.getColor().toString().toLowerCase();

            ActionButton button = ActionButton.builder(DarkAPI.parse("<" + color + ">" + (country.getPlayer() == null ? "⚐" : "⚑") + " <white>" + country.getDisplayName()))
                    .tooltip(DarkAPI.parse("Нажмите, чтобы <" + color + ">открыть"))
                    .action(DialogAction.customClick(new NamespacedKey("warproject", "diplomacy_country_" + country.getName()), null))
                    .width(128)
                    .build();
            buttons.add(button);
        });

        return buttons;
    }

    @EventHandler
    public void onDialogCustomClick(PlayerCustomClickEvent event) {
        if(!(event.getCommonConnection() instanceof PlayerGameConnection conn)) return;
        DialogResponseView view = event.getDialogResponseView();
        if(view == null) return;

        Player player = conn.getPlayer();

        Plugin plugin = WarProject.getInstance();
        if(plugin == null) return;

        Key identifier = event.getIdentifier();

        String search = view.getText("search");
        if (identifier.equals(new NamespacedKey("warproject", "diplomacy_search"))) {
            showHubDialog(player, search);
        } else if (identifier.equals(new NamespacedKey("warproject", "diplomacy_search_clear"))) {
            showHubDialog(player, null);
        } else if (identifier.asString().startsWith("warproject:diplomacy_country_")) {
            String countryName = identifier.asString().substring("warproject:diplomacy_country_".length());
            Country country = gameManager.getCountryByName(countryName);
            if (country != null) {
                showCountryDialog(player, country);
            } else {
                showHubDialog(player, search);
                Bugger.warn("Country '" + countryName + "' not available for diplomacy view");
            }
        }
    }
}