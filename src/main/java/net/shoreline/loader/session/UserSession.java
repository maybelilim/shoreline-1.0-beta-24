package net.shoreline.loader.session;

import net.fabricmc.loader.api.FabricLoader;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class UserSession
{
    private final String hardwareId;
    private final String username;
    private final String uid;
    private final String usertype;
    private final List<String> runningMods;

    public UserSession(String hardwareId,
                       String username,
                       String uid,
                       String usertype,
                       List<String> runningMods)
    {
        this.hardwareId = hardwareId;
        this.username = username;
        this.uid = uid;
        this.usertype = usertype;
        this.runningMods = runningMods;
    }

    public String getHardwareID()
    {
        return this.hardwareId;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getUID()
    {
        return this.uid;
    }

    public String getUserType()
    {
        return this.usertype;
    }

    public List<String> getRunningMods()
    {
        return this.runningMods;
    }

    public static UserSession load()
    {
        List<String> loadedMods;
        try
        {
            loadedMods = FabricLoader.getInstance().getAllMods()
                    .stream()
                    .map(mod -> mod.getMetadata().getName())
                    .filter(mod -> !mod.contains("Fabric"))
                    .toList();
        }
        catch (Throwable t)
        {
            loadedMods = Collections.emptyList();
        }

        String hwid = computeHardwareId();
        String user = resolveUsername();
        String uid = "1";
        String type = System.getProperty("shoreline.usertype", "premium");

        return new UserSession(hwid, user, uid, type, loadedMods);
    }

    private static String resolveUsername()
    {
        String override = System.getProperty("shoreline.user");
        if (override != null && !override.isEmpty())
        {
            return override;
        }
        try
        {
            String sysUser = System.getProperty("user.name");
            if (sysUser != null && !sysUser.isEmpty())
            {
                return sysUser;
            }
        }
        catch (Throwable ignored)
        {
        }
        return "Player";
    }

    private static String computeHardwareId()
    {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}