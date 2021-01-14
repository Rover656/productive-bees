package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Catcher;
import cy.jdkdigital.productivebees.common.tileentity.CatcherTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CatcherContainer extends AbstractContainer
{
    public final CatcherTileEntity tileEntity;

    public final IWorldPosCallable canInteractWithCallable;

    public CatcherContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CatcherContainer(final int windowId, final PlayerInventory playerInventory, final CatcherTileEntity tileEntity) {
        this(ModContainerTypes.CATCHER.get(), windowId, playerInventory, tileEntity);
    }

    public CatcherContainer(@Nullable ContainerType<?> type, final int windowId, final PlayerInventory playerInventory, final CatcherTileEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            // Comb and bottle slots
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 139, 17));

            // Inventory slots
            addSlotBox(inv, InventoryHandlerHelper.OUTPUT_SLOTS[0], 67, 17, 3, 18, 3, 18);
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, - 5, 84);
    }

    private static CatcherTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof CatcherTileEntity) {
            return (CatcherTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Catcher && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
