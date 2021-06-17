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

import me.eccentric_nz.tardisshopdoor.TardisShopDoorPlugin.DIRECTION;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorListener implements Listener {

    private final TardisShopDoorPlugin plugin;
    public float[][] adjustYaw = new float[4][4];

    public TardisShopDoorListener(TardisShopDoorPlugin plugin) {
        this.plugin = plugin;
        // yaw adjustments if inner and outer door directions are different
        // 0 = EAST, 1 = SOUTH, 2 = WEST, 3 = NORTH
        adjustYaw[0][0] = 180;
        adjustYaw[0][1] = -90;
        adjustYaw[0][2] = 0;
        adjustYaw[0][3] = 90;
        adjustYaw[1][0] = 90;
        adjustYaw[1][1] = 180;
        adjustYaw[1][2] = -90;
        adjustYaw[1][3] = 0;
        adjustYaw[2][0] = 0;
        adjustYaw[2][1] = 90;
        adjustYaw[2][2] = 180;
        adjustYaw[2][3] = -90;
        adjustYaw[3][0] = -90;
        adjustYaw[3][1] = 0;
        adjustYaw[3][2] = 90;
        adjustYaw[3][3] = 180;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            assert block != null;
            if (block.getType().equals(Material.IRON_DOOR)) {
                Bisected bisected = (Bisected) block.getBlockData();
                // is it the top or bottom half of the door?
                String loc = (bisected.getHalf().equals(Bisected.Half.TOP)) ? block.getLocation().toString() : block.getRelative(BlockFace.DOWN).getLocation().toString();
                if (plugin.trackAdd.containsKey(player.getUniqueId())) {
                    // we're in add mode
                    // get door direction
                    String direction = getPlayersDirection(player);
                    TardisShopDoorAdd shopDoor = plugin.trackAdd.get(player.getUniqueId());
                    int type = shopDoor.getType();
                    // save the door
                    new TardisShopDoorQueries().doInsert(shopDoor.getName(), loc, type, direction);
                    if (type == 0) {
                        shopDoor.setType(1);
                        plugin.trackAdd.put(player.getUniqueId(), shopDoor);
                        player.sendMessage("[TARDIS Door Shop] First door saved! Click on the second door.");
                    } else {
                        player.sendMessage("[TARDIS Door Shop] Second door saved!");
                        plugin.trackAdd.remove(player.getUniqueId());
                    }
                } else if (player.hasPermission("tardis.shop")) {
                    // get their key prefs
                    Material key = new TardisShopDoorKey(plugin.tardis).getKeyPref(player.getUniqueId().toString());
                    // we're in teleport mode
                    TardisShopDoorResultSet resultSet = new TardisShopDoorResultSet();
                    TardisShopDoorData from = resultSet.resultSet(loc);
                    if (from != null) {
                        if (player.getInventory().getItemInMainHand().getType().equals(key)) {
                            // get the other side of the door
                            int type = (from.getType() == 0) ? 1 : 0;
                            TardisShopDoorData to = resultSet.resultSet(from.getName(), type);
                            if (to != null) {
                                // teleport the player
                                tp(player, to, from);
                            }
                        } else {
                            player.sendMessage("[TARDIS Door Shop] You must click the door with your TARDIS key!");
                        }
                    }
                }
            }
        }
    }

    private void tp(Player player, TardisShopDoorData to, TardisShopDoorData from) {
        Location loc = getLocationFromBukkitString(to.getLocation());
        assert loc != null;
        int getX = loc.getBlockX();
        int getZ = loc.getBlockZ();
        // adjust position
        switch (to.getDirection()) {
            case EAST -> {
                loc.setX(getX - 0.5);
                loc.setZ(getZ + 0.5);
            }
            case SOUTH -> {
                loc.setX(getX + 0.5);
                loc.setZ(getZ - 0.5);
            }
            case WEST -> {
                loc.setX(getX + 1.5);
                loc.setZ(getZ + 0.5);
            }
            default -> {
                loc.setX(getX + 0.5);
                loc.setZ(getZ + 1.5);
            }
        }
        // set pitch and yaw
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw() + adjustYaw(from.getDirection(), to.getDirection()));
        // teleport
        player.teleport(loc);
        Objects.requireNonNull(loc.getWorld()).playEffect(loc, Effect.DOOR_TOGGLE, 0);
    }

    private Location getLocationFromBukkitString(String string) {
        //Location{world=CraftWorld{name=world},x=0.0,y=0.0,z=0.0,pitch=0.0,yaw=0.0}
        String[] loc_data = string.split(",");
        String[] wStr = loc_data[0].split("=");
        String[] xStr = loc_data[1].split("=");
        String[] yStr = loc_data[2].split("=");
        String[] zStr = loc_data[3].split("=");
        World world = plugin.getServer().getWorld(wStr[2].substring(0, (wStr[2].length() - 1)));
        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(xStr[1].substring(0, (xStr[1].length() - 2)));
            y = Integer.parseInt(yStr[1].substring(0, (yStr[1].length() - 2)));
            z = Integer.parseInt(zStr[1].substring(0, (zStr[1].length() - 2)));
        } catch (NumberFormatException e) {
            System.err.println("[TARDIS Shop Door] Could not convert to number! " + e.getMessage());
            return null;
        }
        return new Location(world, x, y, z);
    }

    private float adjustYaw(DIRECTION direction1, DIRECTION direction2) {
        return switch (direction1) {
            case EAST -> adjustYaw[0][direction2.ordinal()];
            case SOUTH -> adjustYaw[1][direction2.ordinal()];
            case WEST -> adjustYaw[2][direction2.ordinal()];
            default -> adjustYaw[3][direction2.ordinal()];
        };
    }

    public String getPlayersDirection(Player player) {
        // get player direction
        float playerYaw = player.getLocation().getYaw();
        if (playerYaw >= 0) {
            playerYaw = (playerYaw % 360);
        } else {
            playerYaw = (360 + (playerYaw % 360));
        }
        // determine direction player is facing
        String direction = "";
        if (playerYaw >= 315 || playerYaw < 45) {
            direction = "SOUTH";
        }
        if (playerYaw >= 225 && playerYaw < 315) {
            direction = "EAST";
        }
        if (playerYaw >= 135 && playerYaw < 225) {
            direction = "NORTH";
        }
        if (playerYaw >= 45 && playerYaw < 135) {
            direction = "WEST";
        }
        return direction;
    }
}
