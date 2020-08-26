package xyz.r3alcl0ud.voxitone;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class VoxitoneConfigScreen extends Screen {

    private Screen parent;

    private Text closeOnPath;
    
    private ButtonWidget toggleCloseOnPath;
    private ButtonWidget close;
    private ButtonWidget closeAndSave;

    public VoxitoneConfigScreen(Screen parent) {
        this(new LiteralText("Voxitone Config"));
        this.parent = parent;
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
        closeOnPath = new LiteralText(Voxitone.config.closeOnPath ? "True" : "False");
        toggleCloseOnPath = new ButtonWidget(0, 20, 100, 20, new LiteralText("Close map on path"), (b) -> {
            Voxitone.config.closeOnPath = !Voxitone.config.closeOnPath;
            closeOnPath = new LiteralText(Voxitone.config.closeOnPath ? "True" : "False");
        });
        close = new ButtonWidget(0,this.height - 30,100,20, new LiteralText("Close"), (b) -> {
           onClose(); 
        });
        closeAndSave = new ButtonWidget(0,this.height - 30,100,20, new LiteralText("Close & Save"), (b) -> {
            Voxitone.saveConfig();
            onClose(); 
         });
        this.addButton(toggleCloseOnPath);
        this.addButton(close);
        this.addButton(closeAndSave);
        
    }

    public void render(MatrixStack matrices, int mX, int mY, float d) {
        this.renderBackground(matrices, 0);
        fill(matrices, 20, 45, width-20, height - 50, 0x99000000);
        super.render(matrices, mX, mY, d);
        int help = 60;
        toggleCloseOnPath.y = help;
        toggleCloseOnPath.x = 30;
        close.x = (int)(width / 2) - 105;
        close.y = this.height - 30;
        closeAndSave.x = (int)(width / 2) + 5;
        closeAndSave.y = this.height - 30;
        drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);
//        drawTextWithShadow(matrices, textRenderer, title, width / 2 - 20, 10, 0xFFFFFF);
        drawTextWithShadow(matrices, textRenderer, closeOnPath, this.width - 50, help,
            Voxitone.config.closeOnPath ? 0x00FF00 : 0xFF0000);
    }
}
