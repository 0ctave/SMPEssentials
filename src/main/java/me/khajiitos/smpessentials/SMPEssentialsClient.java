package me.khajiitos.smpessentials;

import me.khajiitos.smpessentials.packet.RequestOpenTeamManagerPacket;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
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
}
