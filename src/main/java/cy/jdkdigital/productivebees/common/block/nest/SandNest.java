package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

public class SandNest extends SolitaryNest
{
    public SandNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return dimension.isSurfaceWorld() && biome.getCategory().equals(Biome.Category.DESERT);
    }
}
