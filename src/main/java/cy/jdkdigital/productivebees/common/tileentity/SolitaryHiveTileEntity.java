package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;

public class SolitaryHiveTileEntity extends SolitaryNestTileEntity
{
    public SolitaryHiveTileEntity() {
        super(ModTileEntityTypes.SOLITARY_HIVE.get());
        MAX_BEES = 9;
    }

    public boolean canRepopulate() {
        return false;
    }
}
