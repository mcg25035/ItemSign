package dev.mcloudtw.itemsign;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Events implements Listener {
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        if (!Main.testSignable(event.getItemInHand().getType())) return;
        ItemStack placingItem = event.getItemInHand();
        if (!placingItem.hasItemMeta()) return;
        if (!placingItem.getType().isBlock()) {
            throw new IllegalArgumentException("[ItemSign] The raw material of specified item is not a block.");
        }

        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();
        String key = x + "-" + y + "-" + z;
        String value = NBT.itemStackToNBT(placingItem).getCompound("components").toString();

        NamespacedKey nbtKey = new NamespacedKey("itemsign", key);
        event.getBlock().getChunk().getPersistentDataContainer().set(nbtKey, PersistentDataType.STRING, value);
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {

        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();
        String key = x + "-" + y + "-" + z;

        NamespacedKey nbtKey = new NamespacedKey("itemsign", key);
        String value = event.getBlock().getChunk().getPersistentDataContainer().get(nbtKey, PersistentDataType.STRING);

        if (value == null) return;

        event.setDropItems(false);

        Material type = event.getBlock().getType();
        if (!type.isItem()) {
            if (event.getBlock().getDrops().isEmpty()) throw new IllegalArgumentException("[ItemSign] The block is not droppable.");
            type = event.getBlock().getDrops().stream().findFirst().get().getType();
        }
        ItemStack rawDrop = new ItemStack(type);
        ReadWriteNBT rawDropNbtToEdit = NBT.itemStackToNBT(rawDrop);
        ReadWriteNBT storedDropNbt = NBT.parseNBT(value);
        storedDropNbt.removeKey("minecraft:container");
        rawDropNbtToEdit.mergeCompound(NBT.parseNBT("{components: " + storedDropNbt + "}"));
        ItemStack newDrop = NBT.itemStackFromNBT(rawDropNbtToEdit);

        assert newDrop != null;
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), newDrop);
        event.getBlock().getChunk().getPersistentDataContainer().remove(nbtKey);

    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        List<InventoryType> nonChangingInventoryTypes = List.of(
                InventoryType.CHEST,
                InventoryType.DISPENSER,
                InventoryType.DROPPER,
                InventoryType.PLAYER,
                InventoryType.CREATIVE,
                InventoryType.MERCHANT,
                InventoryType.ENDER_CHEST,
                InventoryType.BEACON,
                InventoryType.HOPPER,
                InventoryType.SHULKER_BOX,
                InventoryType.BARREL,
                InventoryType.LECTERN,
                InventoryType.COMPOSTER,
                InventoryType.CHISELED_BOOKSHELF,
                InventoryType.JUKEBOX,
                InventoryType.DECORATED_POT
        );

        if (nonChangingInventoryTypes.contains(event.getClickedInventory().getType())) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        PersistentDataContainer pdc = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
        if (!pdc.has(new NamespacedKey("itemsign", "signed"), PersistentDataType.BOOLEAN)) return;
        if (!pdc.get(new NamespacedKey("itemsign", "signed"), PersistentDataType.BOOLEAN)) return;
        if (!pdc.has(new NamespacedKey("itemsign", "signer"), PersistentDataType.STRING)) return;
        if (
                pdc.get(new NamespacedKey("itemsign", "signer"), PersistentDataType.STRING)
                        .equals(event.getWhoClicked().getUniqueId().toString())
        ) return;

        event.setCancelled(true);



    }



}
