package dev.mcloudtw.itemsign;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SignatureUtil {

    private static final String NAMESPACE = "itemsign";
    private static final NamespacedKey SIGNED_KEY = new NamespacedKey(NAMESPACE, "signed");
    private static final NamespacedKey SIGNER_KEY = new NamespacedKey(NAMESPACE, "signer");
    private static final NamespacedKey SIGN_TIME_KEY = new NamespacedKey(NAMESPACE, "sign_time");

    public static void signItem(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (isSigned(item)) {
            throw new SecurityException("Item is signed.");
        }

        pdc.set(SIGNED_KEY, PersistentDataType.BOOLEAN, true);
        pdc.set(SIGNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
        pdc.set(SIGN_TIME_KEY, PersistentDataType.LONG, System.currentTimeMillis());

        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Component.text("§a§l[物品簽章]: §6§l" + player.getName()));
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    public static void unsignItem(Player player, ItemStack item) throws Exception {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (!isSigned(item)) {
            throw new IllegalArgumentException("Item is not signed.");
        }

        if (!getSigner(item).equals(player.getUniqueId())) {
            throw new SecurityException("Cannot unsign item signed by another player.");
        }

        pdc.remove(SIGNED_KEY);
        pdc.remove(SIGNER_KEY);
        pdc.remove(SIGN_TIME_KEY);

        List<Component> lore = meta.lore();
        if (lore != null) {
            lore.removeIf(component -> component.toString().contains("物品簽章"));
            meta.lore(lore);
        }

        item.setItemMeta(meta);
    }

    public static String getSignatureInfo(ItemStack item) {
        if (!isSigned(item)) {
            return "Item is not signed.";
        }

        UUID signer = getSigner(item);
        long signTime = getSignTime(item);

        return "§7[§c物品簽章§7] §a簽章者 §f: §e" + Bukkit.getPlayer(signer).getName() + "\n" +
                "§7[§c物品簽章§7] §a簽章時間 §f: §e" +
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(signTime));
    }

    public static boolean isSigned(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(SIGNED_KEY, PersistentDataType.BOOLEAN) && pdc.get(SIGNED_KEY, PersistentDataType.BOOLEAN);
    }

    public static UUID getSigner(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String signerString = pdc.get(SIGNER_KEY, PersistentDataType.STRING);
        return UUID.fromString(signerString);
    }

    public static long getSignTime(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(SIGN_TIME_KEY, PersistentDataType.LONG);
    }
}
