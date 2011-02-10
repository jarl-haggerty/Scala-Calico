/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico.dialog

import calico.CalicoFrame
import calico.CalicoElement
import calico.CalicoImage

import scala.swing.Label
import scala.swing.Orientation
import scala.swing.Panel
import scala.swing.RadioButton
import scala.swing.ScrollPane
import scala.swing.Swing.CompoundBorder
import scala.swing.Swing.TitledBorder
import scala.swing.Swing.EtchedBorder
import scala.swing.Swing.EmptyBorder
import calico.Utils
import java.awt.Dimension
import java.io.File
import javax.swing.table.DefaultTableModel
import scala.swing.Alignment
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.ButtonGroup
import scala.swing.CheckBox
import scala.swing.Dialog
import scala.swing.FileChooser
import scala.swing.FlowPanel
import scala.swing.GridBagPanel
import scala.swing.GridBagPanel.Fill
import scala.swing.GridPanel
import scala.swing.Table
import scala.swing.TextArea
import scala.swing.TextField
import scala.swing.event.ButtonClicked

class ElementDialog(parent : CalicoFrame, element : CalicoElement) extends Dialog(parent) {
    title = "Edit this element"
    val nameInput = new TextField(element.name)
    nameInput.horizontalAlignment = Alignment.Center
    val xInput = new TextField(element.x.toString)
    val yInput = new TextField(element.y.toString)
    val widthInput = new TextField(element.width.toString)
    val heightInput = new TextField(element.height.toString)
    val viewXInput = new TextField(element.viewX.toString)
    val viewYInput = new TextField(element.viewY.toString)
    val attachedToViewInput = new CheckBox("Attached to view"){selected = element.attachedToView}
    val rectangleInput = new RadioButton("Rectangle")
    val ellipseInput = new RadioButton("Ellipse")
    val LLInput = new RadioButton("LL")
    val LRInput = new RadioButton("LR")
    val URInput = new RadioButton("UR")
    val ULInput = new RadioButton("UL")
    val noneInput = new RadioButton("None")
    val imageInput = new RadioButton("Image")
    val displayInput = new ButtonGroup(rectangleInput,
                                       ellipseInput,
                                       LLInput,
                                       LRInput,
                                       URInput,
                                       ULInput,
                                       noneInput,
                                       imageInput)
    element.displayName match {
        case "Rectangle" => rectangleInput.selected = true
        case "Ellipse" => ellipseInput.selected = true
        case "LL" => LLInput.selected = true
        case "LR" => LRInput.selected = true
        case "UR" => URInput.selected = true
        case "UL" => ULInput.selected = true
        case "None" => noneInput.selected = true
        case _ => imageInput.selected = true
    }
    val imageLeftInput = new TextField(element.ImageBounds.left.toString)
    val imageRightInput = new TextField(element.ImageBounds.right.toString)
    val imageBottomInput = new TextField(element.ImageBounds.bottom.toString)
    val imageTopInput = new TextField(element.ImageBounds.top.toString)
    val imageButton = new Button("Load Image")
    val imageLabel = new Label(if(imageInput.selected) element.displayName else "")

    val codeInput = new Table{
        val newModel = new DefaultTableModel(element.code.size, 2)
        newModel.setColumnIdentifiers(Array[Object]("Key", "Value"))
        var i = 0
        for((key, value) <- element.code) {
            newModel.setValueAt(key, i, 0)
            newModel.setValueAt(value, i, 1)
            i += 1
        }
        model = newModel
    }

    val addRowButton = new Button("Add Row")
    val removeRowButton = new Button("Remove Row")
    val okButton = new Button("Ok")
    val cancelButton = new Button("Cancel")
    
