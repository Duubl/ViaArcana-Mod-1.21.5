package com.duubl.via_arcana.client;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.entities.projectiles.BaseSpellProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class BaseSpellProjectileRenderer extends EntityRenderer<BaseSpellProjectile, EntityRenderState> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "textures/entity/projectile/base_spell_projectile.png");

    public BaseSpellProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityRenderState state, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        
        poseStack.popPose();
        super.render(state, poseStack, buffer, light);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void extractRenderState(BaseSpellProjectile entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }
} 