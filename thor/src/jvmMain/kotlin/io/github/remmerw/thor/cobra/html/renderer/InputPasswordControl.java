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
/*
 * Created on Jan 15, 2006
 */
package io.github.remmerw.thor.cobra.html.renderer;

import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;

import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement;

class InputPasswordControl extends InputTextControl {
    private static final long serialVersionUID = -5595867004382157867L;

    public InputPasswordControl(final HTMLBaseInputElement modelNode) {
        super(modelNode);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.xamjwg.html.renderer.InputTextControl#createTextField(java.lang.String)
     */
    @Override
    protected JTextComponent createTextField() {
        return new JPasswordField();
    }
}
