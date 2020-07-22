package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.block.Bottler;
import cy.jdkdigital.productivebees.container.BottlerContainer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.item.GeneBottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BottlerTileEntity extends FluidTankTileEntity implements INamedContainerProvider
{
    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == InventoryHandlerHelper.BOTTLE_SLOT || slot == InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT) {
                return super.isItemValid(slot, stack);
            }
            return false;
        }

        @Override
        public boolean isInputItem(Item item) {
            return item == Items.GLASS_BOTTLE;
        }
    });

    public LazyOptional<IFluidHandler> honeyInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            BottlerTileEntity.this.markDirty();
        }

        @Override
        public boolean isFluidValid(FluidStack stack)
        {
            return stack.getFluid().isIn(ModTags.HONEY);
        }
    });

    @Override
    public void markDirty() {
        super.markDirty();

        if (world != null) {
            inventoryHandler.ifPresent(inv -> {
                boolean hasBottle = !inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).isEmpty();
                world.setBlockState(pos, this.getBlockState().with(Bottler.HAS_BOTTLE, hasBottle));
            });
        }
    }

    public BottlerTileEntity() {
        super(ModTileEntityTypes.BOTTLER.get());
    }

    @Override
    public void tick() {
        BlockState state = world.getBlockState(pos.up());
        if (state.getBlock() == Blocks.PISTON_HEAD && state.get(DirectionalBlock.FACING) == Direction.DOWN) {
            // Check for ProductiveBeeEntity on top of block
            List<ProductiveBeeEntity> bees = world.getEntitiesWithinAABB(ProductiveBeeEntity.class, (new AxisAlignedBB(pos).grow(0.0D, 1.0D, 0.0D)));
            if (!bees.isEmpty()) {
                ProductiveBeeEntity bee = bees.iterator().next();
                inventoryHandler.ifPresent(inv -> {
                    ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    if (!bottles.isEmpty() && bee.isAlive()) {
                        ItemStack geneBottle = GeneBottle.getStack(bee);
                        Block.spawnAsEntity(world, pos.up(), geneBottle);

                        bee.onKillCommand();
                        bottles.shrink(1);
                    }
                });
            }
        }

        super.tick();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return honeyInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.BOTTLER.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new BottlerContainer(windowId, playerInventory, this);
    }
}