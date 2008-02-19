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
package org.qi4j.spi.entity;

import java.util.Map;
import org.qi4j.entity.EntityComposite;
import org.qi4j.entity.EntitySession;
import org.qi4j.spi.property.AssociationBinding;
import org.qi4j.spi.property.PropertyBinding;

public interface EntityStore
{
    boolean exists( String identity, Class<? extends EntityComposite> compositeType ) throws StoreException;

    EntityState newEntityInstance( EntitySession session, String identity, Class compositeType, Iterable<PropertyBinding> propertyBindings, Iterable<AssociationBinding> associationBindings, Map<String, Object> propertyValues ) throws StoreException;

    EntityState getEntityInstance( EntitySession session, String identity, Class compositeType, Iterable<PropertyBinding> propertyBindings, Iterable<AssociationBinding> associationBindings ) throws StoreException;

    /**
     * Delete the entity with the given identity from the store.
     *
     * @param identity The identity of the entity to be deleted from the store.
     * @return true if an entity was removed, otherwise false.
     * @throws StoreException if there is a physical problem with the connection to the backing store.
     */
    boolean delete( String identity, Class compositeType )
        throws StoreException;
}