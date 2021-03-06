package calico

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point

import java.awt.Toolkit
import scala.swing.Alignment
import scala.swing.BoxPanel
import scala.swing.GridPanel
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.Swing
import scala.swing.TextField
import swing.SimpleSwingApplication

object Main extends SimpleSwingApplication {
//    def top = new MainFrame {
//        title = "Test"
//        contents = new GridPanel(1, 2) {
//            contents += new Label("Hello")
//            contents += new Label("Hello")
//        }
//        val dim = Toolkit.getDefaultToolkit.getScreenSize
//        size = new Dimension(dim.getWidth.toInt*3/4, dim.getHeight.toInt*3/4)
//        location = new Point(dim.getWidth.toInt/8, dim.getHeight.toInt/8)
//
//        pack
//    }
    def top = new CalicoFrame {
        val dim = Toolkit.getDefaultToolkit.getScreenSize
        size = new Dimension(dim.getWidth.toInt*3/4, dim.getHeight.toInt*3/4)
        location = new Point(dim.getWidth.toInt/8, dim.getHeight.toInt/8);
    }
}
