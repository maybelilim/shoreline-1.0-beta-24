package net.shoreline.client.init;

import net.shoreline.client.Shoreline;
import net.shoreline.client.api.render.layers.LightmapManager;
import net.shoreline.client.api.render.shader.ShaderManager;
import net.shoreline.client.impl.manager.EventManager;
import net.shoreline.client.impl.manager.ModuleManager;
import net.shoreline.client.impl.manager.anticheat.AntiCheatManager;
import net.shoreline.client.impl.manager.client.AccountManager;
import net.shoreline.client.impl.manager.client.CommandManager;
import net.shoreline.client.impl.manager.client.MacroManager;
import net.shoreline.client.impl.manager.client.SocialManager;
import net.shoreline.client.impl.manager.client.cape.CapeManager;
import net.shoreline.client.impl.manager.combat.HitboxManager;
import net.shoreline.client.impl.manager.combat.PearlManager;
import net.shoreline.client.impl.manager.combat.TotemManager;
import net.shoreline.client.impl.manager.combat.hole.HoleManager;
import net.shoreline.client.impl.manager.mojang.LookupManager;
import net.shoreline.client.impl.manager.network.NetworkManager;
import net.shoreline.client.impl.manager.player.InventoryManager;
import net.shoreline.client.impl.manager.player.MovementManager;
import net.shoreline.client.impl.manager.player.PositionManager;
import net.shoreline.client.impl.manager.player.interaction.InteractionManager;
import net.shoreline.client.impl.manager.player.rotation.RotationManager;
import net.shoreline.client.impl.manager.world.BlockManager;
import net.shoreline.client.impl.manager.world.WaypointManager;
import net.shoreline.client.impl.manager.world.sound.SoundManager;
import net.shoreline.client.impl.manager.world.tick.TickManager;

import java.util.function.Supplier;

public class Managers
{
    public static NetworkManager NETWORK;
    public static MacroManager MACRO;
    public static ModuleManager MODULE;
    public static EventManager EVENT;
    public static CommandManager COMMAND;
    public static SocialManager SOCIAL;
    public static WaypointManager WAYPOINT;
    public static AccountManager ACCOUNT;
    public static TickManager TICK;
    public static InventoryManager INVENTORY;
    public static PositionManager POSITION;
    public static RotationManager ROTATION;
    public static AntiCheatManager ANTICHEAT;
    public static MovementManager MOVEMENT;
    public static HoleManager HOLE;
    public static TotemManager TOTEM;
    public static InteractionManager INTERACT;
    public static SoundManager SOUND;
    public static CapeManager CAPES;
    public static ShaderManager SHADER;
    public static LookupManager LOOKUP;
    public static LightmapManager LIGHT_MAP;
    public static BlockManager BLOCK;
    public static HitboxManager HITBOX;
    public static PearlManager PEARL;

    private static boolean initialized;

    public static void init()
    {
        if (isInitialized())
        {
            return;
        }

        NETWORK = tryInit("NetworkManager", NetworkManager::new);
        MACRO = tryInit("MacroManager", MacroManager::new);
        MODULE = tryInit("ModuleManager", ModuleManager::new);
        EVENT = tryInit("EventManager", EventManager::new);
        SOCIAL = tryInit("SocialManager", SocialManager::new);
        WAYPOINT = tryInit("WaypointManager", WaypointManager::new);
        ACCOUNT = tryInit("AccountManager", AccountManager::new);
        TICK = tryInit("TickManager", TickManager::new);
        INVENTORY = tryInit("InventoryManager", InventoryManager::new);
        POSITION = tryInit("PositionManager", PositionManager::new);
        ROTATION = tryInit("RotationManager", RotationManager::new);
        BLOCK = tryInit("BlockManager", BlockManager::new);
        HITBOX = tryInit("HitboxManager", HitboxManager::new);
        PEARL = tryInit("PearlManager", PearlManager::new);
        ANTICHEAT = tryInit("AntiCheatManager", AntiCheatManager::new);
        MOVEMENT = tryInit("MovementManager", MovementManager::new);
        HOLE = tryInit("HoleManager", HoleManager::new);
        TOTEM = tryInit("TotemManager", TotemManager::new);
        INTERACT = tryInit("InteractionManager", InteractionManager::new);
        COMMAND = tryInit("CommandManager", CommandManager::new);
        SOUND = tryInit("SoundManager", SoundManager::new);
        SHADER = tryInit("ShaderManager", ShaderManager::new);
        LOOKUP = tryInit("LookupManager", LookupManager::new);

        initialized = true;
    }

    public static void postInit()
    {
        if (!isInitialized())
        {
            return;
        }

        tryPostInit("MacroManager", MACRO, MacroManager::postInit);
        tryPostInit("AccountManager", ACCOUNT, AccountManager::postInit);

        CAPES = tryInit("CapeManager", CapeManager::new);
        LIGHT_MAP = tryInit("LightmapManager", LightmapManager::new);
    }

    public static boolean isInitialized()
    {
        return initialized;
    }

    private static <T> T tryInit(String name, Supplier<T> ctor)
    {
        try
        {
            T instance = ctor.get();
            Shoreline.info("Initialized " + name);
            return instance;
        }
        catch (Throwable t)
        {
            logFailure(name + " init", t);
            return null;
        }
    }

    private static <T> void tryPostInit(String name, T instance, java.util.function.Consumer<T> action)
    {
        if (instance == null)
        {
            return;
        }
        try
        {
            action.accept(instance);
        }
        catch (Throwable t)
        {
            logFailure(name + " postInit", t);
        }
    }

    private static void logFailure(String what, Throwable t)
    {
        Shoreline.error("Failed to run " + what + ": " + t);
        Throwable c = t.getCause();
        while (c != null)
        {
            Shoreline.error("  caused by: " + c);
            c = c.getCause();
        }
    }
}