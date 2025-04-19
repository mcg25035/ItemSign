package dev.mcloudtw.itemsign.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.mcloudtw.itemsign.SignatureUtil;
import org.bukkit.inventory.ItemStack;

public class SignInfoCommand {
    public static CommandAPICommand command() {
        return new CommandAPICommand("signinfo")
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    ItemStack signItem = player.getEquipment().getItemInMainHand();
                    if (signItem.isEmpty()) {
                        player.sendMessage("§7[§c物品簽章§7] §c此物品沒有被簽章。");
                        return;
                    }

                    player.sendMessage(SignatureUtil.getSignatureInfo(signItem));
                });
    }
}
