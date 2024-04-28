
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
package com.falsepattern.falsetweaks.asm.modules.threadedupdates.settings;

import com.falsepattern.lib.asm.IClassNodeTransformer;
import com.falsepattern.lib.mapping.MappingManager;
import com.falsepattern.lib.mapping.types.MappingType;
import com.falsepattern.lib.mapping.types.NameType;
import com.falsepattern.lib.mapping.types.UniversalClass;
import com.falsepattern.lib.mapping.types.UniversalField;
import lombok.NoArgsConstructor;
import lombok.val;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

@NoArgsConstructor
public final class Threading_GameSettings implements IClassNodeTransformer {
    static final UniversalClass GAME_SETTINGS_CLASS;
    static final UniversalField FANCY_GRAPHICS_FIELD;

    static {
        try {
            GAME_SETTINGS_CLASS = MappingManager.classForName(NameType.Regular,
                                                              MappingType.MCP,
                                                              "net.minecraft.client.settings.GameSettings");
            FANCY_GRAPHICS_FIELD = GAME_SETTINGS_CLASS.getField(MappingType.MCP, "fancyGraphics");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new AssertionError("Woe be upon ye traveler!", e);
        }
    }

    @Override
    public String getName() {
        return "Threading_GameSettings";
    }

    @Override
    public boolean shouldTransform(ClassNode cn, String transformedName, boolean obfuscated) {
        return GAME_SETTINGS_CLASS.regularName.srg.equals(transformedName);
    }

    @Override
    public void transform(ClassNode cn, String transformedName, boolean obfuscated) {
        cn.fields.removeIf(Threading_GameSettings::shouldRemoveField);
    }

    static boolean isTargetOwner(FieldInsnNode fieldInst) {
        if (fieldInst == null)
            return false;
        val owner = fieldInst.owner;
        if (owner == null)
            return false;

        if (owner.equals(GAME_SETTINGS_CLASS.getName(NameType.Internal, MappingType.Notch)))
            return true;
        if (owner.equals(GAME_SETTINGS_CLASS.getName(NameType.Internal, MappingType.SRG)))
            return true;
        return owner.equals(GAME_SETTINGS_CLASS.getName(NameType.Internal, MappingType.MCP));
    }

    static boolean shouldRemoveField(FieldNode fieldNode) {
        return tryMapFieldNodeToMCP(fieldNode) != null;
    }

    static String tryMapFieldNodeToMCP(FieldNode fieldNode) {
        if (fieldNode == null)
            return null;
        return tryMapFieldNameToMCP(fieldNode.name);
    }

    static String tryMapFieldNameToMCP(String fieldName) {
        if (fieldName == null)
            return null;

        val mcpName = FANCY_GRAPHICS_FIELD.getName(MappingType.MCP);
        if (fieldName.equals(mcpName))
            return mcpName;
        if (fieldName.equals(FANCY_GRAPHICS_FIELD.getName(MappingType.Notch)))
            return mcpName;
        if (fieldName.equals(FANCY_GRAPHICS_FIELD.getName(MappingType.SRG)))
            return mcpName;

        return null;
    }
}
