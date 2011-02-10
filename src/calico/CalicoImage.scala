/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.Graphics2D
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class CalicoImage(parent : CalicoElement) extends CalicoDisplay(parent) {
    var (left, right, bottom, top) = (0f, 1f, 0f, 1f)

    val image = try {
        ImageIO.read(new File("resources" + File.separator + parent.displayName))
    } catch {
        case e : IOException => {
            println("Calico Image Failed for " + "resources" + File.separator + parent.displayName)
            null
        }
    }

    def bounds = (left, right, bottom, top)
    def bounds_=(input : (Float, Float, Float, Float)) = {
        left = input._1
        right = input._2
        bottom = input._3
        top = input._4
    }

    override def draw(g : Graphics2D, offsetX : Int, offsetY : Int) = {
        g.drawImage(image, offsetX, offsetY, offsetX + width, offsetY + height, (image.getWidth*left).toInt,
                                                                                (image.getHeight*(1-top)).toInt,
                                                                                (image.getWidth*right).toInt,
                                                                                (image.getHeight*(1-bottom)).toInt, null)
        g.drawRect(offsetX, offsetY, width, height)

        g.drawRect(offsetX - 3, offsetY - 3, 6, 6)
        g.drawRect(width + offsetX - 3, offsetY - 3, 6, 6)
        g.drawRect(width + offsetX - 3, height + offsetY - 3, 6, 6)
        g.drawRect(offsetX - 3, height + offsetY - 3, 6, 6)
    }
}
