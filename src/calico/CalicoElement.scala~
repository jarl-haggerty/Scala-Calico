/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.Graphics2D
import java.lang.Math
import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq

class CalicoElement(elem : Elem, val panel : CalicoPanel) {
    abstract class Hook
    object None extends Hook
    object Whole extends Hook
    object LL extends Hook
    object LR extends Hook
    object UR extends Hook
    object UL extends Hook

    var hook : Hook = None
    var name = ""
    var x = 0f
    var y = 0f
    var viewX = 0f
    var viewY = 0f
    var _width = 0f
    def width = _width
    def width_=(newWidth : Float) : Unit =  _width = newWidth
    var _height = 0f
    def height = _height
    def height_=(newHeight : Float) : Unit = _height = newHeight
    var attachedToView = false
    var display : CalicoDisplay = null
    var _displayName = "Rectangle"
    def displayName = _displayName
    def displayName_=(input : String) = {
        _displayName = input
        display = displayName match {
            case "Rectangle" => new CalicoRectangle(this)
            case "LL" => new CalicoLL(this)
            case "LR" => new CalicoLR(this)
            case "UR" => new CalicoUR(this)
            case "UL" => new CalicoUL(this)
            case "Ellipse" => new CalicoEllipse(this)
            case "None" => null
            case _ => {
                val temp = new CalicoImage(this)
                temp.bounds = (ImageBounds.left, ImageBounds.right, ImageBounds.bottom, ImageBounds.top)
                width = temp.image.getWidth*(ImageBounds.right-ImageBounds.left)/panel.pixelsPerMeter
                height = temp.image.getHeight*(ImageBounds.top-ImageBounds.bottom)/panel.pixelsPerMeter
                temp
            }
        }
    }
    object ImageBounds {
        var _left = 0f
        def left = _left
        def left_=(input : Float) = {
            _left = input
            if(display.isInstanceOf[CalicoImage]) {
                display.asInstanceOf[CalicoImage].left = _left
                width = display.asInstanceOf[CalicoImage].image.getWidth*(right-left)/panel.pixelsPerMeter
            }
        }
        var _right = 1f
        def right = _right
        def right_=(input : Float) = {
            _right = input
            if(display.isInstanceOf[CalicoImage]) {
                display.asInstanceOf[CalicoImage].right = _right
                width = display.asInstanceOf[CalicoImage].image.getWidth*(right-left)/panel.pixelsPerMeter
            }
        }
        var _bottom = 0f
        def bottom = _bottom
        def bottom_=(input : Float) = {
            _bottom = input
            if(display.isInstanceOf[CalicoImage]) {
                display.asInstanceOf[CalicoImage].bottom = _bottom
                height = display.asInstanceOf[CalicoImage].image.getHeight*(top-bottom)/panel.pixelsPerMeter
            }
        }
        var _top = 1f
        def top = _top
        def top_=(input : Float) = {
            _top = input
            if(display.isInstanceOf[CalicoImage]) {
                display.asInstanceOf[CalicoImage].top = _top
                height = display.asInstanceOf[CalicoImage].image.getHeight*(top-bottom)/panel.pixelsPerMeter
            }
        }
    }

    var code = Map.empty[String, String]
    for(e <- elem.child if e.label == "Property") {
        (e \ "@Key" text) match {
            case "Name" => name = (e \ "@Value" text)
            case "X" => x = (e \ "@Value" text).toFloat
            case "Y" => y = (e \ "@Value" text).toFloat
            case "ViewX" => viewX = (e \ "@Value" text).toFloat
            case "ViewY" => viewY = (e \ "@Value" text).toFloat
            case "Width" => width = (e \ "@Value" text).toFloat
            case "Height" => height = (e \ "@Value" text).toFloat
            case "AttachedToView" => attachedToView = (e \ "@Value" text).toBoolean
            case "CalicoDisplay" => displayName = (e \ "@Value" text)
            case "ImageLeft" => ImageBounds.left = (e \ "@Value" text).toFloat
            case "ImageRight" => ImageBounds.right = (e \ "@Value" text).toFloat
            case "ImageBottom" => ImageBounds.bottom = (e \ "@Value" text).toFloat
            case "ImageTop" => ImageBounds.top = (e \ "@Value" text).toFloat
            case _ => code = code(e \ "@Key" text) = (e \ "@Value" text)
        }
    }
    var nameWidth = 0
    var nameHeight = 0

    def copy : CalicoElement = new CalicoElement((xml \ "Element")(0).asInstanceOf[Elem], panel)

    var selected = false

    def isHit(x : Int, y : Int) : Boolean = isHitInner(x/panel.pixelsPerMeter, (panel.worldHeight - y)/panel.pixelsPerMeter)
    def isHitInner(x : Float, y : Float) : Boolean = {
        if(attachedToView || display == null) return false

        if(x > this.x - 3/panel.pixelsPerMeter &&
           x < this.x + this.width + 3/panel.pixelsPerMeter &&
           y > this.y - 3/panel.pixelsPerMeter &&
           y < this.y + this.height + 3/panel.pixelsPerMeter) return true
        else if(x > this.x && x < this.x + nameWidth && y > y - nameHeight && y < y) return true
        else return false
    }

    def xOnPanel = (x * panel.pixelsPerMeter).toInt
    def yOnPanel = (panel.worldHeight - (y + height) * panel.pixelsPerMeter).toInt
    def widthOnPanel = (width * panel.pixelsPerMeter).toInt
    def heightOnPanel = (height * panel.pixelsPerMeter).toInt
    def xOnPanel_=(x : Int) = this.x = x / panel.pixelsPerMeter
    def yOnPanel_=(y : Int) = this.y = (panel.worldHeight - y)/panel.pixelsPerMeter - height
    def widthOnPanel_=(width : Int) = this.width = width / panel.pixelsPerMeter
    def heightOnPanel_=(height : Int) = {
        this.y += this.height - height/panel.pixelsPerMeter
        this.height = height / panel.pixelsPerMeter
    }

