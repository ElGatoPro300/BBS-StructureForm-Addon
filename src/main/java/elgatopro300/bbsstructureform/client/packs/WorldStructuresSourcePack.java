package elgatopro300.bbsstructureform.client.packs;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.resources.ISourcePack;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.resources.packs.ExternalAssetsSourcePack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * WorldStructuresSourcePack
 *
 * Provides access to structure files saved in the current world's folder
 * (e.g., `<world>/generated/minecraft/structures`). It hooks into the
 * existing `assets:` namespace so UI code that queries
 * `assets:structures/...` can also discover entries coming from the world.
 */
public class WorldStructuresSourcePack implements ISourcePack
{
    private static final String PREFIX = "world";

    @Override
    public String getPrefix()
    {
        return PREFIX;
    }

    @Override
    public boolean hasAsset(Link link)
    {
        File file = resolve(link);

        return file != null && file.exists();
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        File file = resolve(link);

        if (file == null || !file.exists())
        {
            throw new IOException("World structure asset not found: " + link);
        }

        return new FileInputStream(file);
    }

    @Override
    public File getFile(Link link)
    {
        File file = resolve(link);

        return file != null && file.exists() ? file : null;
    }

    @Override
    public Link getLink(File file)
    {
        File generated = getGeneratedFolder();

        if (generated == null)
        {
            return null;
        }

        String base1 = new File(generated, "minecraft/structures").getAbsolutePath();
        String base2 = new File(generated, "structures").getAbsolutePath();

        String path = file.getAbsolutePath();

        if (path.startsWith(base1))
        {
            String rel = path.substring(base1.length());
            if (!rel.isEmpty() && (rel.charAt(0) == '/' || rel.charAt(0) == '\\'))
            {
                rel = rel.substring(1);
            }

            return new Link(PREFIX, rel.replace('\\', '/'));
        }
        else if (path.startsWith(base2))
        {
            String rel = path.substring(base2.length());
            if (!rel.isEmpty() && (rel.charAt(0) == '/' || rel.charAt(0) == '\\'))
            {
                rel = rel.substring(1);
            }

            return new Link(PREFIX, rel.replace('\\', '/'));
        }

        return null;
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
        File generated = getGeneratedFolder();

        if (generated == null)
        {
            return;
        }

        if (!PREFIX.equals(link.source))
        {
            return;
        }

        File base1 = new File(generated, "minecraft/structures");
        File base2 = new File(generated, "structures");

        File dir1 = link.path.isEmpty() ? base1 : new File(base1, link.path);
        File dir2 = link.path.isEmpty() ? base2 : new File(base2, link.path);

        if (dir1.isDirectory())
        {
            ExternalAssetsSourcePack.getLinksFromPathRecursively(dir1, links, link, link.path, recursive ? 9999 : 1);
        }

        if (dir2.isDirectory())
        {
            ExternalAssetsSourcePack.getLinksFromPathRecursively(dir2, links, link, link.path, recursive ? 9999 : 1);
        }
    }

    private File resolve(Link link)
    {
        if (!PREFIX.equals(link.source))
        {
            return null;
        }

        File generated = getGeneratedFolder();

        if (generated == null)
        {
            return null;
        }

        String rel = link.path;
        if (rel.startsWith("/"))
        {
            rel = rel.substring(1);
        }

        File candidate1 = new File(generated, "minecraft/structures/" + rel);
        File candidate2 = new File(generated, "structures/" + rel);

        if (candidate1.exists())
        {
            return candidate1;
        }
        if (candidate2.exists())
        {
            return candidate2;
        }

        return null;
    }

    private static File getGeneratedFolder()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getServer() == null)
        {
            return null;
        }
        return mc.getServer().getSavePath(WorldSavePath.GENERATED).toFile();
    }
}
