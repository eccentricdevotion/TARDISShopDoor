/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import me.eccentric_nz.tardisshopdoor.TardisShopDoor.DIRECTION;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author eccentric_nz
 */
public class TardisShopDoorListener implements Listener {

    private final TardisShopDoor plugin;
    public float[][] adjustYaw = new float[4][4];

    public TardisShopDoorListener(TardisShopDoor plugin) {
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
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && b.getType().equals(Material.IRON_DOOR_BLOCK)) {
            @SuppressWarnings("deprecation")
            byte data = b.getData();
            // is it the top or bottom half of the door?
            String loc = (data < 8) ? b.getLocation().toString() : b.getRelative(BlockFace.DOWN).getLocation().toString();
            if (plugin.trackAdd.containsKey(p.getUniqueId())) {
                // we're in add mode
                // get door direction
                String direction = getPlayersDirection(p);
                TardisShopDoorAdd a = plugin.trackAdd.get(p.getUniqueId());
                int type = a.getType();
                // save the door
                new TardisShopDoorQueries().doInsert(a.getName(), loc, type, direction);
                if (type == 0) {
                    a.setType(1);
                    plugin.trackAdd.put(p.getUniqueId(), a);
                    p.sendMessage("[TARDIS Door Shop] First door saved! Click on the second door.");
                } else {
                    p.sendMessage("[TARDIS Door Shop] Second door saved!");
                    plugin.trackAdd.remove(p.getUniqueId());
                }
            } else if (p.hasPermission("tardis.shop")) {
                // get their key prefs
                Material key = new TardisShopDoorKey(plugin.tardis).getKeyPref(p.getUniqueId().toString());
                // we're in teleport mode
                TardisShopDoorResultSet rs = new TardisShopDoorResultSet();
                TardisShopDoorData from = rs.resultSet(loc);
                if (from != null) {
                    if (p.getInventory().getItemInMainHand().getType().equals(key)) {
                        // get the other side of the door
                        int type = (from.getType() == 0) ? 1 : 0;
                        TardisShopDoorData to = rs.resultSet(from.getName(), type);
                        if (to != null) {
                            // teleport the player
                            tp(p, to, from);
                        }
                    } else {
                        p.sendMessage("[TARDIS Door Shop] You must click the door with your TARDIS key!");
                    }
                }
            }
        }
    }

    private void tp(Player p, TardisShopDoorData to, TardisShopDoorData from) {
        Location l = getLocationFromBukkitString(to.getLocation());
        int getx = l.getBlockX();
        int getz = l.getBlockZ();
        // adjust position
        switch (to.getDirection()) {
            case EAST:
                l.setX(getx - 0.5);
                l.setZ(getz + 0.5);
                break;
            case SOUTH:
                l.setX(getx + 0.5);
                l.setZ(getz - 0.5);
                break;
            case WEST:
                l.setX(getx + 1.5);
                l.setZ(getz + 0.5);
                break;
            default:
                l.setX(getx + 0.5);
                l.setZ(getz + 1.5);
                break;
        }
        // set pitch and yaw
        l.setPitch(p.getLocation().getPitch());
        l.setYaw(p.getLocation().getYaw() + adjustYaw(from.getDirection(), to.getDirection()));
        // teleport
        p.teleport(l);
        l.getWorld().playEffect(l, Effect.DOOR_TOGGLE, 0);
    }

    private Location getLocationFromBukkitString(String string) {
        //Location{world=CraftWorld{name=world},x=0.0,y=0.0,z=0.0,pitch=0.0,yaw=0.0}
        String[] loc_data = string.split(",");
        String[] wStr = loc_data[0].split("=");
        String[] xStr = loc_data[1].split("=");
        String[] yStr = loc_data[2].split("=");
        String[] zStr = loc_data[3].split("=");
        World w = plugin.getServer().getWorld(wStr[2].substring(0, (wStr[2].length() - 1)));
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
        return new Location(w, x, y, z);
    }

    private float adjustYaw(DIRECTION d1, DIRECTION d2) {
        switch (d1) {
            case EAST:
                return adjustYaw[0][d2.ordinal()];
            case SOUTH:
                return adjustYaw[1][d2.ordinal()];
            case WEST:
                return adjustYaw[2][d2.ordinal()];
            default:
                return adjustYaw[3][d2.ordinal()];
        }
    }

    public String getPlayersDirection(Player p) {
        // get player direction
        float pyaw = p.getLocation().getYaw();
        if (pyaw >= 0) {
            pyaw = (pyaw % 360);
        } else {
            pyaw = (360 + (pyaw % 360));
        }
        // determine direction player is facing
        String d = "";
        if (pyaw >= 315 || pyaw < 45) {
            d = "SOUTH";
        }
        if (pyaw >= 225 && pyaw < 315) {
            d = "EAST";
        }
        if (pyaw >= 135 && pyaw < 225) {
            d = "NORTH";
        }
        if (pyaw >= 45 && pyaw < 135) {
            d = "WEST";
        }
        return d;
    }
}
