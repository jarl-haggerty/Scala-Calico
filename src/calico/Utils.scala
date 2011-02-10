/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import swing.Dialog.showMessage
import swing.Dialog.Message

object Utils {
    def copy(from : String, to : String) = {
        val f1 = new File(from)
        val f2 = new File(to)
        println("Copying from " + f1.getAbsolutePath + " to " + f2.getAbsolutePath)
        if(f1.getAbsolutePath != f2.getAbsolutePath) {
            try {
                println("Copying from " + f1 + " to " + f2)
                
                val in = new FileInputStream(f1)
                val out = new FileOutputStream(f2)
                val buf = new Array[Byte](1024)
                var len = in.read(buf)
                while (len > 0) {
                    out.write(buf, 0, len)
                    len = in.read(buf)
                }
                in.close
                out.close
            } catch {
                case e : IOException => showMessage(null, "Failed to copy file from " + from + " to " + to, "Copy Error", Message.Error)
                case e : Exception => println(e)
            }
        }
    }
}