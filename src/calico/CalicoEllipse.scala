/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Stroke

class CalicoEllipse(parent : CalicoElement) extends CalicoDisplay(parent) {
    override def draw(g : Graphics2D, offsetX : Int, offsetY : Int) = {
        g.drawOval(offsetX, offsetY, width, height)

        g.drawRect(offsetX - 3, offsetY - 3, 6, 6)
        g.drawRect(width + offsetX - 3, offsetY - 3, 6, 6)
        g.drawRect(width + offsetX - 3, height + offsetY - 3, 6, 6)
        g.drawRect(offsetX - 3, height + offsetY - 3, 6, 6)

        val temp = new Array[Float](1)
        temp(0) = 9
        val drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, temp, 0)
        val originalStroke = g.getStroke
        g.setStroke(drawingStroke)
        g.drawRect(offsetX, offsetY, width, height)
        g.setStroke(originalStroke)
    }
}
