package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

/**
 * APG door light model for Tianjin variant.
 * Re-created from MTR 3.5.9 RenderPSDAPGDoor.ModelAPGDoorLight since it is private.
 */
public class ModelAPGDoorLight extends EntityModel<Entity> {
    private final ModelPart root;

    public ModelAPGDoorLight() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();

        PartDefinition bone = rootDef.addOrReplaceChild("bone", CubeListBuilder.create()
                        .texOffs(0, 4).addBox(-0.5F, -9, -7, 1, 1, 3, new CubeDeformation(0.05F)),
                PartPose.ZERO);

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.5F, 0.05F, -3.05F, 1, 1, 3, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0, -9.05F, -4.95F, 0.3927F, 0, 0));

        this.root = LayerDefinition.create(mesh, 8, 8).bakeRoot();
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
