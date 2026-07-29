package elgatopro300.bbsstructureform.client.packs;

import mchorse.bbs_mod.resources.ISourcePack;
import mchorse.bbs_mod.resources.Link;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MinecraftSourcePack implements ISourcePack
{
    private final Map<String, Object> links = new HashMap<>();

    public MinecraftSourcePack()
    {
        this.setupPaths();
    }

    private ResourceManager getEffectiveManager(Link link)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getServer() != null && (link.path.startsWith("structure/") || link.path.endsWith(".nbt")))
        {
            return mc.getServer().getResourceManager();
        }

        return mc.getResourceManager();
    }

    public void setupPaths()
    {
        try
        {
            ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
            Map<Identifier, List<Resource>> map = manager.findAllResources("textures", (l) -> l.getNamespace().equals("minecraft") && l.getPath().endsWith(".png"));

            for (Identifier id : map.keySet())
            {
                String[] parts = id.getPath().split("/");
                Map<String, Object> current = this.links;
                for (int i = 0; i < parts.length; i++)
                {
                    String part = parts[i];
                    if (i == parts.length - 1)
                    {
                        current.put(part, part);
                    }
                    else
                    {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> next = (Map<String, Object>) current.computeIfAbsent(part, k -> new HashMap<String, Object>());
                        current = next;
                    }
                }
            }
        }
        catch (Throwable ignored) {}
    }

    @Override
    public String getPrefix()
    {
        return "minecraft";
    }

    @Override
    public boolean hasAsset(Link link)
    {
        if (link == null || link.path == null) return false;

        try
        {
            ResourceManager manager = this.getEffectiveManager(link);
            if (this.checkResource(manager, link))
            {
                return true;
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getResourceManager() != manager && this.checkResource(mc.getResourceManager(), link))
            {
                return true;
            }
        }
        catch (Throwable ignored) {}

        return false;
    }

    private boolean checkResource(ResourceManager manager, Link link)
    {
        if (manager == null) return false;

        String src = (link.source == null || link.source.isEmpty()) ? "minecraft" : link.source;
        Identifier id = Identifier.of(src, link.path);
        if (manager.getResource(id).isPresent()) return true;

        if (!link.path.startsWith("structure/"))
        {
            if (manager.getResource(Identifier.of(src, "structure/" + link.path)).isPresent()) return true;
        }
        if (!link.path.startsWith("structures/"))
        {
            if (manager.getResource(Identifier.of(src, "structures/" + link.path)).isPresent()) return true;
        }

        return false;
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        if (link == null || link.path == null) return null;

        try
        {
            ResourceManager manager = this.getEffectiveManager(link);
            InputStream is = this.openResource(manager, link);
            if (is != null) return is;

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getResourceManager() != manager)
            {
                is = this.openResource(mc.getResourceManager(), link);
                if (is != null) return is;
            }
        }
        catch (Throwable e)
        {
            throw new IOException("Failed to load minecraft asset: " + link, e);
        }

        return null;
    }

    private InputStream openResource(ResourceManager manager, Link link)
    {
        if (manager == null) return null;

        try
        {
            String src = (link.source == null || link.source.isEmpty()) ? "minecraft" : link.source;
            Identifier id = Identifier.of(src, link.path);
            Optional<Resource> res = manager.getResource(id);
            if (res.isPresent()) return res.get().getInputStream();

            if (!link.path.startsWith("structure/"))
            {
                res = manager.getResource(Identifier.of(src, "structure/" + link.path));
                if (res.isPresent()) return res.get().getInputStream();
            }

            if (!link.path.startsWith("structures/"))
            {
                res = manager.getResource(Identifier.of(src, "structures/" + link.path));
                if (res.isPresent()) return res.get().getInputStream();
            }
        }
        catch (Throwable ignored) {}

        return null;
    }

    @Override
    public File getFile(Link link)
    {
        return null;
    }

    @Override
    public Link getLink(File file)
    {
        return null;
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
    }
}
