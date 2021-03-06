/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.io.File
import java.lang.Math
import javax.swing.JPopupMenu
import scala.swing.Action
import scala.swing.BorderPanel
import scala.swing.FileChooser
import scala.swing.MenuItem
import scala.swing.Orientation
import scala.swing.Panel
import scala.swing.ScrollBar
import scala.swing.event.Event
import scala.swing.event.KeyReleased
import scala.swing.event.MouseDragged
import scala.swing.event.MousePressed
import scala.swing.event.MouseReleased
import scala.swing.event.UIElementResized
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.XML

class CalicoPanel(val frame : CalicoFrame, var file : File = null) extends BorderPanel {
    var _elements : List[CalicoElement] = Nil
    def elements = _elements
    def elements_=(newElements : List[CalicoElement]) = {
        _elements = newElements
        frame.updateList
    }
    var _selected : List[CalicoElement] = Nil
    def selected = _selected
    def selected_=(newSelected : List[CalicoElement]) = {
        for(s <- _selected) s.selected = false
        for(s <- newSelected) s.selected = true
        _selected = newSelected
    }
    var clipBoard : List[CalicoElement] = Nil
    var selecting = false
    var lastX = 0
    var lastY = 0
    var title = ""
    var _pixelsPerMeter = 1f
    def pixelsPerMeter = _pixelsPerMeter
    def pixelsPerMeter_=(input : Float) = {
        _pixelsPerMeter = input
        _spacing = (realSpacing * pixelsPerMeter).toInt
        resizeWorld
    }
    var worldX = 0
    var worldY = 0
    var realWorldWidth = 500f
    var realWorldHeight = 500f
    var worldWidth = 500
    var worldHeight = 500
    def worldBounds = (realWorldWidth, realWorldHeight)
    def worldBounds_=(input : (Float, Float)) = {
        resizeRealWorld(input._1, input._2)
    }
    var _useGrid = false
    def useGrid = _useGrid
    def useGrid_=(input : Boolean) = {
        _useGrid = input
        repaint
    }
    var realSpacing = 1f
    var _spacing = 1
    def spacing = realSpacing
    def spacing_=(newSpacing : Float) = {
        realSpacing = newSpacing
        _spacing = (realSpacing * pixelsPerMeter).toInt
        repaint
    }
    var popX = -1
    var popY = -1
    var selectColor = Color.red
    var backgroundColor = Color.black
    var foregroundColor = Color.white
    var good = true
    object selection{
        var x = 0
        var y = 0
        var width = 0
        var height = 0
    }

    if(file != null) {
        val extension_index = file.getAbsolutePath.indexOf(".")
        val fileIndex = file.getAbsolutePath.lastIndexOf(File.separator)
        val title = if(extension_index < 0) {
            file.getAbsolutePath().substring(fileIndex + 1);
        } else {
            file.getAbsolutePath().substring(fileIndex + 1, extension_index);
        }

        try {
            val xml = XML.loadFile(file)
            for(e <- (xml \ "Level")(0).child if e.label == "Property") {
                (e \ "@Key" text) match {
                    case "PixelsPerMeter" => {
                        _pixelsPerMeter = (e \ "@Value" text).toFloat
                    }
                    case "Width" => {
                        realWorldWidth = (e \ "@Value" text).toFloat
                        worldWidth = (realWorldWidth * pixelsPerMeter).toInt
                    }
                    case "Height" => {
                        realWorldHeight = (e \ "@Value" text).toFloat
                        worldHeight = (realWorldHeight * pixelsPerMeter).toInt
                    }
                    case "UseGrid" => {
                        _useGrid = (e \ "@Value" text).toBoolean
                    }
                    case "GridSpacing" => {
                        realSpacing = (e \ "@Value" text).toFloat
                        _spacing = (realSpacing * pixelsPerMeter).toInt
                    }
                    case "BackgroundColor" => {
                        val temp = (e \ "@Value" text).split(",")
                        backgroundColor = new Color(temp(0).toInt, temp(1).toInt, temp(2).toInt)
                    }
                }
            }

            for(x <- xml \ "Element") elements = new CalicoElement(x.asInstanceOf[Elem], this) :: elements
        } catch {
            case e : Exception => good = false
        }
    }

