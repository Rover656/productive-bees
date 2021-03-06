package cy.jdkdigital.productivebees.network.packets;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Messages
{
    public static class BeesMessage
    {
        public Map<String, CompoundNBT> data;

        public BeesMessage(Map<String, CompoundNBT> data) {
            this.data = data;
        }

        public static void encode(BeesMessage message, PacketBuffer buffer) {
            buffer.writeInt(message.data.size());
            for (Map.Entry<String, CompoundNBT> entry : message.data.entrySet()) {
                buffer.writeString(entry.getKey());
                buffer.writeCompoundTag(entry.getValue());
            }
        }

        public static BeesMessage decode(PacketBuffer buffer) {
            Map<String, CompoundNBT> data = new HashMap<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> {
                data.put(buffer.readString(), buffer.readCompoundTag());
            });
            return new BeesMessage(data);
        }

        public static void handle(BeesMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                BeeReloadListener.INSTANCE.setData(message.data);
                // Trigger jei reload
                ProductiveBees.LOGGER.info("trigger recipe reload");
                RecipeManager manager = ProductiveBees.proxy.getClientWorld().getRecipeManager();
                net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(manager);
            });
            context.get().setPacketHandled(true);
        }
    }
}
