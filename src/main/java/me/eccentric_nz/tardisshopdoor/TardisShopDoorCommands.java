/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorCommands implements CommandExecutor {

    TardisShopDoor plugin;

    public TardisShopDoorCommands(TardisShopDoor plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tsd")) {
            Player p = null;
            if (sender instanceof Player) {
                p = (Player) sender;
            }
            if (p == null) {
                sender.sendMessage("[TARDIS Shop Door] Command can only be run by a player");
                return true;
            }
            if (!p.hasPermission("tardis.shopdoor") || p.isOp()) {
                if (args.length == 1) {
                    // check the name
                    TardisShopDoorData data = new TardisShopDoorResultSet().resultSet(args[0], 0);
                    if (data == null) {
                        // start tracking this name
                        TardisShopDoorAdd a = new TardisShopDoorAdd();
                        a.setName(args[0]);
                        a.setType(0);
                        plugin.trackAdd.put(p.getUniqueId(), a);
                        p.sendMessage("[TARDIS Door Shop] Click on the first door.");
                    } else {
                        p.sendMessage("[TARDIS Door Shop] A door set already exists with that name.");
                    }
                    return true;
                }
                if (args.length == 2 && args[0].equals("remove")) {
                    // check the name
                    TardisShopDoorData data = new TardisShopDoorResultSet().resultSet(args[0], 0);
                    if (data != null) {
                        // delete records
                        new TardisShopDoorQueries().doDelete(args[1]);
                        p.sendMessage("[TARDIS Door Shop] The door set called '" + args[1] + "' was removed.");
                    } else {
                        p.sendMessage("[TARDIS Door Shop] Could not find a door set with that name.");
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
