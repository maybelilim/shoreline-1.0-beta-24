package net.shoreline.loader.mixin;

import net.fabricmc.loader.impl.launch.knot.MixinServiceKnot;

import java.io.IOException;
import java.io.InputStream;

public final class MixinServiceExt extends MixinServiceKnot
{
    @Override
    public byte[] getClassBytes(String name, boolean runTransformers) throws ClassNotFoundException, IOException
    {
        return super.getClassBytes(name, runTransformers);
    }

    @Override
    public InputStream getResourceAsStream(String name)
    {
        return super.getResourceAsStream(name);
    }
}