    def draw(g : Graphics2D, offsetX : Int, offsetY : Int) : Unit = {
        if(display == null) return
        if(selected) g.setColor(panel.selectColor)
        else g.setColor(panel.foregroundColor)
        val measure = g.getFontMetrics
        val stringDims = measure.getStringBounds(name, g)
        nameWidth = stringDims.getWidth.toInt
        nameHeight = stringDims.getHeight.toInt

        val (finalX, finalY) = if(attachedToView){
            ((panel.bounds.width*viewX - this.width*panel.pixelsPerMeter/2).toInt, (panel.bounds.height*viewY - (this.height*panel.pixelsPerMeter + nameHeight)/2).toInt)
        }else{
            (xOnPanel - offsetX, yOnPanel - offsetY)
        }

        g.drawString(name, finalX, finalY - nameHeight)
        display.draw(g, finalX, finalY)
    }

    def unHook = hook = None
    def drag(exclusive : Boolean, x : Int, y : Int, dx : Int, dy : Int) = dragInner(exclusive, x/panel.pixelsPerMeter, (panel.worldHeight - y)/panel.pixelsPerMeter, dx/panel.pixelsPerMeter, -dy/panel.pixelsPerMeter)
    def dragInner(exclusive : Boolean, x : Float, y : Float, dx : Float, dy : Float) : Unit = {
        if(attachedToView) return

        if(!exclusive || hook == Whole) {
            this.x += dx
            this.y += dy
            return
        }

        hook match {
            case None => {
                val llDistance = Math.abs(x - this.x) + Math.abs(y - this.y);
                val lrDistance = Math.abs(x - (this.x + width)) + Math.abs(y - this.y);
                val urDistance = Math.abs(x - (this.x + width)) + Math.abs(y - (this.y + height));
                val ulDistance = Math.abs(x - this.x) + Math.abs(y - (this.y + height));
                if(lrDistance <= 6/panel.pixelsPerMeter && lrDistance <= llDistance && lrDistance <= urDistance && lrDistance <= ulDistance) hook = LR
                else if(llDistance <= 6/panel.pixelsPerMeter && llDistance <= lrDistance && llDistance <= urDistance && llDistance <= ulDistance) hook = LL
                else if(urDistance <= 6/panel.pixelsPerMeter && urDistance <= llDistance && urDistance <= lrDistance && urDistance <= ulDistance) hook = UR
                else if(ulDistance <= 6/panel.pixelsPerMeter && ulDistance <= llDistance && ulDistance <= lrDistance && ulDistance <= urDistance) hook = UL
                else hook = Whole
                dragInner(exclusive, x, y, dx, dy);
            }
            case UR => {
                width += dx
                height += dy
                if(width < 0 && height < 0) {
                    hook = LL
                    width *= -1
                    height *= -1
                    this.x -= width
                    this.y -= height
                } else if(width < 0) {
                    hook = UL
                    width *= -1
                    this.x -= width
                } else if(height < 0) {
                    hook = LR
                    height *= -1
                    this.y -= height
                }
            }
            case LR => {
                width += dx
                this.y += dy
                height -= dy
                if(width < 0 && width < 0) {
                    hook = UL
                    width *= -1
                    height *= -1
                    this.x -= width
                    this.y -= height
                } else if(width < 0) {
                    hook = LL
                    width *= -1
                    this.x -= width
                } else if(height < 0) {
                    hook = UR
                    height *= -1
                    this.y -= height
                }
            }
            case LL => {
                this.x += dx
                width -= dx
                this.y += dy
                height -= dy
                if(width < 0 && width < 0) {
                    hook = UR
                    width *= -1
                    height *= -1
                    this.x -= width
                    this.y -= height
                } else if(width < 0) {
                    hook = LR
                    width *= -1
                    this.x -= width
                } else if(height < 0) {
                    hook = UL
                    height *= -1
                    this.y -= height
                }
            }
            case UL => {
                this.x += dx
                width -= dx
                height += dy
                if(width < 0 && width < 0) {
                    hook = LR
                    width *= -1
                    height *= -1
                    this.x -= width
                    this.y -= height
                } else if(width < 0) {
                    hook = UR
                    width *= -1
                    this.x -= width
                } else if(height < 0) {
                    hook = LL
                    height *= -1
                    this.y -= height
                }
            }
        }
    }

    def xml : NodeSeq = {
        var extra : List[NodeSeq] = Nil
        for((key, value) <- code) {
            extra ::=
<Temp>
        <Property Key={key} Value={value}/></Temp>.child
        }
<Temp>    <Element>
        <Property Key="Name" Value={name} />
        <Property Key="X" Value={x.toString} />
        <Property Key="Y" Value={y.toString} />
        <Property Key="ViewX" Value={viewX.toString} />
        <Property Key="ViewY" Value={viewY.toString} />
        <Property Key="Width" Value={width.toString} />
        <Property Key="Height" Value={height.toString} />
        <Property Key="AttachedToView" Value={attachedToView.toString} />
        <Property Key="CalicoDisplay" Value={displayName} />
        <Property Key="ImageLeft" Value={ImageBounds.left.toString} />
        <Property Key="ImageRight" Value={ImageBounds.right.toString} />
        <Property Key="ImageBottom" Value={ImageBounds.bottom.toString} />
        <Property Key="ImageTop" Value={ImageBounds.top.toString} />{extra}
    </Element>
</Temp>.child
    }

    override def toString = if(name.nonEmpty) name else "Anonymous"
}
