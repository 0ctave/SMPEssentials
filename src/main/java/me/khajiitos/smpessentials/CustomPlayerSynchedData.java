package me.khajiitos.smpessentials;

public interface CustomPlayerSynchedData {

    String getTeamTag();
    boolean isPvpOn();
    boolean isStaff();

    void setTeamTag(String teamTag);
    void setStaff(boolean staff);
    void setPvpOn(boolean pvpOn);
}
