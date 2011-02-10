/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calico

import javax.swing.JToolBar
import scala.swing.Component

class Toolbar extends Component {
    override lazy val peer: JToolBar = new JToolBar with SuperMixin

    
}
