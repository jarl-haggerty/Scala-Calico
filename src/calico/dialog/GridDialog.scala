package calico.dialog

import calico.CalicoFrame
import calico.CalicoPanel

import swing.Dialog
import swing.GridPanel
import scala.swing.CheckBox
import swing.Button
import swing.TextField
import swing.Label
import swing.event.ButtonClicked

class GridDialog(parent : CalicoFrame) extends Dialog(parent) {
    val input = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].spacing.toString)
    val useGridInput = new CheckBox("Use Grid"){selected = parent.panels.selection.page.content.asInstanceOf[CalicoPanel].useGrid}
    val okButton = new Button("Ok")
    val cancelButton = new Button("Cancel")
    contents = new GridPanel(3, 2) {
        contents += new Label("Grid Size = ")
        contents += input
        contents += new Label("")
        contents += useGridInput
        contents += okButton
        contents += cancelButton
    }
    centerOnScreen
    
    listenTo(okButton, cancelButton)
    
    reactions += {
        case ButtonClicked(button) => {
            if(button == okButton) {
                parent.panels.selection.page.content.asInstanceOf[CalicoPanel].spacing = input.text.toFloat
                parent.panels.selection.page.content.asInstanceOf[CalicoPanel].useGrid = useGridInput.selected
                close
            } else if (button == cancelButton) {
                close
            }
        }
    }
}