    val horizontalBar = new ScrollBar {orientation = Orientation.Horizontal}
    layout(horizontalBar) = BorderPanel.Position.South
    val verticalBar = new ScrollBar {orientation = Orientation.Vertical}
    layout(verticalBar) = BorderPanel.Position.East

    worldBounds = (realWorldWidth, realWorldHeight)

    val overElement = new JPopupMenu
    overElement.add(new MenuItem(Action("Bring To Front") {
        if(selected == Nil) {
            val temp = elementAt(popX, popY)
            elements -= temp
            elements = elements ::: temp :: Nil
        } else {
            elements --= selected
            elements = elements ::: selected
        }
    }).peer)
    overElement.add(new MenuItem(Action("Move to Back") {
        if(selected == Nil) {
            val temp = elementAt(popX, popY)
            elements -= temp
            elements =  temp :: elements
        } else {
            elements --= selected
            elements = selected ::: elements
        }
    }).peer)
    overElement.add(new MenuItem(Action("Edit Element") {
        frame.editElement(elementAt(popX, popY))
    }).peer)
    overElement.add(new MenuItem(Action("Resize World") {
        resizeWorld(popX, popY)
    }).peer)

    val overNothing = new JPopupMenu
    overNothing.add(new MenuItem(Action("Resize World") {
        resizeWorld(popX, popY)
    }).peer)

