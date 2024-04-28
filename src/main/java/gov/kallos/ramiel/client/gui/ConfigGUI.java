package gov.kallos.ramiel.client.gui;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.config.DefaultValues;
import gov.kallos.ramiel.client.config.RGBValue;
import gov.kallos.ramiel.client.gui.model.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

/**
 * Configuration GUI
 * Holds all client configurable values.
 */
public class ConfigGUI extends GuiRoot {

    private RGBValue friendlyRgb = new RGBValue(-1, -1, -1);
    private RGBValue neutralRgb = new RGBValue(-1, -1, -1);
    private RGBValue enemyRgb = new RGBValue(-1, -1, -1);
    private int timeToDisappear = DefaultValues.DEFAULT_DISAPPEAR_TIME;

    public ConfigGUI(Screen parentScreen) {
        super(parentScreen, Text.literal(Formatting.GRAY + "Ramiel-Project " + RamielClient.VERSION + "-" + RamielClient.STABILITY));
    }

    @Override
    public GuiElement build() {
        FlexListLayout root = new FlexListLayout(Vec2.Direction.VERTICAL);
        root.add(new FlexListLayout(Vec2.Direction.HORIZONTAL)
                .add(new Label(this.title.getString()).align(Label.Alignment.ALIGN_CENTER).setWeight(new Vec2(1, 0)))
                .add((GuiElement) new Button("Save & Close").onClick(b -> {
                    saveToConfig();
                    this.client.setScreen(null);
                })));

        TableLayout table = new TableLayout();

        //TODO Holy shit i am retarded and there is most definitely a better way to do this
        FlexListLayout friendlyColours = new FlexListLayout(Vec2.Direction.HORIZONTAL);
        friendlyColours.add(new Label("Friendly Colour  "));
        friendlyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                friendlyRgb.r = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "r (0-255)"));
        friendlyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                friendlyRgb.g = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "g (0-255)"));
        friendlyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                friendlyRgb.b = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "b (0-255)"));
        root.add(friendlyColours);
        FlexListLayout neutralColours = new FlexListLayout(Vec2.Direction.HORIZONTAL);
        neutralColours.add(new Label("Neutral Colour "));
        neutralColours.add(new TextField(b -> {
            if(validRgb(b)) {
                neutralRgb.r = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "r (0-255)"));
        neutralColours.add(new TextField(b -> {
            if(validRgb(b)) {
                neutralRgb.g = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "g (0-255)"));
        neutralColours.add(new TextField(b -> {
            if(validRgb(b)) {
                neutralRgb.b = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "b (0-255)"));
        root.add(neutralColours);
        FlexListLayout enemyColours = new FlexListLayout(Vec2.Direction.HORIZONTAL);
        enemyColours.add(new Label("Enemy Colour "));
        enemyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                enemyRgb.r = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "r (0-255)"));
        enemyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                enemyRgb.g = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "g (0-255)"));
        enemyColours.add(new TextField(b -> {
            if(validRgb(b)) {
                enemyRgb.b = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "b (0-255)"));
        root.add(enemyColours);

        GuiElement[] timeToDisappear = new GuiElement[2];
        timeToDisappear[0] = new Label("Time to Disappear (Minutes): ");
        timeToDisappear[1] = new TextField(b -> {
            if(isInteger(b)) {
                this.timeToDisappear = Integer.parseInt(b);
                return true;
            }
            return false;
        }, "", "minutes").setMinSize(new Vec2(150, 20));
        table.addRow(Arrays.asList(timeToDisappear));


        root.add(table);
        return new TableLayout()
                .addRow((List<GuiElement>) Arrays.asList((GuiElement[])new GuiElement[]{new Spacer().setWeight(new Vec2(999, 1))}))
                .addRow((List<GuiElement>)Arrays.asList((GuiElement[])new GuiElement[]{null, root}))
                .addRow((List<GuiElement>)Arrays.asList((GuiElement[])new GuiElement[]{null, null, new Spacer().setWeight(new Vec2(999, 1))}));
    }

    private void saveToConfig() {
        if(friendlyRgb.isEdited()) {
            RamielClient.getInstance().getConfig().setFriendlyRgb(friendlyRgb);
        } else if(neutralRgb.isEdited()) {
            RamielClient.getInstance().getConfig().setNeutralRgb(neutralRgb);
        } else if(enemyRgb.isEdited()) {
            RamielClient.getInstance().getConfig().setEnemyRgb(enemyRgb);
        } else if(timeToDisappear != DefaultValues.DEFAULT_DISAPPEAR_TIME) {
            RamielClient.getInstance().getConfig().setTimeToDisappear(timeToDisappear);
        }

        //Finally, write config.
        RamielClient.getInstance().getConfig().save();
    }

    private boolean validPersonalSpace(String b) {
        try {
            Integer intTranslate = Integer.parseInt(b);
            return !(intTranslate > 10) && !(intTranslate < 0);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String getBooleanString(boolean bool) {
        return bool ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled";
    }

    private String getConnectedString(boolean bool) {
        return bool ? Formatting.GREEN + "Connected" : Formatting.RED + "Disconnected";
    }

    private boolean isInteger(String b) {
        try {
            Integer.parseInt(b);
            return true;
        } catch (NumberFormatException ex) {
            return  false;
        }
    }

    private boolean validRgb(String b) {
        try {
            Integer intTranslate = Integer.parseInt(b);
            return !(intTranslate > 255) && !(intTranslate < 0);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public void init() {
        super.init();
        int left = this.width / 2 - 155;
        int centre = left + 80;
        int right = left + 160;
        int offset = this.height / 6 - 18;

        TextFieldWidget textTestWidget = new TextFieldWidget(textRenderer, left, height / 6, 60, 20, Text.literal("Test Box"));
        addDrawable(textTestWidget);
        offset += 24;
        

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
