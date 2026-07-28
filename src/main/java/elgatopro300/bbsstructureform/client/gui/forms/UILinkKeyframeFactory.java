package elgatopro300.bbsstructureform.client.gui.forms;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIKeyframeFactory;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs_mod.utils.keyframes.Keyframe;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
                List<String> items = new ArrayList<>();
                try {
                    for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("bbs-structureform", "structures"))) {
                        if (l.path.toLowerCase().endsWith(".nbt")) items.add("bbs-structureform:" + l.path);
                    }
                    for (Link l : BBSMod.getProvider().getLinksFromPath(new Link("world", ""))) {
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

    private static Set<String> getSavedStructureFiles()
    {
        Set<String> locations = new HashSet<>();
        File savedFolder = new File(BBSMod.getAssetsFolder(), "structures");
        if (savedFolder.exists() && savedFolder.isDirectory())
        {
            try (Stream<Path> paths = Files.walk(savedFolder.toPath()))
            {
                paths.filter(Files::isRegularFile)
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
