/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.BasicStroke
import java.awt.Graphics2D

class CalicoUL(parent : CalicoElement) extends CalicoDisplay(parent){
    override def draw(g : Graphics2D, offsetX : Int, offsetY : Int)
    {
        g.drawLine(offsetX, offsetY, width  + offsetX, offsetY)
        g.drawLine(width + offsetX, offsetY, offsetX, height + offsetY)
        g.drawLine(offsetX, height + offsetY, offsetX, offsetY)

        g.drawRect(offsetX - 3, offsetY - 3, 6, 6);
        g.drawRect(width + offsetX - 3, offsetY - 3, 6, 6);
        g.drawRect(width + offsetX - 3, height + offsetY - 3, 6, 6);
        g.drawRect(offsetX - 3, height + offsetY - 3, 6, 6);

        val drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, Array(9f), 0);
        val originalStroke = g.getStroke
        g.setStroke(drawingStroke)
        g.drawRect(offsetX, offsetY, width, height)
        g.setStroke(originalStroke)
    }
}
