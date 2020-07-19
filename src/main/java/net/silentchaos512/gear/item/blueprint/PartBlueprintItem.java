package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.parts.PartType;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final PartType partType;
    private final ITag.INamedTag<Item> itemTag;

    public PartBlueprintItem(PartType partType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.partType = partType;
        this.itemTag = ItemTags.makeWrapperTag(new ResourceLocation(partType.getName().getNamespace(), "blueprints/" + partType.getName().getPath()).toString());
    }

    public PartType getPartType() {
        return partType;
    }

    @Override
    public ITag.INamedTag<Item> getItemTag() {
        return itemTag;
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        return this.partType.getDisplayName(0);
    }
}
