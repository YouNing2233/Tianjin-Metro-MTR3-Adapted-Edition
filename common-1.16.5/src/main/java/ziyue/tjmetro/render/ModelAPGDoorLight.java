package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * APG door light model for Tianjin variant.
 * Re-created from MTR 3.5.9 RenderPSDAPGDoor.ModelAPGDoorLight since it is private.
 */
public class ModelAPGDoorLight extends EntityModel<Entity> {
    private final ModelPart root;

    public ModelAPGDoorLight() {
        // 1.16.5 没有 LayerDefinition，纹理尺寸直接写回 Model 字段（等价于 LayerDefinition.create(mesh, 8, 8)）
        this.texWidth = 8;
        this.texHeight = 8;

        // 等价于 rootDef.addOrReplaceChild("bone", CubeListBuilder..., PartPose.ZERO)
        // 膨胀量 0.05F 等价于 1.18 的 new CubeDeformation(0.05F)：7 参数 addBox 会把 grow 同时用于 X/Y/Z 三个轴。
        // 注意该重载返回 void，不能链式调用，必须把 texOffs 拆成单独一句。
        final ModelPart bone = new ModelPart(8, 8, 0, 0);
        bone.setPos(0, 0, 0);
        bone.texOffs(0, 4);
        bone.addBox(-0.5F, -9, -7, 1, 1, 3, 0.05F);

        // 等价于 bone.addOrReplaceChild("cube_r1", CubeListBuilder..., PartPose.offsetAndRotation(0, -9.05F, -4.95F, 0.3927F, 0, 0))
        final ModelPart cubeR1 = new ModelPart(8, 8, 0, 0);
        cubeR1.setPos(0, -9.05F, -4.95F);
        cubeR1.xRot = 0.3927F;
        cubeR1.yRot = 0;
        cubeR1.zRot = 0;
        cubeR1.texOffs(0, 0);
        cubeR1.addBox(-0.5F, 0.05F, -3.05F, 1, 1, 3, 0.05F);
        bone.addChild(cubeR1);

        // 等价于 LayerDefinition.create(mesh, 8, 8).bakeRoot()
        final ModelPart root = new ModelPart(8, 8, 0, 0);
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
