package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.UpgradeItem;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.common.tileentity.UpgradeableTileEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class AbstractContainer extends Container
{
    protected AbstractContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    protected abstract TileEntity getTileEntity();

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack slotStack = slot.getStack();
            returnStack = slotStack.copy();

            final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();

            // Move from container to player inventory.
            if (index < containerSlots) {
                if (!mergeItemStack(slotStack, containerSlots, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inv into container
                int upgradeSlotCount = this.getTileEntity() instanceof UpgradeableTileEntity ? 4 : 0;
                if (upgradeSlotCount > 0 && slotStack.getItem() instanceof UpgradeItem) {
                    if (!mergeItemStack(slotStack, containerSlots - upgradeSlotCount, containerSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!mergeItemStack(slotStack, 0, containerSlots - upgradeSlotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

    protected int addSlotRange(IInventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) handler, index, x, y));
            } else {
                addSlot(new Slot(handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    protected void addSlotBox(IInventory handler, int startIndex, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            startIndex = addSlotRange(handler, startIndex, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void addSlotBox(IItemHandler handler, int startIndex, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            startIndex = addSlotRange(handler, startIndex, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void layoutPlayerInventorySlots(PlayerInventory inventory, int startIndex, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, startIndex + 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(inventory, startIndex, leftCol, topRow, 9, 18);
    }
}