    this.focusable = true
    mouse.clicks.reactions += {
        case event : MousePressed => {
            if(event.peer.getButton == MouseEvent.BUTTON1) {
                if(event.peer.getClickCount == 1)  {
                    val temp = frame.newElement
                    if(temp != null) {
                        elements = temp :: elements
                        temp.xOnPanel = event.peer.getX + worldX
                        temp.yOnPanel = event.peer.getY + worldY
                        selected = temp :: Nil
                    } else {
                        val temp2 = elementAt(event.peer.getX + worldX, event.peer.getY + worldY);
                        if(!selected.contains(temp2)) {
                            if(!event.peer.isControlDown) selected = Nil
                            if(temp2 != null) {
                                selected = temp2 :: selected
                            } else {
                                selecting = true;
                                selection.x = event.peer.getX + worldX
                                selection.y = event.peer.getY + worldY
                                selection.width = 0
                                selection.height = 0
                            }
                        }
                    }
                    lastX = event.peer.getX
                    lastY = event.peer.getY
                } else {
                    val temp = elementAt(event.peer.getX + worldX, event.peer.getY + worldY)
                    if(temp != null) frame.editElement(temp)
                }
            } else if(event.peer.getButton == MouseEvent.BUTTON3) {
                popX = event.peer.getX
                popY = event.peer.getY
                if(elementAt(popX + worldX, popY + worldY) == null) overNothing.show(this.peer, popX, popY)
                else overElement.show(this.peer, popX, popY)
            }
            repaint
        }
        case event : MouseReleased => {
            requestFocusInWindow
            if(selecting) {
                selecting = false;
                if(selection.width < 0 && selection.height < 0) {
                    selection.width *= -1
                    selection.height *= -1
                    selection.x -= selection.width
                    selection.y -= selection.height
                } else if(selection.width < 0) {
                    selection.width *= -1
                    selection.x -= selection.width
                } else if(selection.height < 0) {
                    selection.height *= -1
                    selection.y -= selection.height
                }

                if(!event.peer.isControlDown) selected = Nil
                selected :::= elementsUnder(selection.x, selection.y, selection.width, selection.height)
            }
            for(s <- selected) s.unHook
            if(useGrid) {
                for(s <- selected) {
                    s.xOnPanel = ((s.xOnPanel + _spacing/2) / _spacing)*_spacing
                    s.yOnPanel = ((s.yOnPanel + _spacing/2) / _spacing)*_spacing
                    s.widthOnPanel = ((s.widthOnPanel + _spacing/2) / _spacing)*_spacing
                    s.heightOnPanel = ((s.heightOnPanel + _spacing/2) / _spacing)*_spacing
                    s.widthOnPanel = Math.max(s.widthOnPanel, _spacing)
                    s.heightOnPanel = Math.max(s.heightOnPanel, _spacing)
                }
            }
            if(event.peer.getButton == MouseEvent.BUTTON3) {
                popX = event.peer.getX
                popY = event.peer.getY
                if(elementAt(popX + worldX, popY + worldY) == null) overNothing.show(this.peer, popX, popY)
                else overElement.show(this.peer, popX, popY)
            }
            repaint
        }
    }
    mouse.moves.reactions += {
        case event : MouseDragged => {
            if(selecting) {
                selection.width += event.peer.getX - lastX
                selection.height += event.peer.getY - lastY
            } else {
                if(selected.length == 1) {
                    selected.head.drag(true, lastX + worldX, lastY + worldY, event.peer.getX - lastX, event.peer.getY - lastY)
                } else {
                    for(s <- selected) {
                        s.drag(false, lastX + worldX, lastY + worldY, event.peer.getX - lastX, event.peer.getY - lastY)
                    }
                }
            }
            lastX = event.peer.getX
            lastY = event.peer.getY
            repaint
        }
    }
    keys.reactions += {
        case event : KeyReleased => {
            event.peer.getKeyCode match {
                case KeyEvent.VK_DELETE => {
                    elements --= selected
                    repaint
                }
            }
            if(event.peer.isControlDown) {
                event.peer.getKeyCode match {
                    case KeyEvent.VK_C => clipBoard = selected map (x => x.copy)
                    case KeyEvent.VK_Z => {}
                    case KeyEvent.VK_X => {
                        clipBoard = selected map (x => x.copy)
                        elements --= selected
                    }
                    case KeyEvent.VK_V => {
                        if(clipBoard.nonEmpty) {
                            var minX = clipBoard.head.xOnPanel
                            var minY = clipBoard.head.yOnPanel
                            for(element <- clipBoard) {
                                if(element.xOnPanel < minX) minX = element.xOnPanel
                                if(element.yOnPanel < minY) minY = element.yOnPanel
                            }
                            for(element <- clipBoard) {
                                element.xOnPanel = worldX + (element.xOnPanel - minX)
                                element.yOnPanel = worldY + (element.yOnPanel - minY)
                            }
                            elements = clipBoard ::: elements
                            selected = clipBoard
                            clipBoard = Nil
                            repaint
                        }
                    }
                }
            }
        }
    }

    object HorizontalScrollListener extends AdjustmentListener {
        def adjustmentValueChanged(e : AdjustmentEvent) = {
            worldX = horizontalBar.value
            repaint
        }
    }
    horizontalBar.peer.addAdjustmentListener(HorizontalScrollListener)

    object VerticalScrollListener extends AdjustmentListener {
        def adjustmentValueChanged(e : AdjustmentEvent) = {
            worldY = verticalBar.value
            repaint
        }
    }
    verticalBar.peer.addAdjustmentListener(VerticalScrollListener)

    listenTo(this)
    reactions += {
        case e : UIElementResized => {
            worldX = Math.max(Math.min(worldX, worldWidth - (bounds.width - verticalBar.bounds.width)), 0)
            worldY = Math.max(Math.min(worldY, worldHeight - (bounds.height - horizontalBar.bounds.height)), 0)

            horizontalBar.value = worldX
            verticalBar.value = worldY

            horizontalBar.visibleAmount = bounds.width - verticalBar.bounds.width
            verticalBar.visibleAmount = bounds.height - horizontalBar.bounds.height

            repaint
        }
    }

