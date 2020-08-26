package xyz.r3alcl0ud.voxitone;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class VoxitoneConfigScreen extends Screen {

    private Screen parent;
    private Text closeOnPath;
    private static VoxitoneConfig defaultConfig = new VoxitoneConfig();

    private MyButtonWidget toggleCloseOnPath;
    private ButtonWidget toggleCloseOnPathReset;
    private ButtonWidget close;
    private ButtonWidget closeAndSave;

    private boolean originalState;

    private float fillWidth = 400;
    // these will be reassigned by init
    private int center = 0;
    private int left;
    private int right;
    
    public VoxitoneConfigScreen(Screen parent) {
        this(new LiteralText("Voxitone Config"));
        this.parent = parent;
        originalState = Voxitone.config.closeOnPath;
    }

    protected VoxitoneConfigScreen(Text title) {
        super(title);
        // TODO Auto-generated constructor stub
    }

    public void onClose() {
        client.openScreen(parent);
    }

    public void init() {
        super.init();

        left = (int) ((width / 2) - (fillWidth / 2));
        right = (int) ((width / 2) + (fillWidth / 2));
        center = width / 2;
        
        closeOnPath = new LiteralText("Close map on path: ");
        
        
        // options
        
        // TOGGLE CLOSE ON PATH
        toggleCloseOnPath = this.addButton(new MyButtonWidget(center + 5, 60, 100, 20, new LiteralText(Voxitone.config.closeOnPath ? "True" : "False"), (b) -> {
            Voxitone.config.closeOnPath = !Voxitone.config.closeOnPath;
            toggleCloseOnPathReset.active = Voxitone.config.closeOnPath != defaultConfig.closeOnPath;
            b.setMessage(new LiteralText(Voxitone.config.closeOnPath ? "True" : "False"));
            ((MyButtonWidget)b).setTextColor(Voxitone.config.closeOnPath ? 0xFF00FF00 : 0xFFFF0000);
        }));
        toggleCloseOnPath.setTextColor(Voxitone.config.closeOnPath ? 0xFF00FF00 : 0xFFFF0000);
        
        toggleCloseOnPathReset = this.addButton(new ButtonWidget(center + 110, 60, 50, 20, new LiteralText("Reset"), (b) -> {
            Voxitone.config.closeOnPath = defaultConfig.closeOnPath;
            toggleCloseOnPath.setMessage(new LiteralText(Voxitone.config.closeOnPath ? "True" : "False"));
            toggleCloseOnPath.setTextColor(Voxitone.config.closeOnPath ? 0xFF00FF00 : 0xFFFF0000);
            b.active = false;
        }));
        toggleCloseOnPathReset.active = Voxitone.config.closeOnPath != defaultConfig.closeOnPath;
        
        
        
        
        
        // close
        close = this.addButton(new ButtonWidget((int) (width / 2) - 105, this.height - 30, 100, 20, new LiteralText("Close"), (b) -> {
            Voxitone.config.closeOnPath = originalState;
            toggleCloseOnPath.setMessage(new LiteralText(Voxitone.config.closeOnPath ? "True" : "False"));
            onClose();
        }));
        closeAndSave = this.addButton(new ButtonWidget((int) (width / 2) + 5, this.height - 30, 100, 20, new LiteralText("Close & Save"), (b) -> {
            Voxitone.saveConfig();
            onClose();
        }));
    }

    public void render(MatrixStack matrices, int mX, int mY, float d) {
        this.renderBackgroundTexture(0);
        fill(matrices, left, 45, right, height - 50,
            0x99000000);
        drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);
        // drawTextWithShadow(matrices, textRenderer, title, width / 2 - 20, 10,
        // 0xFFFFFF);
        
        if (mY >= 58 && mY <= 82) {
            fill(matrices, left, 58, right, 82, 0x2FFFFFFF);
        }
        drawCenteredText(matrices, textRenderer, closeOnPath, center - 55, 66, 0xFFFFFF);
        super.render(matrices, mX, mY, d);
        
    }
}
