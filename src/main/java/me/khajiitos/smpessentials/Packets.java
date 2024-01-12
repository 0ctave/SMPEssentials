package me.khajiitos.smpessentials;

import me.khajiitos.smpessentials.packet.*;
import me.khajiitos.smpessentials.packet.teammanager.c2s.*;
import me.khajiitos.smpessentials.packet.teammanager.s2c.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class Packets {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetsRegistered;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SMPEssentials.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(packetsRegistered++, RulesPacket.class, RulesPacket::encode, RulesPacket::decode, RulesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, RulesResultPacket.class, RulesResultPacket::encode, RulesResultPacket::decode, RulesResultPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, CloseRulesPacket.class, CloseRulesPacket::encode, CloseRulesPacket::decode, CloseRulesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, RequestOpenTeamManagerPacket.class, RequestOpenTeamManagerPacket::encode, RequestOpenTeamManagerPacket::decode, RequestOpenTeamManagerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, OpenTeamManagerPacket.class, OpenTeamManagerPacket::encode, OpenTeamManagerPacket::decode, OpenTeamManagerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(packetsRegistered++, CreateTeamPacket.class, CreateTeamPacket::encode, CreateTeamPacket::decode, CreateTeamPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, FetchInvitesPacket.class, FetchInvitesPacket::encode, FetchInvitesPacket::decode, FetchInvitesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, UpdateBannerPacket.class, UpdateBannerPacket::encode, UpdateBannerPacket::decode, UpdateBannerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, InvitePacket.class, InvitePacket::encode, InvitePacket::decode, InvitePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, FetchAllTeamsPacket.class, FetchAllTeamsPacket::encode, FetchAllTeamsPacket::decode, FetchAllTeamsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RequestOpenTeamInfoPacket.class, RequestOpenTeamInfoPacket::encode, RequestOpenTeamInfoPacket::decode, RequestOpenTeamInfoPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, LeaveTeamPacket.class, LeaveTeamPacket::encode, LeaveTeamPacket::decode, LeaveTeamPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, AcceptInvitePacket.class, AcceptInvitePacket::encode, AcceptInvitePacket::decode, AcceptInvitePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, DenyInvitePacket.class, DenyInvitePacket::encode, DenyInvitePacket::decode, DenyInvitePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, UpdateFriendlyFirePacket.class, UpdateFriendlyFirePacket::encode, UpdateFriendlyFirePacket::decode, UpdateFriendlyFirePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RemoveAllyPacket.class, RemoveAllyPacket::encode, RemoveAllyPacket::decode, RemoveAllyPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, KickMemberPacket.class, KickMemberPacket::encode, KickMemberPacket::decode, KickMemberPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RequestAlliesPacket.class, RequestAlliesPacket::encode, RequestAlliesPacket::decode, RequestAlliesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RequestWarsPacket.class, RequestWarsPacket::encode, RequestWarsPacket::decode, RequestWarsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RequestEndWarPacket.class, RequestEndWarPacket::encode, RequestEndWarPacket::decode, RequestEndWarPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, AllyInviteResponsePacket.class, AllyInviteResponsePacket::encode, AllyInviteResponsePacket::decode, AllyInviteResponsePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, WarInviteResponsePacket.class, WarInviteResponsePacket::encode, WarInviteResponsePacket::decode, WarInviteResponsePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, InviteToAlliesPacket.class, InviteToAlliesPacket::encode, InviteToAlliesPacket::decode, InviteToAlliesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, InviteToWarPacket.class, InviteToWarPacket::encode, InviteToWarPacket::decode, InviteToWarPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, UpdateMemberRolePacket.class, UpdateMemberRolePacket::encode, UpdateMemberRolePacket::decode, UpdateMemberRolePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, RequestJoinTeamPacket.class, RequestJoinTeamPacket::encode, RequestJoinTeamPacket::decode, RequestJoinTeamPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, AcceptJoinRequestPacket.class, AcceptJoinRequestPacket::encode, AcceptJoinRequestPacket::decode, AcceptJoinRequestPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(packetsRegistered++, DenyJoinRequestPacket.class, DenyJoinRequestPacket::encode, DenyJoinRequestPacket::decode, DenyJoinRequestPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(packetsRegistered++, InvitesPacket.class, InvitesPacket::encode, InvitesPacket::decode, InvitesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, ErrorPacket.class, ErrorPacket::encode, ErrorPacket::decode, ErrorPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, PlayerInvitedPacket.class, PlayerInvitedPacket::encode, PlayerInvitedPacket::decode, PlayerInvitedPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, AllTeamsPacket.class, AllTeamsPacket::encode, AllTeamsPacket::decode, AllTeamsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, OpenTeamInfoPacket.class, OpenTeamInfoPacket::encode, OpenTeamInfoPacket::decode, OpenTeamInfoPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, AlliesPacket.class, AlliesPacket::encode, AlliesPacket::decode, AlliesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, WarsPacket.class, WarsPacket::encode, WarsPacket::decode, WarsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, RefreshCurrentTabPacket.class, RefreshCurrentTabPacket::encode, RefreshCurrentTabPacket::decode, RefreshCurrentTabPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(packetsRegistered++, UpdateTeamManagerPacket.class, UpdateTeamManagerPacket::encode, UpdateTeamManagerPacket::decode, UpdateTeamManagerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <MSG> void sendToPlayer(ServerPlayerEntity player, MSG packet) {
        Packets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static <MSG> void sendToServer(MSG packet) {
        Packets.INSTANCE.sendToServer(packet);
    }
}
