/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.AbstractAction
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTextField

class InputFileControl(modelNode: HTMLBaseInputElement?) : BaseInputControl(modelNode) {
    private val textField = JTextField()
    private val browseButton = JButton()
    var fileValue: File? = null
        private set(file) {
            field = file
            if (file == null) {
                this.textField.setText("")
            } else {
                this.textField.setText(file.absolutePath)
            }
        }

    init {
        this.layout = BoxLayout(this, BoxLayout.X_AXIS)
        val browseButton = this.browseButton
        browseButton.action = BrowseAction()
        browseButton.text = "Browse"
        val ps = this.textField.preferredSize
        this.textField.preferredSize = Dimension(128, ps.height)
        this.textField.isEditable = false
        this.add(this.textField)
        this.add(Box.createHorizontalStrut(4))
        this.add(browseButton)
    }

    var value: String?
        get() =// This is the way browsers behave, even
            // though this value is not submitted.
            this.textField.text
        set(value) {
            // nop - security
        }

    override fun setDisabled(disabled: Boolean) {
        this.browseButton.isEnabled = !disabled
    }

    override fun resetInput() {
        this.fileValue = null
    }

    private inner class BrowseAction : AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            val chooser = JFileChooser()
            if (chooser.showOpenDialog(this@InputFileControl) == JFileChooser.APPROVE_OPTION) {
                this.fileValue = chooser.selectedFile
            } else {
                this.fileValue = null
            }
        }

        companion object {
            private val serialVersionUID = -967133652737594806L
        }
    }

    companion object {
        private const val serialVersionUID = 4255784506085448850L
    }
}
