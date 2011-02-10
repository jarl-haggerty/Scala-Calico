package calico.dialog

import calico.CalicoFrame
import calico.CalicoPanel

import swing.Dialog
import swing.GridPanel
import swing.Button
import swing.TextField
import swing.Label
import swing.event.ButtonClicked

class RescaleDialog(parent : CalicoFrame) extends Dialog(parent) {
    val input = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].pixelsPerMeter.toString)
    val okButton = new Button("Ok")
    val cancelButton = new Button("Cancel")
    contents = new GridPanel(2, 2) {
        contents += new Label("Scale(pixels/meter) = ")
        contents += input
        contents += okButton
        contents += cancelButton
    }
    centerOnScreen
    
    listenTo(okButton, cancelButton)
    
    reactions += {
        case ButtonClicked(button) => {
            if(button == okButton) {
                parent.panels.selection.page.content.asInstanceOf[CalicoPanel].pixelsPerMeter = input.text.toFloat
                close
            } else if (button == cancelButton) {
                close
            }
        }
    }
}
