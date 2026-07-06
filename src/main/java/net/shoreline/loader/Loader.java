package net.shoreline.loader;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.shoreline.loader.session.UserSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Loader implements
		ClientModInitializer,
		PreLaunchEntrypoint,
		IMixinConfigPlugin
{
	private static final Logger LOGGER = LogManager.getLogger("Shoreline");
	public static final String VERSION = "r1.0.2";
	public static final UserSession SESSION;

	private static boolean clientInitialized = false;

	static
	{
		info("Loading Shoreline...");

		info("Loaded Shoreline's dependant libraries.");

		SESSION = UserSession.load();

		info("Authenticated as " + SESSION.getUsername() + " [" + SESSION.getUserType() + "]");

		info("Shoreline " + VERSION + " is up to date.");
	}

	@Override
	public void onPreLaunch()
	{
	}

	@Override
	public void onInitializeClient()
	{
		if (clientInitialized)
		{
			return;
		}
		clientInitialized = true;
		try
		{
			Class<?> shoreline = Class.forName("net.shoreline.client.Shoreline");
			Method init = shoreline.getDeclaredMethod("init");
			if (!Modifier.isStatic(init.getModifiers()))
			{
				error("Shoreline entrypoint is invalid.");
				return;
			}
			init.setAccessible(true);
			init.invoke(null);
		}
		catch (ClassNotFoundException e)
		{
			error("Shoreline main class missing: " + e.getMessage());
		}
		catch (NoSuchMethodException e)
		{
			error("Shoreline entrypoint missing: " + e.getMessage());
		}
		catch (Throwable t)
		{
			error("Shoreline failed to initialize: " + t);
			Throwable c = t.getCause();
			while (c != null)
			{
				error("  caused by: " + c);
				c = c.getCause();
			}
		}
	}

	@Override
	public void onLoad(String mixinPackage)
	{
	}

	@Override
	public String getRefMapperConfig()
	{
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
	}

	@Override
	public List<String> getMixins()
	{
		return Collections.emptyList();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}

	public static Object performVersionCheck(Object currentVersion)
	{
		return null;
	}

	public static Object showErrorWindow(Object message)
	{
		error(String.valueOf(message));
		return null;
	}

	public static void info(String message)
	{
		LOGGER.info(String.format("[Shoreline] %s", message));
	}

	public static void info(String message, Object... params)
	{
		LOGGER.info(String.format("[Shoreline] %s", message), params);
	}

	public static void error(String message)
	{
		LOGGER.error(String.format("[Shoreline] %s", message));
	}

	public static void error(String message, Object... params)
	{
		LOGGER.error(String.format("[Shoreline] %s", message), params);
	}

	public static InputStream getResource(String name)
	{
		return Loader.class.getClassLoader().getResourceAsStream(name);
	}
}