    def elementAt(x : Int, y : Int) : CalicoElement = {
        for(element <- elements.reverse) {
            if(element.isHit(x, y)) return element
        }
        return null
    }

    
    def save : Unit = {
        if(file == null) {
            saveAs
        } else {
            val temp = backgroundColor.getRed + "," + backgroundColor.getGreen + "," + backgroundColor.getBlue
            val levelXML =
<Temp>    <Level>
       <Property Key="PixelsPerMeter" Value={pixelsPerMeter.toString}/>
       <Property Key="Width" Value={realWorldWidth.toString}/>
       <Property Key="Height" Value={realWorldHeight.toString}/>
       <Property Key="UseGrid" Value={useGrid.toString}/>
       <Property Key="BackgroundColor" Value={temp}/>
       <Property Key="GridSpacing" Value={realSpacing.toString}/>
    </Level></Temp>.child

            var elementsXML : List[NodeSeq] = Nil
            for(element <- elements) elementsXML = element.xml :: elementsXML
            val output =
<Calico>
{levelXML}
{elementsXML}</Calico>
            XML.save(file.getPath, output, "UTF-8", true)
        }
    }
    def saveAs : Unit = {
        val fileChooser = new FileChooser
        if(fileChooser.showSaveDialog(frame.panels) == FileChooser.Result.Approve) {
            file = fileChooser.selectedFile
            val extensionIndex = file.getAbsolutePath.lastIndexOf(".")
            if(extensionIndex != file.getAbsolutePath.length - 4) {
                title = file.getAbsolutePath.substring(file.getAbsolutePath.lastIndexOf(File.separator) + 1)
                file = new File(file.getAbsolutePath + ".xml")
            } else {
                title = file.getAbsolutePath.substring(file.getAbsolutePath.lastIndexOf(File.separator) + 1, extensionIndex);
            }
            this.save
        }
    }

    def resizeWorld = {
        worldWidth = (realWorldWidth * pixelsPerMeter).toInt
        worldHeight = (realWorldHeight * pixelsPerMeter).toInt

        horizontalBar.value = Math.max(Math.min(worldX, worldWidth - (bounds.width - verticalBar.bounds.width)), 0)
        verticalBar.value = Math.max(Math.min(worldY, worldHeight - (bounds.height - horizontalBar.bounds.height)), 0)

        horizontalBar.visibleAmount = bounds.width - verticalBar.bounds.width
        verticalBar.visibleAmount = bounds.height - horizontalBar.bounds.height

        horizontalBar.maximum = worldWidth + verticalBar.bounds.width
        verticalBar.maximum = worldHeight + horizontalBar.bounds.height

        repaint
    }

    def resizeWorld(width : Int, height : Int) = {
        realWorldWidth = width/pixelsPerMeter
        realWorldHeight = height/pixelsPerMeter
        worldWidth = width
        worldHeight = height

        horizontalBar.value = Math.max(Math.min(worldX, worldWidth - (bounds.width - verticalBar.bounds.width)), 0)
        verticalBar.value = Math.max(Math.min(worldY, worldHeight - (bounds.height - horizontalBar.bounds.height)), 0)

        horizontalBar.visibleAmount = bounds.width - verticalBar.bounds.width
        verticalBar.visibleAmount = bounds.height - horizontalBar.bounds.height

        horizontalBar.maximum = width + verticalBar.bounds.width
        verticalBar.maximum = height + horizontalBar.bounds.height

        repaint
    }

    def resizeRealWorld(width : Float, height : Float) = {
        realWorldWidth = width
        realWorldHeight = height
        worldWidth = (width * pixelsPerMeter).toInt
        worldHeight = (height * pixelsPerMeter).toInt

        horizontalBar.value = Math.max(Math.min(worldX, worldWidth - (bounds.width - verticalBar.bounds.width)), 0)
        verticalBar.value = Math.max(Math.min(worldY, worldHeight - (bounds.height - horizontalBar.bounds.height)), 0)

        horizontalBar.visibleAmount = bounds.width - verticalBar.bounds.width
        verticalBar.visibleAmount = bounds.height - horizontalBar.bounds.height

        horizontalBar.maximum = worldWidth + verticalBar.bounds.width
        verticalBar.maximum = worldHeight + horizontalBar.bounds.height
        
        repaint
    }

