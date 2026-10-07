package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * APG door bottom model for Tianjin variant.
 * Re-created from MTR 3.5.9 RenderPSDAPGDoor.ModelAPGDoorBottom since it is private.
 */
public class ModelAPGDoorBottom extends EntityModel<Entity> {
    private final ModelPart root;

    public ModelAPGDoorBottom() {
        // 1.16.5 没有 LayerDefinition，纹理尺寸直接写回 Model 字段（等价于 LayerDefinition.create(mesh, 34, 27)）
        this.texWidth = 34;
        this.texHeight = 27;

        // 等价于 rootDef.addOrReplaceChild("bone", CubeListBuilder..., PartPose.ZERO)
        final ModelPart bone = new ModelPart(34, 27, 0, 0);
        bone.setPos(0, 0, 0);
        bone.texOffs(0, 0).addBox(-8, -16, -7, 16, 16, 1);
        bone.texOffs(0, 17).addBox(-8, -6, -8, 16, 6, 1);

        // 等价于 bone.addOrReplaceChild("cube_r1", CubeListBuilder..., PartPose.offsetAndRotation(0, -6, -8, -0.7854F, 0, 0))
        final ModelPart cubeR1 = new ModelPart(34, 27, 0, 0);
        cubeR1.setPos(0, -6, -8);
        cubeR1.xRot = -0.7854F;
        cubeR1.yRot = 0;
        cubeR1.zRot = 0;
        cubeR1.texOffs(0, 24).addBox(-8, -2, 0, 16, 2, 1);
        bone.addChild(cubeR1);

        // 等价于 LayerDefinition.create(mesh, 34, 27).bakeRoot()
        final ModelPart root = new ModelPart(34, 27, 0, 0);
        root.setPos(0, 0, 0);
        root.addChild(bone);
        this.root = root;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
