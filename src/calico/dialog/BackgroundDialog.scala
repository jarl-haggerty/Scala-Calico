package calico.dialog

import calico.CalicoFrame
import calico.CalicoPanel

import java.awt.Color
import scala.swing.Dialog
import swing.GridPanel
import swing.Button
import swing.TextField
import swing.Label
import swing.event.ButtonClicked

class BackgroundDialog(parent : CalicoFrame) extends Dialog(parent) {
    title = "Select background color components"
    
    val redInput = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].background.getRed.toString)
    val greenInput = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].background.getGreen.toString)
    val blueInput = new TextField(parent.panels.selection.page.content.asInstanceOf[CalicoPanel].background.getBlue.toString)
    val okButton = new Button("Ok")
    val cancelButton = new Button("Cancel")
    contents = new GridPanel(4, 2) {
        contents += new Label("Red = ")
        contents += redInput
        contents += new Label("Green = ")
        contents += greenInput
        contents += new Label("Blue = ")
        contents += blueInput
        contents += okButton
        contents += cancelButton
    }
    centerOnScreen
    
    listenTo(okButton, cancelButton)
    
    reactions += {
        case ButtonClicked(button) => {
            if(button == okButton) {
                parent.panels.selection.page.content.asInstanceOf[CalicoPanel].background = new Color(redInput.text.toInt, greenInput.text.toInt, blueInput.text.toInt)
                close
            } else if (button == cancelButton) {
                close
            }
        }
    }
}
