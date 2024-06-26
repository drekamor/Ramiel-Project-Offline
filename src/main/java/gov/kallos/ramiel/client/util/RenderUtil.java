package gov.kallos.ramiel.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderUtil {

    public static void drawWaypoint(MatrixStack m, TextRenderer text, String name, int colorhex, float x, float y, float z, float size, Quaternionf rotate) {
        int i = text.getWidth(name) / 2 + 2;
        m.push();
        m.translate((double)x, (double)y, (double)z);
        m.multiply(rotate);
        m.scale(-size, -size, size);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        Matrix4f mat = m.peek().getPositionMatrix();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();

        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bb.vertex(mat, (float)(-i), -3.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)(-i), 10.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)i, 10.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        bb.vertex(mat, (float)i, -3.0F, 0.0F).color(0.0F, 0.0F, 0.0F, 0.6F).next();
        tes.draw();
        bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bb.vertex(mat, 0.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, -4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 0.0F, 20.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, 0.0F, 12.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        bb.vertex(mat, -4.0F, 16.0F, 0.0F).color(colorhex >> 16 & 255, colorhex >> 8 & 255, colorhex & 255, 255).next();
        tes.draw();
        RenderSystem.enableBlend();
        VertexConsumerProvider.Immediate wvc = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        text.draw(Text.literal(name), (float)(-i), 0.0F, colorhex, false, mat, wvc, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880);
        wvc.draw();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        m.pop();
        RenderSystem.enableDepthTest();
    }
}
