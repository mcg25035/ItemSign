package dev.mcloudtw.itemsign.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.mcloudtw.itemsign.SignatureUtil;
import org.bukkit.inventory.ItemStack;

public class UnsignCommand {
    public static CommandAPICommand command() {
        return new CommandAPICommand("unsignitem")
                .withPermission("itemsign.unsign")
                .executesPlayer((player, args) -> {
                    ItemStack signItem = player.getEquipment().getItemInMainHand();
                    if (signItem.isEmpty()) {
                        player.sendMessage("§7[§c物品簽章§7] §c此物品沒有被簽章。");
                        return;
                    }

                    try {
                        SignatureUtil.unsignItem(player, signItem);
                        player.sendMessage("§7[§c物品簽章§7] §a解除簽章成功!");
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§7[§c物品簽章§7] §c此物品沒有被簽章。");
                    } catch (SecurityException e) {
                        player.sendMessage("§7[§c物品簽章§7] §c無法解除別人的簽章。");
                    } catch (Exception e) {
                        player.sendMessage("§7[§c物品簽章§7] §c發生錯誤: " + e.getMessage());
                    }
                });
    }
}
