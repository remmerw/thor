/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package io.github.remmerw.thor.cobra.util.gui

import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.event.ActionEvent
import java.net.PasswordAuthentication
import javax.swing.AbstractAction
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder

/**
 * Dialog used in HTTP and proxy authentication.
 */
class AuthenticationDialog : JDialog {
    private val userNameField = JTextField()
    private val passwordField = JPasswordField()
    var authentication: PasswordAuthentication? = null
        private set

    constructor(owner: Frame?) : super(owner) {
        this.init()
    }

    constructor(owner: Dialog?) : super(owner) {
        this.init()
    }

    private fun init() {
        val contentPane = this.contentPane
        contentPane.layout = FlowLayout()

        val rootBox = Box(BoxLayout.Y_AXIS)
        rootBox.border = EmptyBorder(4, 4, 4, 4)

        val userNameBox = Box(BoxLayout.X_AXIS)
        val userNameLabel = JLabel("User name:")
        val unph = userNameLabel.preferredSize.height
        userNameLabel.preferredSize = Dimension(100, unph)
        userNameLabel.horizontalAlignment = SwingConstants.RIGHT
        userNameBox.add(userNameLabel)
        userNameBox.add(Box.createRigidArea(Dimension(4, 1)))
        userNameBox.add(this.userNameField)
        userNameBox.preferredSize = Dimension(300, unph + 4)

        val passwordBox = Box(BoxLayout.X_AXIS)
        val passwordLabel = JLabel("Password:")
        val pwph = passwordLabel.preferredSize.height
        passwordLabel.preferredSize = Dimension(100, pwph)
        passwordLabel.horizontalAlignment = SwingConstants.RIGHT
        passwordBox.add(passwordLabel)
        passwordBox.add(Box.createRigidArea(Dimension(4, 1)))
        passwordBox.add(this.passwordField)
        passwordBox.preferredSize = Dimension(300, pwph + 4)

        val buttonBox = Box(BoxLayout.X_AXIS)
        val okButton = JButton()
        okButton.action = OkAction()
        okButton.text = "OK"
        val cancelButton = JButton()
        cancelButton.action = CancelAction()
        cancelButton.text = "Cancel"
        buttonBox.add(Box.createHorizontalGlue())
        buttonBox.add(okButton)
        buttonBox.add(Box.createHorizontalStrut(4))
        buttonBox.add(cancelButton)
        buttonBox.add(Box.createHorizontalGlue())

        rootBox.add(userNameBox)
        rootBox.add(Box.createVerticalStrut(2))
        rootBox.add(passwordBox)
        rootBox.add(Box.createVerticalStrut(4))
        rootBox.add(buttonBox)

        contentPane.add(rootBox)
    }

    fun setUserName(userName: String?) {
        this.userNameField.text = userName
        this.passwordField.grabFocus()
    }

    private inner class OkAction : AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            authentication =
                PasswordAuthentication(userNameField.text, passwordField.password)
            this@AuthenticationDialog.dispose()
        }

        companion object {
            private const val serialVersionUID = 3308644732677944619L
        }
    }

    private inner class CancelAction : AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            authentication = null
            this@AuthenticationDialog.dispose()
        }

        companion object {
            private const val serialVersionUID = 703637268854289240L
        }
    }

    companion object {
        private const val serialVersionUID = 5601837809153264164L
    }
}
