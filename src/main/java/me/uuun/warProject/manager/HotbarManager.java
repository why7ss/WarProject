package me.uuun.warProject.manager;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.uuun.warProject.WarProject;
import me.uuun.warProject.bug.Bugger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.uuun.darkAPI.DarkAPI;

import java.util.List;

public class HotbarManager implements Listener {
    private static final Key EXIT_COUNTRY_KEY = Key.key("warproject", "exit_country");

    private final WarProject plugin;

    private final SettingsManager settingsManager = new SettingsManager();
    private final DiplomacyManager diplomacyManager;

    public HotbarManager(WarProject plugin){
        this.plugin = plugin;
        this.diplomacyManager = new DiplomacyManager(plugin.getGameManager());
        Bukkit.getPluginManager().registerEvents(settingsManager, plugin);
        Bukkit.getPluginManager().registerEvents(diplomacyManager, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(plugin.getGameManager().getPlayerCountry(player) == null) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        if(player.getInventory().getItemInMainHand().getType().isAir()) return;

        if(event.getAction().isRightClick()){
            int slot = player.getInventory().getHeldItemSlot();
            switch (slot){
                case 0 -> diplomacyManager.showHubDialog(player, null);
                case 1 -> plugin.getGameManager().getResearchManager().openResearchGui(player);
                case 6 -> settingsManager.showDialog(player);
                case 8 -> showExitDialog(player);
            }
        }
        event.setCancelled(true);
    }

    private void showExitDialog(Player player){
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.empty()).body(List.of(
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(Component.empty()),
                        DialogBody.plainMessage(DarkAPI.parse("ʙы <yellow><u>уʙᴇᴘᴇʜы</u><white>, чтᴏ xᴏтитᴇ\n<#FF0000><b>пᴏᴋиʜуть</b><white> ᴄтᴘᴀʜу?"))
                    )).build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.builder(DarkAPI.parse("<#FF0000>✔ <white>Выйти"))
                                .tooltip(DarkAPI.parse("Нажмите, чтобы <#FF0000>выйти"))
                                .action(DialogAction.customClick(EXIT_COUNTRY_KEY, null))
                                .width(125)
                                .build(),
                        ActionButton.builder(Component.empty()).width(1).build(),
                        ActionButton.builder(DarkAPI.parse("<green>✘ <white>Отменить"))
                                .tooltip(DarkAPI.parse("Нажмите, чтобы <green>отменить"))
                                .width(125)
                                .build()
                )).columns(3).build())
        );
        player.showDialog(dialog);
    }

    @EventHandler
    public void onDialogCustomClick(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(EXIT_COUNTRY_KEY)) return;

        if (event.getCommonConnection() instanceof io.papermc.paper.connection.PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            plugin.getGameManager().kickPlayerFromCountry(player);
        } else {
            Bugger.bug("Common connection is not PlayerGameConnection in exit dialog");
        }
    }

    public static void giveItemsToPlayer(Player player){
        Inventory inv = player.getInventory();

        inv.clear();

        inv.setItem(0, createItem("<yellow>Дипломатия", Material.PAPER, "nexo:diplomacy"));
        inv.setItem(1, createItem("<blue>Улучшения", Material.PAPER, "nexo:research"));
        inv.setItem(2, createItem("<gray>Фокусы", Material.PAPER, "nexo:government"));
        inv.setItem(3, createItem("<gold>Продукция", Material.PAPER, "nexo:production"));
        inv.setItem(4, createItem("<green>Армия", Material.PAPER, "nexo:army"));
        inv.setItem(5, createItem("<yellow>Магазин", Material.PAPER, "nexo:shop"));
        inv.setItem(6, createItem("<gray>Настройки", Material.PAPER, "nexo:settings"));
        inv.setItem(7, createItem("<gray>Туториал", Material.PAPER, "nexo:tutorial"));
        inv.setItem(8, createItem("<dark_green>Покинуть страну", Material.PAPER, "nexo:change_country"));
    }

    private static ItemStack createItem(String displayName, Material material, String modelName){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.displayName(DarkAPI.parse(displayName));
            meta.setItemModel(NamespacedKey.fromString(modelName));
            item.setItemMeta(meta);
        }
        return item;
    }
}