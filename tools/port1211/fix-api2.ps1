# 1.21.1 移植：第二批 API 修复
$ErrorActionPreference = 'Stop'
$root = 'E:\Project\Tianjin-metro-MTR3\common-1.21.1\src\main\java'
$enc = New-Object System.Text.UTF8Encoding($false)
$changed = New-Object System.Collections.Generic.List[string]

Get-ChildItem $root -Recurse -Filter *.java | ForEach-Object {
    $o = [System.IO.File]::ReadAllText($_.FullName)
    $t = $o

    # 1) EntityModel.renderToBuffer：1.21 用单个 ARGB int 代替 4 个 float
    $t = $t.Replace(
        'renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)',
        'renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)')
    $t = $t.Replace(
        'root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);',
        'root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);')
    $t = $t -replace 'renderToBuffer\(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1\)', 'renderToBuffer(matricesNew, vertexConsumer, light, overlay, 0xFFFFFFFF)'

    # 2) Entity.defineSynchedData 现在带 Builder 参数
    $t = $t.Replace('protected void defineSynchedData() {',
                    'protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {')

    # 3) Screen.renderBackground 现在必须给全 4 个参数
    $t = $t.Replace('renderBackground(guiGraphics);', 'renderBackground(guiGraphics, mouseX, mouseY, delta);')

    # 4) mouseScrolled 增加横向滚动参数
    $t = $t -replace 'public boolean mouseScrolled\(double mouseX, double mouseY, double amount\)',
                     'public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)'
    $t = $t.Replace('return super.mouseScrolled(mouseX, mouseY, amount);', 'return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);')

    # 5) 物品的 appendHoverText：Level -> Item.TooltipContext
    $t = $t -replace 'appendHoverText\(ItemStack (\w+), @Nullable Level (\w+), List<Component>',
                     'appendHoverText(ItemStack $1, @Nullable net.minecraft.world.item.Item.TooltipContext $2, List<Component>'
    $t = $t -replace 'appendHoverText\(ItemStack (\w+), Level (\w+), List<Component>',
                     'appendHoverText(ItemStack $1, net.minecraft.world.item.Item.TooltipContext $2, List<Component>'

    # 6) ImageButton：1.21 用 WidgetSprites 取代纹理坐标
    $t = $t -replace 'new ImageButton\(0, 0, 0, SQUARE_SIZE, 0, 0, 20, ([^,]+), 20, 40,',
                     'new ImageButton(0, 0, 0, SQUARE_SIZE, new net.minecraft.client.gui.components.WidgetSprites($1, $1),'

    # 7) Tesselator：multiBuffer 现在要 ByteBufferBuilder
    $t = $t.Replace('MultiBufferSource.immediate(Tesselator.getInstance().getBuilder())',
                    'MultiBufferSource.immediate(new com.mojang.blaze3d.vertex.ByteBufferBuilder(1536))')

    # 8) ColorPickerScreen：Tesselator.begin + PoseStack + BufferUploader
    $t = $t.Replace(
        'final BufferBuilder buffer = tesselator.getBuilder();',
        "final BufferBuilder buffer = tesselator.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);")
    $t = $t -replace 'IDrawing\.drawRectangle\(buffer, ', 'IDrawing.drawRectangle(guiGraphics.pose(), buffer, '
    $t = $t.Replace('tesselator.end();', 'com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.buildOrThrow());')

    if ($t -ne $o) {
        [System.IO.File]::WriteAllText($_.FullName, $t, $enc)
        $changed.Add($_.Name)
    }
}

Write-Host ("已修改 {0} 个文件：" -f $changed.Count)
$changed | ForEach-Object { Write-Host ("  " + $_) }
