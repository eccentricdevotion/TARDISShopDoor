/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

    TardisShopDoorPlugin plugin;

    public TardisShopDoorCommands(TardisShopDoorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("tsd")) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (player == null) {
                sender.sendMessage("[TARDIS Shop Door] Command can only be run by a player");
                return true;
            }
            if (!player.hasPermission("tardis.shopdoor") || player.isOp()) {
                if (args.length == 1) {
                    // check the name
                    TardisShopDoorData data = new TardisShopDoorResultSet().resultSet(args[0], 0);
                    if (data == null) {
                        // start tracking this name
                        TardisShopDoorAdd a = new TardisShopDoorAdd();
                        a.setName(args[0]);
                        a.setType(0);
                        plugin.trackAdd.put(player.getUniqueId(), a);
                        player.sendMessage("[TARDIS Door Shop] Click on the first door.");
                    } else {
                        player.sendMessage("[TARDIS Door Shop] A door set already exists with that name.");
                    }
                    return true;
                }
                if (args.length == 2 && args[0].equals("remove")) {
                    // check the name
                    TardisShopDoorData data = new TardisShopDoorResultSet().resultSet(args[0], 0);
                    if (data != null) {
                        // delete records
                        new TardisShopDoorQueries().doDelete(args[1]);
                        player.sendMessage("[TARDIS Door Shop] The door set called '" + args[1] + "' was removed.");
                    } else {
                        player.sendMessage("[TARDIS Door Shop] Could not find a door set with that name.");
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
