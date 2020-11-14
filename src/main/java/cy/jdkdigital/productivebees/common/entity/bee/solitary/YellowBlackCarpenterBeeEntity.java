package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;

public class YellowBlackCarpenterBeeEntity extends SolitaryBeeEntity
{
    public YellowBlackCarpenterBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ITag<Block> getFlowerTag() {
        return ModTags.FOREST_FLOWERS;
    }
}