    def elementsUnder(x : Int, y : Int, width : Int, height : Int) : List[CalicoElement] = {
        var thoseUnder : List[CalicoElement] = Nil
        for(element <- elements) {
            if(element.display != null && !element.attachedToView){
                if(element.xOnPanel > x &&
                   element.xOnPanel < x + width &&
                   element.yOnPanel > y &&
                   element.yOnPanel < y + height
                   ||
                   element.xOnPanel + element.widthOnPanel > x &&
                   element.xOnPanel + element.widthOnPanel < x + width &&
                   element.yOnPanel > y &&
                   element.yOnPanel < y + height
                   ||
                   element.xOnPanel + element.widthOnPanel > x &&
                   element.xOnPanel + element.widthOnPanel < x + width &&
                   element.yOnPanel + element.heightOnPanel > y &&
                   element.yOnPanel + element.heightOnPanel < y + height
                   ||
                   element.xOnPanel > x &&
                   element.xOnPanel < x + width &&
                   element.yOnPanel + element.heightOnPanel > y &&
                   element.yOnPanel + element.heightOnPanel < y + height) {
                    thoseUnder = element :: thoseUnder
                }
            }
        }
        return thoseUnder
    }

    override def paintComponent(g : Graphics2D) = {
        foregroundColor = new Color(255 - backgroundColor.getRed, 255 - backgroundColor.getGreen, 255 - backgroundColor.getBlue)
        val gridRed = Math.abs(backgroundColor.getRed - foregroundColor.getRed)/2 + Math.min(foregroundColor.getRed, backgroundColor.getRed)
        val gridGreen = Math.abs(backgroundColor.getGreen - foregroundColor.getGreen)/2 + Math.min(foregroundColor.getGreen, backgroundColor.getGreen)
        val gridBlue = Math.abs(backgroundColor.getBlue - foregroundColor.getBlue)/2 + Math.min(foregroundColor.getBlue, backgroundColor.getBlue)
        val gridColor = new Color(gridRed, gridGreen, gridBlue)

        selectColor = new Color(Math.round(backgroundColor.getRed / 255f) * 255, foregroundColor.getGreen, foregroundColor.getBlue)

        g.setColor(backgroundColor)
        g.fillRect(0, 0, worldWidth, worldHeight)

        if(useGrid) {
            g.setColor(gridColor);
            val offsetX = worldX % _spacing
            val offsetY = worldY % _spacing
            for(a <- (_spacing - offsetX) until bounds.width by _spacing) g.drawLine(a, 0, a, bounds.height)
            for(a <- (_spacing - offsetY) until bounds.height by _spacing) g.drawLine(0, a, bounds.width, a)
        }

        for(element <- elements) element.draw(g, worldX, worldY)

        if(selecting) {
            g.setColor(foregroundColor)
            if(selection.width >= 0 && selection.height >= 0) {
                g.drawRect(selection.x - worldX, selection.y - worldY, selection.width, selection.height)
            } else if(selection.width < 0 && selection.height < 0) {
                g.drawRect(selection.x + selection.width - worldX, selection.y + selection.height - worldY, -selection.width, -selection.height)
            } else if(selection.width < 0) {
                g.drawRect(selection.x + selection.width - worldX, selection.y - worldY, -selection.width, selection.height)
            } else if(selection.height < 0) {
                g.drawRect(selection.x - worldX, selection.y + selection.height - worldY, selection.width, -selection.height)
            }
        }

        g.setColor(Color.cyan);
        if(worldWidth - worldX < bounds.width) g.fillRect(worldWidth - worldX, 0, bounds.width, bounds.height)
        if(worldHeight - worldY < bounds.height) g.fillRect(0, worldHeight, bounds.width, bounds.height)
    }
}
