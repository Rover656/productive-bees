package cy.jdkdigital.productivebees.client.render.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FeederTileEntityRenderer extends TileEntityRenderer<FeederTileEntity>
{
    public static final HashMap<Integer, List<Pair<Float, Float>>> POSITIONS = new HashMap<Integer, List<Pair<Float, Float>>>()
    {{
        put(1, new ArrayList<Pair<Float, Float>>()
        {{
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.5f, 0.5f));
        }});
        put(2, new ArrayList<Pair<Float, Float>>()
        {{
            add(Pair.of(0.3f, 0.3f));
            add(Pair.of(0.5f, 0.5f));
            add(Pair.of(0.7f, 0.7f));
        }});
        put(3, new ArrayList<Pair<Float, Float>>()
        {{
            add(Pair.of(0.3f, 0.3f));
            add(Pair.of(0.5f, 0.7f));
            add(Pair.of(0.7f, 0.4f));
        }});
    }};

    public FeederTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public void render(FeederTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        tileEntityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            int filledSlots = 0;
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                if (!handler.getStackInSlot(slot).isEmpty()) {
                    filledSlots++;
                }
            }

            if (filledSlots > 0) {
                for (int slot = 0; slot < handler.getSlots(); ++slot) {
                    ItemStack slotStack = handler.getStackInSlot(slot);

                    if (slotStack.isEmpty()) continue;

                    boolean isFlower = slotStack.getItem().isIn(ItemTags.FLOWERS);
                    Pair<Float, Float> pos = POSITIONS.get(filledSlots).get(slot);
                    float rotation = isFlower ? 90F : 35.0F * slot;
                    float zScale = isFlower ? 0.775F : 0.575F;

                    matrixStackIn.push();
                    matrixStackIn.translate(pos.getFirst(), 0.52D, pos.getSecond());
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(rotation));
                    matrixStackIn.scale(0.575F, zScale, 0.575F);
                    Minecraft.getInstance().getItemRenderer().renderItem(slotStack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                    matrixStackIn.pop();
                }
            }
        });
    }
}