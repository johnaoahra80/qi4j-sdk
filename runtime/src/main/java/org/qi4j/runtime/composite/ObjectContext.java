/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.composite;

import java.util.Map;
import java.util.Set;
import org.qi4j.association.AbstractAssociation;
import org.qi4j.property.Property;
import org.qi4j.runtime.property.AssociationContext;
import org.qi4j.runtime.property.PropertyContext;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.spi.composite.ObjectBinding;
import org.qi4j.spi.composite.ObjectModel;
import org.qi4j.spi.composite.ObjectResolution;
import org.qi4j.spi.injection.ObjectInjectionContext;
import org.qi4j.spi.structure.ModuleBinding;

/**
 * TODO
 */
public final class ObjectContext
{
    private ObjectBinding objectBinding;
    private InstanceFactory instanceFactory;
    private ModuleBinding moduleBinding;

    private Map<String, PropertyContext> propertyContexts;
    private Map<String, AssociationContext> associationContexts;

    public ObjectContext( ObjectBinding objectBinding, ModuleBinding moduleBinding, InstanceFactory instanceFactory, Map<String, PropertyContext> propertyContexts, Map<String, AssociationContext> associationContexts )
    {
        this.associationContexts = associationContexts;
        this.propertyContexts = propertyContexts;
        this.moduleBinding = moduleBinding;
        this.objectBinding = objectBinding;
        this.instanceFactory = instanceFactory;
    }

    public ObjectModel getObjectModel()
    {
        return objectBinding.getObjectResolution().getObjectModel();
    }

    public ObjectResolution getObjectResolution()
    {
        return objectBinding.getObjectResolution();
    }

    public ObjectBinding getObjectBinding()
    {
        return objectBinding;
    }

    public ModuleBinding getModuleBinding()
    {
        return moduleBinding;
    }

    public Iterable<PropertyContext> getPropertyContexts()
    {
        return propertyContexts.values();
    }

    public Iterable<AssociationContext> getAssociationContexts()
    {
        return associationContexts.values();
    }


    public Object newObjectInstance( ModuleInstance moduleInstance, Set adapt, Object decoratedObject, Map<String, Property> objectProperties, Map<String, AbstractAssociation> objectAssociations )
    {
        ObjectInjectionContext objectInjectionContext = new ObjectInjectionContext( moduleInstance.getCompositeBuilderFactory(), moduleInstance.getObjectBuilderFactory(), moduleInstance.getServiceRegistry(), moduleBinding, adapt, decoratedObject, objectProperties, objectAssociations );
        Object objectInstance = instanceFactory.newInstance( objectBinding, objectInjectionContext );

        // Return
        return objectInstance;
    }

    public void inject( Object instance, ModuleInstance moduleInstance, Set<Object> adaptContext, Object decoratedObject, Map<String, Property> objectProperties, Map<String, AbstractAssociation> objectAssociations )
    {
        ObjectInjectionContext objectInjectionContext = new ObjectInjectionContext( moduleInstance.getCompositeBuilderFactory(), moduleInstance.getObjectBuilderFactory(), moduleInstance.getServiceRegistry(), moduleBinding, adaptContext, decoratedObject, objectProperties, objectAssociations );
        instanceFactory.inject( instance, objectBinding, objectInjectionContext );
    }
}