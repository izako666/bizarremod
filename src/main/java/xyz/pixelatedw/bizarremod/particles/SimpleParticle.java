package xyz.pixelatedw.bizarremod.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.pixelatedw.bizarremod.particles.data.GenericParticleData;
import xyz.pixelatedw.wypi.WyHelper;

@OnlyIn(Dist.CLIENT)
public class SimpleParticle extends TexturedParticle
{
	private ResourceLocation texture;
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);

	public SimpleParticle(World world, ResourceLocation texture, double posX, double posY, double posZ, double motionX, double motionY, double motionZ)
	{
		super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		this.texture = texture;
		this.maxAge = 30 + this.rand.nextInt(10);
		this.age = 0;
		this.particleScale = 1.3F;
		this.particleGravity = 0F;
		this.setColor(1.0F, 1.0F, 1.0F);
		this.canCollide = false;

		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo info, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		if (this.texture == null)
			return;

		Minecraft.getInstance().textureManager.bindTexture(this.texture);

		float scale = 0.1F * this.particleScale;
		float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
		float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
		float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		buffer.begin(7, VERTEX_FORMAT);
		buffer.pos(x - rotationX * scale - rotationXY * scale, y - rotationZ * scale, z - rotationYZ * scale - rotationXZ * scale).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(x - rotationX * scale + rotationXY * scale, y + rotationZ * scale, z - rotationYZ * scale + rotationXZ * scale).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(x + rotationX * scale + rotationXY * scale, y + rotationZ * scale, z + rotationYZ * scale + rotationXZ * scale).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(x + rotationX * scale - rotationXY * scale, y - rotationZ * scale, z + rotationYZ * scale - rotationXZ * scale).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
		Tessellator.getInstance().draw();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
	}

	@Override
	public void tick()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.particleGravity != 0)
			this.motionY = -0.04D * this.particleGravity;

		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.99D;
		this.motionY *= 0.99D;
		this.motionZ *= 0.99D;

		// The "Menacing" effect
		this.setPosition(this.posX + (WyHelper.randomDouble() / 10), this.posY + (WyHelper.randomDouble() / 10), this.posZ + (WyHelper.randomDouble() / 10));
		if (this.age + 5 >= this.maxAge)
		{
			if (this.particleScale > 0)
				this.setParticleScale(this.particleScale -= 0.1);

			if (this.particleAlpha > 0)
				this.particleAlpha -= 0.15;
		}

		if (this.age++ >= this.maxAge || this.onGround)
			this.setExpired();
	}

	public SimpleParticle setParticleAlpha(float f)
	{
		this.setAlphaF(f);
		return this;
	}

	public SimpleParticle setParticleScale(float f)
	{
		this.particleScale = f;
		return this;
	}

	public SimpleParticle setParticleGravity(float f)
	{
		this.particleGravity = f;
		return this;
	}

	public SimpleParticle setParticleAge(int i)
	{
		this.maxAge = i + this.rand.nextInt(10);
		return this;
	}

	public SimpleParticle setParticleTexture(ResourceLocation rs)
	{
		this.texture = rs;
		return this;
	}

	public BlockPos getPos()
	{
		return new BlockPos(this.posX, this.posY, this.posZ);
	}

	@Override
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.CUSTOM;
	}

	@Override
	protected float getMaxU()
	{
		return 0;
	}

	@Override
	protected float getMaxV()
	{
		return 0;
	}

	@Override
	protected float getMinU()
	{
		return 0;
	}

	@Override
	protected float getMinV()
	{
		return 0;
	}

	public static class Factory implements IParticleFactory<GenericParticleData>
	{
		public Factory()
		{
		}

		@Override
		public Particle makeParticle(GenericParticleData data, World world, double posX, double posY, double posZ, double velX, double velY, double velZ)
		{
			SimpleParticle particle = new SimpleParticle(world, data.getTexture(), posX, posY, posZ, data.getMotionX(), data.getMotionY(), data.getMotionZ());
			particle.setColor(data.getRed(), data.getGreen(), data.getBlue());
			particle.setParticleAlpha(data.getAlpha());
			particle.setParticleScale(data.getSize());
			particle.setParticleAge(data.getLife());
			
			return particle;
		}
	}
}