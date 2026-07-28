package elgatopro300.bbsstructureform.client.gui.forms.input.list;

import elgatopro300.bbsstructureform.client.gui.forms.StructureLikeManager;

import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Colors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * List component for vanilla Minecraft structure files (.nbt)
 */
public class UIVanillaStructureList extends UIStringList
{
    private final Map<String, StructureInfo> structureInfoMap = new HashMap<>();
    private UIIcon likeButton;
    private Runnable likeToggleCallback;
    private StructureLikeManager likeManager;
    private boolean loaded = false;

    public UIVanillaStructureList(Consumer<List<String>> callback, StructureLikeManager likeManager)
    {
        super(callback);
        this.likeButton = new UIIcon(Icons.LIKE, null);
        this.likeManager = likeManager;
    }

    public void setLikeToggleCallback(Runnable callback)
    {
        this.likeToggleCallback = callback;
    }

    private void ensureLoaded()
    {
        if (!this.loaded)
        {
            this.loadVanillaStructures();
            this.populateList();
            this.loaded = true;
        }
    }

    private void loadVanillaStructures()
    {
        this.structureInfoMap.clear();
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.getServer() != null)
        {
            this.scanInternalResources(mc.getServer().getResourceManager());
        }
        else
        {
            this.scanInternalResources(mc.getResourceManager());
        }
    }

    private void scanInternalResources(ResourceManager manager)
    {
        Map<Identifier, List<Resource>> resources = manager.findAllResources("structure", (id) -> 
            id.getNamespace().equals("minecraft") && id.getPath().endsWith(".nbt"));

        for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet())
        {
            Identifier id = entry.getKey();
            String path = id.getPath();

            String relativePath = path;
            if (relativePath.startsWith("structure/"))
            {
                relativePath = relativePath.substring("structure/".length());
            }

            String fullPath = "minecraft:" + relativePath;
            String name = relativePath.replace(".nbt", "");

            StructureInfo info = new StructureInfo();
            info.path = fullPath;
            info.name = name;
            info.source = "Minecraft";

            try
            {
                if (!entry.getValue().isEmpty())
                {
                    try (InputStream is = entry.getValue().get(0).getInputStream();
                         DataInputStream dis = new DataInputStream(is))
                    {
                        NbtCompound nbt = NbtIo.readCompressed(dis, NbtSizeTracker.ofUnlimitedBytes());

                        if (nbt.contains("size"))
                        {
                            NbtList sizeList = nbt.getList("size", NbtElement.INT_TYPE);
                            info.sizeX = sizeList.getInt(0);
                            info.sizeY = sizeList.getInt(1);
                            info.sizeZ = sizeList.getInt(2);
                        }

                        if (nbt.contains("blocks"))
                        {
                            info.blockCount = nbt.getList("blocks", NbtElement.COMPOUND_TYPE).size();
                        }
                    }
                }

                this.structureInfoMap.put(fullPath, info);
            }
            catch (Exception e)
            {
                /* Skip invalid structures */
            }
        }
    }

    private void populateList()
    {
        this.list.clear();

        for (String key : this.structureInfoMap.keySet())
        {
            StructureInfo info = this.structureInfoMap.get(key);
            String prefix = "[" + info.source + "]: ";
            String displayName = prefix + info.name;

            this.list.add(displayName);
        }

        this.list.sort(String::compareToIgnoreCase);
        this.update();
    }

    private String removePrefix(String displayName)
    {
        int endBracket = displayName.indexOf(']');

        if (endBracket > 0 && displayName.startsWith("["))
        {
            int colonSpace = displayName.indexOf("]: ", endBracket);

            if (colonSpace > 0)
            {
                return displayName.substring(colonSpace + 3);
            }
        }

        return displayName;
    }

    private String getStructurePath(String displayName)
    {
        String name = this.removePrefix(displayName);

        for (String key : this.structureInfoMap.keySet())
        {
            StructureInfo info = this.structureInfoMap.get(key);

            if (info.name.equals(name))
            {
                return info.path;
            }
        }

        return null;
    }

    @Override
    protected void renderElementPart(UIContext context, String element, int i, int x, int y, boolean hover, boolean selected)
    {
        int textWidth = context.batcher.getFont().getWidth(element);
        int buttonSpace = 20;
        int maxWidth = this.area.w - 8 - buttonSpace;

        String displayText = element;

        if (textWidth > maxWidth)
        {
            displayText = context.batcher.getFont().limitToWidth(element, maxWidth);
        }

        context.batcher.textShadow(displayText, x + 4, y + (this.scroll.scrollItemSize - context.batcher.getFont().getHeight()) / 2, hover ? Colors.HIGHLIGHT : Colors.WHITE);

        int currentIconX = this.area.x + this.area.w - 20;
        int iconY = y + (this.scroll.scrollItemSize - 16) / 2;

        String structurePath = this.getStructurePath(element);
        boolean isLiked = structurePath != null && this.likeManager.isStructureLiked(structurePath);
        boolean isHoverOnLike = this.area.isInside(context)
            && context.mouseX >= currentIconX
            && context.mouseX < currentIconX + 16
            && context.mouseY >= iconY
            && context.mouseY < iconY + 16;

        this.likeButton.both(isLiked ? Icons.DISLIKE : Icons.LIKE);
        this.likeButton.iconColor(isHoverOnLike || isLiked ? Colors.WHITE : Colors.GRAY);
        this.likeButton.area.set(currentIconX, iconY, 16, 16);
        this.likeButton.render(context);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            int scrollIndex = this.scroll.getIndex(context.mouseX, context.mouseY);
            String element = this.getElementAt(scrollIndex);

            if (element != null)
            {
                int y = this.area.y + scrollIndex * this.scroll.scrollItemSize - (int) this.scroll.getScroll();
                int iconY = y + (this.scroll.scrollItemSize - 16) / 2;
                int likeIconX = this.area.x + this.area.w - 20;

                if (
                    context.mouseX >= likeIconX &&
                    context.mouseX < likeIconX + 16 &&
                    context.mouseY >= iconY &&
                    context.mouseY < iconY + 16
                ) {
                    String structurePath = this.getStructurePath(element);

                    if (structurePath != null)
                    {
                        this.likeManager.toggleStructureLiked(structurePath);

                        if (this.likeToggleCallback != null)
                        {
                            this.likeToggleCallback.run();
                        }
                    }

                    return true;
                }
            }
        }

        boolean result = super.subMouseClicked(context);

        if (result && this.callback != null)
        {
            List<String> current = this.getCurrent();

            if (!current.isEmpty())
            {
                List<String> paths = new ArrayList<>();

                for (String displayName : current)
                {
                    String path = this.getStructurePath(displayName);

                    if (path != null)
                    {
                        paths.add(path);
                    }
                }

                if (!paths.isEmpty())
                {
                    this.callback.accept(paths);
                }

                return true;
            }
        }

        return result;
    }

    @Override
    public void render(UIContext context)
    {
        this.ensureLoaded();

        super.render(context);

        if (this.getIndex() > 0 && this.getIndex() < this.list.size())
        {
            String selected = this.list.get(this.getIndex());
            String path = this.getStructurePath(selected);
            StructureInfo info = path != null ? this.structureInfoMap.get(path) : null;

            if (info != null)
            {
                int y = this.area.ey() + 5;

                context.batcher.box(this.area.x, y, this.area.ex(), y + 60, Colors.A50);

                y += 5;
                context.batcher.textCard("Name: " + info.name, this.area.x + 5, y);
                y += 12;
                context.batcher.textCard("Source: " + info.source, this.area.x + 5, y);
                y += 12;
                context.batcher.textCard("Blocks: " + info.blockCount, this.area.x + 5, y);
                y += 12;

                if (info.sizeX > 0)
                {
                    context.batcher.textCard(
                        "Size: " + info.sizeX + "x" + info.sizeY + "x" + info.sizeZ, 
                        this.area.x + 5, y);
                }
            }
        }
    }

    public void refresh()
    {
        this.loaded = false;
        this.ensureLoaded();
    }

    private static class StructureInfo
    {
        public String path;
        public String name;
        public String source;
        public int blockCount;
        public int sizeX;
        public int sizeY;
        public int sizeZ;
    }
}
