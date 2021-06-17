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

/**
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
