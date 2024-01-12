package me.khajiitos.smpessentials;

import me.khajiitos.smpessentials.commands.PVPCommands;
import me.khajiitos.smpessentials.commands.StaffCommands;
import me.khajiitos.smpessentials.commands.TeamCommands;
import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.data.SMPData;
import me.khajiitos.smpessentials.listener.AntiGriefListeners;
import me.khajiitos.smpessentials.listener.DisplayNameListeners;
import me.khajiitos.smpessentials.listener.EventListeners;
import me.khajiitos.smpessentials.listener.LogListeners;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SMPEssentials.MOD_ID)
public class SMPEssentials {
    public static final String MOD_ID = "smpessentials";

    public static final Logger LOGGER = LogManager.getLogger(SMPEssentials.class);
    public static MinecraftServer server;

    public SMPEssentials() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventListeners());
        MinecraftForge.EVENT_BUS.register(new LogListeners());
        MinecraftForge.EVENT_BUS.register(new AntiGriefListeners());
        MinecraftForge.EVENT_BUS.register(new DisplayNameListeners());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SMPEssentialsClient::init);

        Packets.init();
        me.khajiitos.smpessentials.manager.LogManager.init();
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent e) {
        server = e.getServer();

        Config.load();
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e) {
        PVPCommands.register(e.getDispatcher());
        StaffCommands.register(e.getDispatcher());
        TeamCommands.register(e.getDispatcher());
    }

    public static SMPData getData() {
        return server.overworld().getDataStorage().computeIfAbsent(SMPData::new, "smpdata");
    }
}