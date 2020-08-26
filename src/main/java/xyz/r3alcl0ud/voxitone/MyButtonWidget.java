package xyz.r3alcl0ud.voxitone;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class MyButtonWidget extends ButtonWidget {
    private int textColor = 0xFFFFFFFF;
    private Text message;
    
    public MyButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, new LiteralText(""), onPress);
        this.message = message;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    
    public void setMessage(Text message) {
        this.message = message;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        super.renderButton(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, mc.textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
    }
}
