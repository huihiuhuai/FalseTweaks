/*
 * This file is part of FalseTweaks.
 *
 * Copyright (C) 2022-2024 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * FalseTweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FalseTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FalseTweaks. If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.falsetweaks.modules.triangulator.sorting;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import lombok.Data;
import org.joml.Vector3f;

@Data
public class TreeNode {
    public float normalX;
    public float normalY;
    public float normalZ;
    public float originX;
    public float originY;
    public float originZ;
    public TIntList triangleRefsList;
    public int start;
    public int length;
    public int frontRef;
    public int backRef;

    public TreeNode(float normalX, float normalY, float normalZ,
                    float originX, float originY, float originZ,
                    TIntList triangleRefs, int start, int length,
                    int frontRef, int backRef) {
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.triangleRefsList = triangleRefs;
        this.start = start;
        this.length = length;
        this.frontRef = frontRef;
        this.backRef = backRef;
    }
}
