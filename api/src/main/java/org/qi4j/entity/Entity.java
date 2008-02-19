/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entity;

import java.net.URL;
import org.qi4j.composite.Composite;

public interface Entity
{
    /**
     * Cast the current object to the given interface.
     * <p/>
     * The returned object uses the current object which provides mixins
     * that should be reused for this new object.
     *
     * @param anObjectType an interface that describes the object to be created
     * @return a new composite object implementing the interface
     */
    <T extends Composite> T cast( Class<T> anObjectType )
        throws CompositeCastException;

    /**
     * Checks if the object can be cast() to the provided object type.
     *
     * @param anObjectType The object type we want to check the assignability to for this object.
     * @return true if a cast() is possible of this object to the provided object type.
     */
    boolean isInstance( Class anObjectType );

    boolean isReference();

    /**
     * The full absolute external form of the composite reference.
     * <p/>
     * The composite reference will be converted to a URL with the following
     * format;
     * <code><pre>
     * &lt;transport-protocol&gt;://&lt;repository-host&gt;/&lt;identity&gt;?type=&lt;type&gt;
     * </pre></code>
     *
     * @return A URL pointing to the potenitally globally accessible location where the composite
     *         can be retrieved.
     */
    URL toURL();
}