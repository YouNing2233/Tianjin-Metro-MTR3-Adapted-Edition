package ziyue.tjmetro.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

/**
 * Simple cube model for Tianjin door rendering.
 * Ported from MTR 4.0 ModelSingleCube to MTR 3.5.9.
 */
public class ModelSingleCube extends EntityModel<Entity> {
    private final ModelPart root;

    public ModelSingleCube() {
        this(1, 1, 0, 0, 0, 1, 1, 1);
    }

    public ModelSingleCube(int textureWidth, int textureHeight, float x, float y, float z, float length, float height, float depth) {
        // 1.16.5 没有 LayerDefinition，纹理尺寸直接写回 Model 字段（等价于 LayerDefinition.create(mesh, textureWidth, textureHeight)）
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;

        // 等价于 rootDef.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(...), PartPose.ZERO)
        final ModelPart cube = new ModelPart(textureWidth, textureHeight, 0, 0);
        cube.setPos(0, 0, 0);
        cube.texOffs(0, 0).addBox(x - 8, y - 16, z - 8, length, height, depth);

        // 等价于 LayerDefinition.create(mesh, textureWidth, textureHeight).bakeRoot()
        final ModelPart root = new ModelPart(textureWidth, textureHeight, 0, 0);
        root.setPos(0, 0, 0);
        root.addChild(cube);
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
