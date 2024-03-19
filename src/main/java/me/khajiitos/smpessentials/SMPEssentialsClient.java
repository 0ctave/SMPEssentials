package me.khajiitos.smpessentials;

import me.khajiitos.smpessentials.packet.RequestOpenTeamManagerPacket;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class SMPEssentialsClient {
    public static final KeyBinding OPEN_TEAM_MANAGER = new KeyBinding(
            "key.smpessentials.open_team_manager",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.smpessentials"
    );

    public static void init() {
        MinecraftForge.EVENT_BUS.register(SMPEssentialsClient.class);
        ClientRegistry.registerKeyBinding(OPEN_TEAM_MANAGER);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent e) {
        while (OPEN_TEAM_MANAGER.consumeClick()) {
            Packets.INSTANCE.sendToServer(new RequestOpenTeamManagerPacket());
        }
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent e) {
        if (e.getGui() instanceof MainMenuScreen) {
            e.setGui(new CustomMainMenu());
        }
    }

    private static class CustomMainMenu extends MainMenuScreen {
        public void init() {
            super.init();
            Button.ITooltip button$itooltip = Button.NO_TOOLTIP;
            this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 24, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_213095_1_) -> {
                Screen screen = (Screen)(this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
                this.minecraft.setScreen(screen);
            }, button$itooltip));
        }
    }
}
