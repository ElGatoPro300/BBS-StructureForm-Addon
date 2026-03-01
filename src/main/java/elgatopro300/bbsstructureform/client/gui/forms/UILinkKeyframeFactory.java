package elgatopro300.bbsstructureform.client.gui.forms;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

public class UILinkKeyframeFactory extends UIKeyframeFactory<Link>
{
    public UILinkKeyframeFactory(Keyframe<Link> keyframe, UIKeyframes editor)
    {
        super(keyframe, editor);

        UIKeyframeSheet sheet = editor.getGraph().getSheet(keyframe);
        boolean isStructureFile = sheet != null && ("structure_file".equals(sheet.id) || sheet.id.endsWith("/structure_file"));

        if (isStructureFile)
        {
            UIButton pickStructure = new UIButton(IKey.raw("Pick Structure"), (b) ->
            {
                java.util.List<String> items = new java.util.ArrayList<>();
                try {
                    for (Link l : mchorse.bbs_mod.BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
                        if (l.path.toLowerCase().endsWith(".nbt")) items.add("bbs-structureform:" + l.path);
                    }
                    for (Link l : mchorse.bbs_mod.BBSMod.getProvider().getLinksFromPath(new Link("world", ""))) {
                        if (l.path.toLowerCase().endsWith(".nbt")) items.add("world:" + l.path);
                    }
                } catch (Throwable ignored) {}
                items.sort(null);
                UIStringOverlayPanel panel = new UIStringOverlayPanel(IKey.raw("Pick Structure"), items, (value) -> {
                    this.editor.getGraph().setValue(value == null || value.isEmpty() ? null : Link.create(value), true);
                });
                Link current = this.keyframe.getValue();
                panel.set(current == null ? "" : current.toString());
                UIOverlay.addOverlay(this.getContext(), panel, 280, 0.5F);
            });

            this.scroll.add(pickStructure);
        }
        else
        {
            this.scroll.add(new UIButton(IKey.raw("Pick Texture"), (b) ->
            {
                UITexturePicker.open(this.getContext(), this.keyframe.getValue(), (l) ->
                {
                    this.editor.getGraph().setValue(l, true);
                });
            }));
        }
    }

    private static java.util.Set<String> getSavedStructureFiles()
    {
        java.util.Set<String> locations = new java.util.HashSet<>();
        java.io.File savedFolder = new java.io.File(mchorse.bbs_mod.BBSMod.getAssetsFolder(), "structures");
        if (savedFolder.exists() && savedFolder.isDirectory())
        {
            try (java.util.stream.Stream<java.nio.file.Path> paths = java.nio.file.Files.walk(savedFolder.toPath()))
            {
                paths.filter(java.nio.file.Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".nbt"))
                    .forEach(path -> {
                        try
                        {
                            String relativePath = savedFolder.toPath().relativize(path).toString().replace("\\", "/");
                            locations.add(relativePath);
                        }
                        catch (Exception ignored) {}
                    });
            }
            catch (Exception e)
            {
                System.err.println("Failed to scan folder: " + savedFolder + " - " + e.getMessage());
            }
        }
        return locations;
    }
}
