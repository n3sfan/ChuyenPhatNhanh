package me.lethinh.chuyenphatnhanh;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemSerializer {

    /**
     * Converts an ItemStack array to a Base64 string.
     *
     * @param items The items to be serialized.
     * @return A Base64 string representing the items.
     * @throws IllegalStateException If an IO error occurs.
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Converts a single ItemStack to a Base64 string.
     *
     * @param item The item to be serialized.
     * @return A Base64 string representing the item.
     * @throws IllegalStateException If an IO error occurs.
     */
    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        return itemStackArrayToBase64(new ItemStack[]{item});
    }

    /**
     * Decodes a Base64 string into an ItemStack array.
     *
     * @param data The Base64 string to decode.
     * @return An array of ItemStacks.
     * @throws IOException If an IO error occurs during decoding.
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Decodes a Base64 string into a single ItemStack.
     *
     * @param data The Base64 string to decode.
     * @return A single ItemStack.
     * @throws IOException If an IO error occurs during decoding.
     */
    public static ItemStack itemStackFromBase64(String data) throws IOException {
        ItemStack[] items = itemStackArrayFromBase64(data);
        return (items != null && items.length > 0) ? items[0] : null;
    }
}
