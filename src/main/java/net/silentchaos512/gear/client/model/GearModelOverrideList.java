package net.silentchaos512.gear.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.gear.CoreCrossbow;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.type.CompoundPart;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GearModelOverrideList extends ItemOverrideList {
    private final Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final GearModel model;
    private final IModelConfiguration owner;
    private final ModelBakery bakery;
    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
    private final IModelTransform modelTransform;
    private final ResourceLocation modelLocation;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public GearModelOverrideList(GearModel model,
                                 IModelConfiguration owner,
                                 ModelBakery bakery,
                                 Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                 IModelTransform modelTransform,
                                 ResourceLocation modelLocation) {
        this.model = model;
        this.owner = owner;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
        this.modelLocation = modelLocation;
    }

    // getModelWithOverrides
    @Nullable
    @Override
    public IBakedModel func_239290_a_(IBakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        int animationFrame = getAnimationFrame(stack, worldIn, entityIn);
        CacheKey key = getKey(model, stack, worldIn, entityIn, animationFrame);
        try {
            return bakedModelCache.get(key, () -> getOverrideModel(stack, worldIn, entityIn, animationFrame));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return model;
    }

    private static int getAnimationFrame(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return ((ICoreItem) stack.getItem()).getAnimationFrame(stack, world, entity);
    }

    private IBakedModel getOverrideModel(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn, int animationFrame) {
        List<MaterialLayer> layers = new ArrayList<>();

        for (PartData part : GearData.getConstructionParts(stack)) {
            if (part.getPart() instanceof CompoundPart) {
                MaterialInstance mat = CompoundPart.getPrimaryMaterial(part);
                if (mat != null) {
                    layers.addAll(mat.getMaterial().getMaterialDisplay(stack, part.getType()).getLayers());
                }
            } else {
                // Legacy parts (remove later?)
                layers.addAll(part.getPart().getLiteTexture(part, stack).getLayers(part.getType()).stream()
                        .map(loc -> {
                            int c = loc.equals(SilentGear.getId("_highlight")) ? Color.VALUE_WHITE : part.getColor(stack, animationFrame);
                            PartTextures tex = PartTextures.byTextureId(loc);
                            return tex != null ? tex.getLayer(c) : new MaterialLayer(loc, c);
                        })
                        .collect(Collectors.toList()));
            }
        }

        if (stack.getItem() instanceof CoreCrossbow) {
            getCrossbowCharge(stack, worldIn, entityIn).ifPresent(layers::add);
        }

        return model.bake(layers, animationFrame, "test", owner, bakery, spriteGetter, modelTransform, this, modelLocation);
    }

    private static Optional<MaterialLayer> getCrossbowCharge(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        // TODO: Maybe should add an ICoreItem method to get additional layers?
        float charged = ModelPropertiesHelper.getValue(stack, new ResourceLocation("charged"), world, entity);
        float firework = ModelPropertiesHelper.getValue(stack, new ResourceLocation("firework"), world, entity);

        if (charged > 0) {
            if (firework > 0) {
                return Optional.of(new MaterialLayer(PartTextures.CHARGED_FIREWORK, Color.VALUE_WHITE));
            }
            return Optional.of(new MaterialLayer(PartTextures.CHARGED_ARROW, Color.VALUE_WHITE));
        }

        return Optional.empty();
    }

    private static CacheKey getKey(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int animationFrame) {
        String chargeSuffix = getCrossbowCharge(stack, world, entity)
                .map(l -> l.getTextureId().getPath())
                .orElse("");
        return new CacheKey(model, GearData.getModelKey(stack, animationFrame) + chargeSuffix);
    }

    @Override
    public ImmutableList<ItemOverride> getOverrides() {
        return super.getOverrides();
    }

    public void clearCache() {
        bakedModelCache.invalidateAll();
    }

    static final class CacheKey {
        final IBakedModel parent;
        final String data;

        CacheKey(IBakedModel parent, String hash) {
            this.parent = parent;
            this.data = hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return parent == cacheKey.parent && Objects.equals(data, cacheKey.data);
        }

        @Override
        public int hashCode() {
            return 31 * parent.hashCode() + data.hashCode();
        }
    }
}