    contents = new GridBagPanel {
        val constraints = new Constraints
        constraints.fill = Fill.Both
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 1
        constraints.gridheight = 1
        constraints.weightx = 0
        constraints.weighty = 0
        layout(new GridPanel(1, 2) {
            border = CompoundBorder(TitledBorder(EtchedBorder, "Name"), EmptyBorder(5,5,5,10))
            contents += nameInput
        }) = constraints

        constraints.gridy = 1
        layout(new GridPanel(8, 2) {
            border = CompoundBorder(TitledBorder(EtchedBorder, "Position and Size"), EmptyBorder(5,5,5,10))
            contents += new Label("X"){horizontalAlignment = Alignment.Right}
            contents += xInput
            contents += new Label("Y"){horizontalAlignment = Alignment.Right}
            contents += yInput
            contents += new Label("Width"){horizontalAlignment = Alignment.Right}
            contents += widthInput
            contents += new Label("Height"){horizontalAlignment = Alignment.Right}
            contents += heightInput
            contents += new Label("View X"){horizontalAlignment = Alignment.Right}
            contents += viewXInput
            contents += new Label("View Y"){horizontalAlignment = Alignment.Right}
            contents += viewYInput
            contents += new Label("")
            contents += attachedToViewInput
        }) = constraints

        constraints.gridy = 2
        layout(new BoxPanel(Orientation.Vertical) {
            border = CompoundBorder(TitledBorder(EtchedBorder, "Display"), EmptyBorder(5,5,5,10))
            for(b <- displayInput.buttons) {
                contents += b
            }

            contents += new GridPanel(4, 2) {
                border = CompoundBorder(TitledBorder(EtchedBorder, "Image Bounds"), EmptyBorder(5,5,5,10))
                contents += new Label("Left"){horizontalAlignment = Alignment.Right}
                contents += imageLeftInput
                contents += new Label("Right"){horizontalAlignment = Alignment.Right}
                contents += imageRightInput
                contents += new Label("Bottom"){horizontalAlignment = Alignment.Right}
                contents += imageBottomInput
                contents += new Label("Top"){horizontalAlignment = Alignment.Right}
                contents += imageTopInput
            }

            contents += new GridPanel(1, 2) {
                contents += imageButton
                contents += imageLabel
            }
        }) = constraints

        constraints.fill = Fill.Both
        constraints.gridx = 1
        constraints.gridy = 0
        constraints.gridwidth = 1
        constraints.gridheight = 3
        constraints.weightx = 1
        constraints.weighty = 1
        layout(new GridBagPanel {
            border = CompoundBorder(TitledBorder(EtchedBorder, "Code"), EmptyBorder(5,5,5,10))
            val constraints = new Constraints
            constraints.fill = Fill.Both
            constraints.gridx = 0
            constraints.gridy = 0
            constraints.gridwidth = 2
            constraints.gridheight = 1
            constraints.weightx = 1
            constraints.weighty = 1
            layout(new ScrollPane(codeInput)) = constraints
            constraints.gridy = 1
            constraints.gridwidth = 1
            constraints.weighty = 0
            layout(addRowButton) = constraints
            constraints.gridx = 1
            layout(removeRowButton) = constraints
        }) = constraints

        constraints.fill = Fill.Both
        constraints.gridx = 0
        constraints.gridy = 3
        constraints.gridwidth = 2
        constraints.gridheight = 1
        layout(new GridPanel(1, 2) {
            contents += okButton
            contents += cancelButton
        }) = constraints
    }
    size = new Dimension((size.width*1.5).toInt, size.height)
    centerOnScreen
    

    listenTo(okButton, cancelButton, addRowButton, removeRowButton, imageButton)

    reactions += {
        case ButtonClicked(button) => {
            if(button == okButton) {
                element.name = nameInput.text
                element.x = xInput.text.toFloat
                element.y = yInput.text.toFloat
                element.width = widthInput.text.toFloat
                element.height = heightInput.text.toFloat
                element.viewX = viewXInput.text.toFloat
                element.viewY = viewYInput.text.toFloat
                element.attachedToView = attachedToViewInput.selected
                element.displayName = if(imageInput.selected) imageLabel.text else displayInput.selected.get.text
                if(element.display.isInstanceOf[CalicoImage]) {
                    element.ImageBounds.left = imageLeftInput.text.toFloat
                    element.ImageBounds.right = imageRightInput.text.toFloat
                    element.ImageBounds.bottom = imageBottomInput.text.toFloat
                    element.ImageBounds.top = imageTopInput.text.toFloat
                } else {
                    element.ImageBounds.left = 0
                    element.ImageBounds.right = 1
                    element.ImageBounds.bottom = 0
                    element.ImageBounds.top = 1
                }
                var mappings : List[(String, String)] = Nil
                for(i <- 0 until codeInput.model.getRowCount) {
                    val temp = codeInput.model.getValueAt(i, 0).toString -> codeInput.model.getValueAt(i, 1).toString
                    if(temp._1.nonEmpty && temp._1.nonEmpty) {
                        mappings ::= temp
                    }
                }
                element.code = Map.empty[String, String] ++ mappings
                element.panel.repaint
                close
            } else if (button == cancelButton) {
                close
            } else if (button == addRowButton) {
                println(codeInput.model.asInstanceOf[DefaultTableModel].getColumnName(0) + ", " + codeInput.model.asInstanceOf[DefaultTableModel].getColumnName(1))
                codeInput.model.asInstanceOf[DefaultTableModel].addRow(Array[Object]("", ""))
            } else if (button == removeRowButton) {
                for(r <- codeInput.selection.rows) {
                    codeInput.model.asInstanceOf[DefaultTableModel].removeRow(r)
                }
            } else if (button == imageButton) {
                val fileChooser = if(parent.lastSceneryFolder == null) new FileChooser(new File("resources")) else new FileChooser(parent.lastSceneryFolder)
                if (fileChooser.showOpenDialog(this.codeInput) == FileChooser.Result.Approve)
                {
                    val file = fileChooser.selectedFile
                    val imageName = file.getAbsolutePath.substring(file.getAbsolutePath.lastIndexOf(File.separator) + 1)
                    Utils.copy(file.getAbsolutePath, "resources" + File.separator + imageName)
                    imageLabel.text = imageName
                    parent.lastSceneryFolder = file.getParentFile
                }
            }
        }
    }
}
