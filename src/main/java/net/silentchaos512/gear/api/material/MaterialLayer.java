package net.silentchaos512.gear.api.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.PartGearKey;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;

public class MaterialLayer {
    protected final ResourceLocation texture;
    protected final PartType partType;
    protected final int color; // TODO: Replace with a color provider?
    protected final boolean animated;

    public MaterialLayer(PartTextures texture, int color) {
        this(texture.getTexture(), texture.getPartType(), color, texture.isAnimated());
    }

    public MaterialLayer(ResourceLocation texture, int color) {
        this(texture, color, true);
    }

    public MaterialLayer(ResourceLocation texture, int color, boolean animated) {
        this(texture, null, color, animated);
    }

    public MaterialLayer(ResourceLocation texture, @Nullable PartType partType, int color, boolean animated) {
        this.texture = texture;
        this.partType = partType;
        this.color = color;
        this.animated = animated;
    }

    public MaterialLayer withColor(int color) {
        return new MaterialLayer(this.texture, this.partType, color, this.animated);
    }

    public ResourceLocation getTexture(GearType gearType, int animationFrame) {
        return getTexture(gearType.getName(), animationFrame);
    }

    public ResourceLocation getTexture(String texturePath, int animationFrame) {
        String path = "item/" + texturePath + "/" + this.texture.getPath();
        String suffix = animated && animationFrame > 0 ? "_" + animationFrame : "";
        return new ResourceLocation(this.texture.getNamespace(), path + suffix);
    }

    public ResourceLocation getTextureId() {
        return texture;
    }

    public PartType getPartType() {
        return partType;
    }

    public int getColor() {
        return color;
    }

    public boolean isAnimated() {
        return animated;
    }

    public static MaterialLayer deserialize(PartGearKey key, JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jo = json.getAsJsonObject();
            ResourceLocation texture = new ResourceLocation(JSONUtils.getString(jo, "texture"));
            int color = Color.from(jo, "color", Color.VALUE_WHITE).getColor();
            return new MaterialLayer(texture, key.getPartType(), color, true);
        }

        ResourceLocation texture = new ResourceLocation(json.getAsString());
        return new MaterialLayer(texture, Color.VALUE_WHITE);
    }

    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("texture", this.texture.toString());
        if ((this.color & 0xFFFFFF) != (Color.VALUE_WHITE & 0xFFFFFF)) {
            json.addProperty("color", Color.format(this.color));
        }
        return json;
    }

    public static MaterialLayer read(PacketBuffer buffer) {
        ResourceLocation texture = buffer.readResourceLocation();
        int color = buffer.readVarInt();
        return new MaterialLayer(texture, color);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.texture);
        buffer.writeVarInt(this.color);
    }

    @Override
    public String toString() {
        return "MaterialLayer{" +
                "texture=" + texture +
                "partType=" + partType.getName() +
                ", color=" + Color.format(color) +
                '}';
    }
}
