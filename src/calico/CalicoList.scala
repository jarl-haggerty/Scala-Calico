/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import scala.swing.BorderPanel
import scala.swing.Button
import scala.swing.GridPanel
import scala.swing.ListView
import scala.swing.ScrollPane
import scala.swing.event.ButtonClicked

class CalicoList(val frame : CalicoFrame) extends BorderPanel {
    val display = new ListView[CalicoElement]
    val addButton = new Button("Add")
    val removeButton = new Button("Remove")
    val editButton = new Button("Edit")

    layout(new ScrollPane(display)) = BorderPanel.Position.Center
    layout(new GridPanel(1, 3) {
        contents += addButton
        contents += removeButton
        contents += editButton
    }) = BorderPanel.Position.South

    listenTo(addButton, removeButton, editButton)
    reactions += {
        case ButtonClicked(button) => {
            if(button == addButton) {
                val xml = <Element>
                             <Property Key="Role" Value="Obstacle"/>
                             <Property Key="Name" Value=""/>
                             <Property Key="X" Value="0"/>
                             <Property Key="Y" Value="0"/>
                             <Property Key="Width" Value="0"/>
                             <Property Key="Height" Value="0"/>
                             <Property Key="ViewX" Value="0"/>
                             <Property Key="ViewY" Value="0"/>
                             <Property Key="AttachedToView" Value="false"/>
                             <Property Key="CalicoDisplay" Value="None"/>
                         </Element>
                frame.panels.selection.page.content.asInstanceOf[CalicoPanel].elements ::= new CalicoElement(xml, frame.panels.selection.page.content.asInstanceOf[CalicoPanel])
                frame.updateList
                frame.panels.selection.page.content.asInstanceOf[CalicoPanel].repaint
            }else if(button == removeButton) {
                frame.panels.selection.page.content.asInstanceOf[CalicoPanel].elements --= display.selection.items.toList
                frame.updateList
                frame.panels.selection.page.content.asInstanceOf[CalicoPanel].repaint
            }else if(button == editButton) {
                if(display.selection.items.length > 0) frame.editElement(display.selection.items.apply(0))
                frame.updateList
                frame.panels.selection.page.content.asInstanceOf[CalicoPanel].repaint
            }
        }
    }
}
