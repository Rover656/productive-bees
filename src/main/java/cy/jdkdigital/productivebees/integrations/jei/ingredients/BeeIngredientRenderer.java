package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    @Override
    public void render(int xPosition, int yPosition, @Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world != null) {

            BeeEntity bee = beeIngredient.getBeeType().create(minecraft.world);


            if (minecraft.player != null && bee != null) {
                bee.ticksExisted = minecraft.player.ticksExisted;
                bee.renderYawOffset = -15;

                float scaledSize = 28;
                if (bee instanceof SolitaryBeeEntity) {
                    scaledSize = scaledSize * 0.85F;
                }

                MatrixStack matrixStack = new MatrixStack();
                matrixStack.push();
                matrixStack.translate(7 + xPosition, 17 + yPosition, 1.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(scaledSize, scaledSize, 32);

                EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
                IRenderTypeBuffer.Impl buffer = minecraft.getRenderTypeBuffers().getBufferSource();
                entityrenderermanager.renderEntityStatic(bee, 0, 0, 0.0D, minecraft.getRenderPartialTicks(), 1, matrixStack, buffer, 15728880);
                buffer.finish();
                matrixStack.pop();
            }
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltip(BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<String> list = new ArrayList<>();
        list.add(beeIngredient.getBeeType().getName().getFormattedText());
        list.add(TextFormatting.DARK_GRAY + "" + beeIngredient.getBeeType().getRegistryName());
        return list;
    }
}
