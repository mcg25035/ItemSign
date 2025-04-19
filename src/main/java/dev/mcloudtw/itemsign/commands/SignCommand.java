package dev.mcloudtw.itemsign.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.mcloudtw.itemsign.Main;
import dev.mcloudtw.itemsign.SignatureUtil;
import org.bukkit.inventory.ItemStack;

public class SignCommand {
    public static CommandAPICommand command() {
        return new CommandAPICommand("signitem")
                .withPermission("itemsign.signs")
                .executesPlayer((player, args) -> {
                    ItemStack signItem = player.getEquipment().getItemInMainHand();
                    if (!Main.testSignable(signItem.getType())) {
                        player.sendMessage("§7[§c物品簽章§7] §c由於設定檔限制，此物品無法被簽章。");
                        return;
                    }
                    if (signItem.isEmpty()) {
                        player.sendMessage("§7[§c物品簽章§7] §c空氣是公有的，無法簽章。");
                        return;
                    }

                    try{
                        SignatureUtil.signItem(player, signItem);
                    }
                    catch (SecurityException e) {
                        player.sendMessage("§7[§c物品簽章§7] §c此物品已經被簽章，請先解除簽章。");
                        return;
                    }


                    player.sendMessage("§7[§c物品簽章§7] §a簽章成功!");
                });
    }
}
