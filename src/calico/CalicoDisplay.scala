/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.awt.Graphics2D

abstract class CalicoDisplay(val parent : CalicoElement) {
    def draw(g : Graphics2D, offsetX : Int, offsetY : Int)
    def x = parent.xOnPanel
    def y = parent.yOnPanel
    def width = parent.widthOnPanel
    def height = parent.heightOnPanel
}
