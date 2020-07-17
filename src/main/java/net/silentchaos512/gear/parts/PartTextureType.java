package net.silentchaos512.gear.parts;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.client.model.PartTextures;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum PartTextureType {
    ABSENT(ImmutableMap.of()),
    LOW_CONTRAST(ImmutableMap.<PartType, List<ResourceLocation>>builder()
            .put(PartType.MAIN, ImmutableList.of(PartTextures.MAIN_GENERIC_LC))
            .put(PartType.ROD, ImmutableList.of(PartTextures.ROD_GENERIC_LC))
            .put(PartType.TIP, ImmutableList.of(PartTextures.TIP_IRON))
            .put(PartType.BINDING, ImmutableList.of(PartTextures.BINDING_GENERIC))
            .put(PartType.GRIP, ImmutableList.of(PartTextures.GRIP_WOOL))
            .put(PartType.BOWSTRING, ImmutableList.of(PartTextures.BOWSTRING_STRING))
            .put(PartType.FLETCHING, ImmutableList.of(PartTextures.FLETCHING_GENERIC))
            .build()),
    HIGH_CONTRAST(ImmutableMap.<PartType, List<ResourceLocation>>builder()
            .put(PartType.MAIN, ImmutableList.of(PartTextures.MAIN_GENERIC_HC))
            .put(PartType.ROD, ImmutableList.of(PartTextures.ROD_GENERIC_HC))
            .put(PartType.TIP, ImmutableList.of(PartTextures.TIP_IRON))
            .put(PartType.BINDING, ImmutableList.of(PartTextures.BINDING_GENERIC))
            .put(PartType.GRIP, ImmutableList.of(PartTextures.GRIP_WOOL))
            .put(PartType.BOWSTRING, ImmutableList.of(PartTextures.BOWSTRING_STRING))
            .put(PartType.FLETCHING, ImmutableList.of(PartTextures.FLETCHING_GENERIC))
            .build()),
    HIGH_CONTRAST_WITH_HIGHLIGHT(ImmutableMap.<PartType, List<ResourceLocation>>builder()
            .put(PartType.MAIN, ImmutableList.of(PartTextures.MAIN_GENERIC_HC, PartTextures.HIGHLIGHT))
            .put(PartType.ROD, ImmutableList.of(PartTextures.ROD_GENERIC_HC))
            .put(PartType.TIP, ImmutableList.of(PartTextures.TIP_IRON))
            .put(PartType.BINDING, ImmutableList.of(PartTextures.BINDING_GENERIC))
            .put(PartType.GRIP, ImmutableList.of(PartTextures.GRIP_WOOL))
            .put(PartType.BOWSTRING, ImmutableList.of(PartTextures.BOWSTRING_STRING))
            .put(PartType.FLETCHING, ImmutableList.of(PartTextures.FLETCHING_GENERIC))
            .build());

    final Map<PartType, List<ResourceLocation>> layers;

    PartTextureType(Map<PartType, List<ResourceLocation>> layers) {
        this.layers = layers;
    }

    public int getIndex() {
        return ordinal();
    }

    public List<ResourceLocation> getLayers(PartType partType) {
        return layers.getOrDefault(partType, Collections.emptyList());
    }
}
