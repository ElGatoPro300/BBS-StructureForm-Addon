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
        try
        {
            Identifier id = Identifier.of(link.source, link.path);
            ResourceManager effectiveManager = this.getEffectiveManager(link);

            if (effectiveManager.getResource(id).isPresent())
            {
                return true;
            }

            if (!link.path.startsWith("structure/") && link.path.endsWith(".nbt"))
            {
                Identifier structureId = Identifier.of(link.source, "structures/" + link.path);
                if (effectiveManager.getResource(structureId).isPresent())
                {
                    return true;
                }
            }
        }
        catch (Throwable ignored) {}

        return false;
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        try
        {
            Identifier id = Identifier.of(link.source, link.path);
            ResourceManager effectiveManager = this.getEffectiveManager(link);

            Optional<Resource> resource = effectiveManager.getResource(id);

            if (resource.isEmpty() && !link.path.startsWith("structure/") && link.path.endsWith(".nbt"))
            {
                Identifier structureId = Identifier.of(link.source, "structure/" + link.path);
                resource = effectiveManager.getResource(structureId);
            }

            if (resource.isPresent())
            {
                return resource.get().getInputStream();
            }
        }
        catch (Throwable e)
        {
            throw new IOException("Failed to load minecraft asset: " + link, e);
        }

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
