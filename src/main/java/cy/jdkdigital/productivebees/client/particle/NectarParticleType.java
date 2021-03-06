package cy.jdkdigital.productivebees.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NectarParticleType extends ParticleType<NectarParticleType> implements IParticleData
{
    private float[] color = null;

    private static final IDeserializer<NectarParticleType> DESERIALIZER = new IDeserializer<NectarParticleType>()
    {
        @Nonnull
        @Override
        public NectarParticleType deserialize(@Nonnull ParticleType<NectarParticleType> particleTypeIn, @Nonnull StringReader reader) throws CommandSyntaxException {
            return (NectarParticleType) particleTypeIn;
        }

        @Nonnull
        @Override
        public NectarParticleType read(@Nonnull ParticleType<NectarParticleType> particleTypeIn, @Nonnull PacketBuffer buffer) {
            return (NectarParticleType) particleTypeIn;
        }
    };

    public NectarParticleType() {
        super(false, DESERIALIZER);
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    @Nullable
    public float[] getColor() {
        return this.color;
    }

    @Nonnull
    @Override
    public ParticleType<NectarParticleType> getType() {
        return this;
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer) {
    }

    @Nonnull
    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this).toString();
    }
}