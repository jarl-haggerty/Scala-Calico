package calico.dialog

import calico.CalicoFrame
import calico.CalicoPanel

import swing.Dialog
import swing.GridPanel
import swing.Button
import swing.TextField
import swing.Label
import swing.event.ButtonClicked

class ResizeDialog(parent : CalicoFrame) extends Dialog(parent) {
    modal = true

    val widthInput = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].realWorldWidth.toString)
    val heightInput = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].realWorldHeight.toString)
    val okButton = new Button("Ok")
    val cancelButton = new Button("Cancel")
    contents = new GridPanel(3, 2) {
        contents += new Label("Width = ")
        contents += widthInput
        contents += new Label("Height = ")
        contents += heightInput
        contents += okButton
        contents += cancelButton
    }
    centerOnScreen

    
    listenTo(okButton, cancelButton)
    
    reactions += {
        case ButtonClicked(button) => {
            if(button == okButton) {
                parent.panels.selection.page.content.asInstanceOf[CalicoPanel].worldBounds = (widthInput.text.toFloat, heightInput.text.toFloat)
                close
            } else if (button == cancelButton) {
                close
            }
        }
    }
}
