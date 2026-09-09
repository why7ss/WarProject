package me.uuun.warProject.manager;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.uuun.warProject.bug.Bugger;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.uuun.darkAPI.DarkAPI;

import java.util.List;

public class SettingsManager implements Listener {
    private final float minSpeed = 1;
    private final float maxSpeed = 5;

    private final NamespacedKey KEY = new NamespacedKey("warproject", "settings");

    public void showDialog(Player player){
        float speed = player.getFlySpeed();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(DarkAPI.parse("Настройки"))
                        .inputs(List.of(
                                DialogInput.numberRange(
                                                "fly_speed",                    // key — по этому ключу потом достаёшь значение из DialogResponseView
                                                DarkAPI.parse("Скорость полёта"), // label
                                                minSpeed,                               // start — минимум диапазона
                                                maxSpeed                              // end — максимум диапазона
                                        )
                                        .step(0.25f)                             // шаг изменения значения (@Positive)
                                        .initial(speed * 10)                          // стартовое значение по умолчанию
                                        .labelFormat("%s: %s") // формат подписи (первый %s — label, второй — текущее значение)
                                        .width(150)                           // ширина поля в интерфейсе (1..1024)
                                        .build()
                        )
                ).build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.builder(DarkAPI.parse("<green>Сохранить"))
                                .tooltip(DarkAPI.parse("Сохранить <green>изменения"))
                                .action(DialogAction.customClick(KEY, null))
                                .build()
                )).columns(1).build()
        ));
        player.showDialog(dialog);
    }

    @EventHandler
    public void onDialogCustomClick(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(KEY)) return;

        if(!(event.getCommonConnection() instanceof PlayerGameConnection conn)){
            Bugger.bug("Common connection is not PlayerGameConnection in exit dialog");
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if(view == null) {
            Bugger.bug("Event dialogResponceView is null");
            return;
        }

        Float speedBoxed = view.getFloat("fly_speed");
        float speed = (speedBoxed != null) ? speedBoxed : 1f;

        Player player = conn.getPlayer();
        player.setFlySpeed(speed / 10);
    }
}