/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.qi4j.lib.swing.binding.adapters;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.qi4j.composite.Mixins;
import org.qi4j.lib.swing.binding.SwingAdapter;
import org.qi4j.property.Property;
import org.qi4j.service.ServiceComposite;

@Mixins( StringToTextFieldAdapterService.StringToTextFieldAdapterMixin.class )
public interface StringToTextFieldAdapterService extends SwingAdapter, ServiceComposite
{

    abstract class StringToTextFieldAdapterMixin
        implements SwingAdapter
    {

        private HashSet<Capabilities> canHandle;

        public StringToTextFieldAdapterMixin()
        {
            canHandle = new HashSet<Capabilities>();
            canHandle.add( new Capabilities( JTextArea.class, String.class, true, false, false, false ) );
            canHandle.add( new Capabilities( JTextField.class, String.class, true, false, false, false ) );
            canHandle.add( new Capabilities( JLabel.class, String.class, true, false, false, false ) );
        }

        public final Set<Capabilities> canHandle()
        {
            return canHandle;
        }

        @SuppressWarnings( "unchecked" )
        public final void fromSwingToProperty( JComponent component, Property property )
        {
            if( property == null )
            {
                return;
            }
            if( component instanceof JTextComponent )
            {
                JTextComponent textComponent = (JTextComponent) component;
                property.set( textComponent.getText() );
            }
            else
            {
                JLabel labelComponent = (JLabel) component;
                property.set( labelComponent.getText() );
            }
        }

        @SuppressWarnings( "unchecked" )
        public final void fromPropertyToSwing( JComponent component, Property property )
        {
            String value;
            if( property == null )
            {
                value = "";
            }
            else
            {
                value = (String) property.get();
            }
            if( component instanceof JTextComponent )
            {
                JTextComponent textComponent = (JTextComponent) component;
                textComponent.setText( value );
            }
            else
            {
                JLabel labelComponent = (JLabel) component;
                labelComponent.setText( value );
            }
        }
    }
}
