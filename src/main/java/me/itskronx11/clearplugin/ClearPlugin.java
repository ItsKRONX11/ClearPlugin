package me.itskronx11.clearplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ClearPlugin extends JavaPlugin implements TabCompleter {
    @Override
    public void onEnable() {
        getCommand("clear").setTabCompleter(this);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) return false;

        Material material;
        try {
            material = Material.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("§cItem not found!"));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cEnter a valid number!");
            return true;
        }

        if (args[0].equals("*")) {
            Bukkit.getOnlinePlayers().forEach(player -> removeItems(player, material, amount));
            sender.sendMessage("§aRemoved " + amount + " " + material.name() + " from " + Bukkit.getOnlinePlayers().size() + " players.");
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage("§cUser not found!");
                return true;
            }

            removeItems(target, material, amount);
            sender.sendMessage("§aRemoved " + amount + " " + material.name() + " from " + target.getName() + ".");
        }
        return true;
    }

    public void removeItems(Player player, Material material, int amount) {
        Inventory playerInventory = player.getInventory();

        for (int slot = 0; slot < playerInventory.getSize(); slot++) {
            ItemStack itemStack = playerInventory.getItem(slot);

            if (itemStack != null && itemStack.getType() == material) {
                int stackSize = itemStack.getAmount();

                if (stackSize <= amount) {
                    playerInventory.clear(slot);
                    amount -= stackSize;
                } else {
                    itemStack.setAmount(stackSize - amount);
                    break;
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.stream(Material.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
