package me.khajiitos.smpessentials.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.screen.tab.*;
import me.khajiitos.smpessentials.screen.widget.ButtonScrollPanel;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TeamManagerScreen extends SMPScreen {
    private AbstractTab tab;
    private ITextComponent tooltip;

    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");

    public boolean hasTeam;
    public CompoundNBT data;

    private ButtonScrollPanel buttonScrollPanel;
    private TeamManagerScrollPanel managerScrollPanel;

    public TeamManagerScreen(boolean hasTeam, CompoundNBT data) {
        super(new StringTextComponent("Team Manager"));

        this.hasTeam = hasTeam;
        this.data = data;
    }

    public void refresh() {
        this.children.clear();
        this.init();
    }

    public void openTab(AbstractTab tab) {
        if (this.tab != tab) {
            this.tab = tab;
            this.initButtons();
            this.initManagerPanelWidgets();
        }
    }

    public void setTooltip(ITextComponent tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    protected void init() {
        super.init();
        this.buttonScrollPanel = new ButtonScrollPanel(this.width - 110, 10, 100, this.height - 20, 7);
        this.managerScrollPanel = new TeamManagerScrollPanel(10, 10, Math.max(16, this.width - 155), Math.max(16, this.height - 20), 12);
        if (this.tab == null) {
            this.tab = this.hasTeam ? new YourTeamTab(this, this.managerScrollPanel) : new CreateTeamTab(this, this.managerScrollPanel);
        } else {
            this.tab.panel = this.managerScrollPanel;
        }
        this.initButtons();
        this.initManagerPanelWidgets();
        this.addRenderableWidget(this.buttonScrollPanel);
        this.addRenderableWidget(this.managerScrollPanel);
    }

    public void initManagerPanelWidgets() {
        this.managerScrollPanel.children().clear();
        this.managerScrollPanel.applyScrollLimits();
        this.tab.initManagerPanelWidgets();
    }

    public void applyManagerScrollLimits() {
        this.managerScrollPanel.applyScrollLimits();
    }

    private IFormattableTextComponent buttonName(String name, Class<? extends AbstractTab> correspondingTabClass) {
        StringTextComponent component = new StringTextComponent(name);
        if (this.tab.getClass() == correspondingTabClass) {
            return component.withStyle(TextFormatting.UNDERLINE);
        }
        return component;
    }

    public void initButtons() {
        this.buttonScrollPanel.children().clear();
        if (this.hasTeam) {
            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Team", YourTeamTab.class), (button) -> {
                this.openTab(new YourTeamTab(this, this.managerScrollPanel));
            }));

            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Members", MembersTab.class), (button) -> {
                this.openTab(new MembersTab(this, this.managerScrollPanel));
            }));

            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Scoreboard", ScoreboardTab.class), (button) -> {
                this.openTab(new ScoreboardTab(this, this.managerScrollPanel));
            }));

            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Banner", BannerTab.class), (button) -> {
                this.openTab(new BannerTab(this, this.managerScrollPanel));
            }));

            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Allies", AlliesTab.class), (button) -> {
                this.openTab(new AlliesTab(this, this.managerScrollPanel));
            }));

            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Wars", WarsTab.class), (button) -> {
                this.openTab(new WarsTab(this, this.managerScrollPanel));
            }));
        } else {
            this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Create team", CreateTeamTab.class), (button) -> {
                this.openTab(new CreateTeamTab(this, this.managerScrollPanel));
            }));
        }

        this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Invites", InvitesTab.class), (button) -> {
            this.openTab(new InvitesTab(this, this.managerScrollPanel));
        }));

        this.buttonScrollPanel.children().add(new Button(0, 0, 86, 20, buttonName("Teams", TeamsTab.class), (button) -> {
            this.openTab(new TeamsTab(this, this.managerScrollPanel));
        }));
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.tooltip = null;

        this.renderBackground(poseStack);
        Minecraft.getInstance().getTextureManager().bind(TEAM_MANAGER);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        if (this.tooltip != null) {
            this.renderTooltip(poseStack, this.tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public <T> void onPacket(T packet) {
        if (this.tab != null) {
            this.tab.onPacket(packet);
        }
    }
}
