package elgatopro300.bbsstructureform.client.gui.forms;

import elgatopro300.bbsstructureform.form.StructureForm;

import mchorse.bbs_mod.l10n.L10n;
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
        this.registerPanel(this.defaultPanel, L10n.lang("bbs.structureform.ui.structure"), Icons.TREE);
        this.registerDefaultPanels();
    }
}
