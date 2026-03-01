package elgatopro300.bbsstructureform.client.gui.forms;

import mchorse.bbs_mod.forms.forms.StructureForm;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.forms.editors.forms.UIForm;
import mchorse.bbs_mod.ui.utils.icons.Icons;

public class UIStructureForm extends UIForm<StructureForm>
{
    public UIStructureForm()
    {
        super();

        this.defaultPanel = new UIStructureFormPanel(this);

        /* Usar el icono de árbol para estructuras */
        this.registerPanel(this.defaultPanel, IKey.raw("Structure"), Icons.TREE);
        this.registerDefaultPanels();
    }
}
