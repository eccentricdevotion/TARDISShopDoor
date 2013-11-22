/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import me.eccentric_nz.tardisshopdoor.TardisShopDoor.DIRECTION;
import org.bukkit.block.BlockFace;

/**
 *
 * @author eccentric_nz
 */
public class TardisShopDoorData {

    private final int id;
    private final String name;
    private final String location;
    private final int type;
    private final DIRECTION direction;

    public TardisShopDoorData(int id, String name, String location, int type, DIRECTION direction) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getType() {
        return type;
    }

    public DIRECTION getDirection() {
        return direction;
    }
}
