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

package org.qi4j.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.qi4j.composite.Composite;
import org.qi4j.runtime.composite.CompositeModelFactory;
import org.qi4j.runtime.composite.ObjectModelFactory;
import org.qi4j.spi.service.ServiceInstanceProvider;
import org.qi4j.spi.structure.CompositeDescriptor;
import org.qi4j.spi.structure.ObjectDescriptor;
import org.qi4j.spi.structure.ServiceDescriptor;
import org.qi4j.spi.structure.Visibility;

/**
 * TODO
 */
public final class ModuleAssembly
{
    private LayerAssembly layerAssembly;
    private String name;
    private List<CompositeDeclaration> compositeDeclarations;
    private List<ObjectDeclaration> objectDeclarations;
    private List<ServiceDeclaration> serviceDeclarations;
    private List<PropertyDeclaration> propertyDeclarations;
    private List<AssociationDeclaration> associationDeclarations;

    public ModuleAssembly( LayerAssembly layerAssembly )
    {
        this.layerAssembly = layerAssembly;
        compositeDeclarations = new ArrayList<CompositeDeclaration>();
        objectDeclarations = new ArrayList<ObjectDeclaration>();
        serviceDeclarations = new ArrayList<ServiceDeclaration>();
        propertyDeclarations = new ArrayList<PropertyDeclaration>();
        associationDeclarations = new ArrayList<AssociationDeclaration>();
    }

    public void addAssembly( Assembly assembly )
        throws AssemblyException
    {
        // Invoke Assembly callback
        assembly.configure( this );
    }

    public LayerAssembly getLayerAssembly()
    {
        return layerAssembly;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public CompositeDeclaration addComposites( Class<? extends Composite>... compositeTypes )
    {
        CompositeDeclaration compositeDeclaration = new CompositeDeclaration( compositeTypes );
        compositeDeclarations.add( compositeDeclaration );
        return compositeDeclaration;
    }

    public ObjectDeclaration addObjects( Class... objectTypes )
    {
        ObjectDeclaration objectDeclaration = new ObjectDeclaration( Arrays.asList( objectTypes ) );
        objectDeclarations.add( objectDeclaration );
        return objectDeclaration;
    }

    public ServiceDeclaration addServices( Class<? extends ServiceInstanceProvider> serviceProvider, Class... serviceTypes )
    {
        ServiceDeclaration serviceDeclaration = new ServiceDeclaration( serviceProvider, Arrays.asList( serviceTypes ) );
        serviceDeclarations.add( serviceDeclaration );
        return serviceDeclaration;
    }

    public PropertyDeclaration addProperty()
    {
        PropertyDeclaration declaration = new PropertyDeclaration();
        propertyDeclarations.add( declaration );
        return declaration;
    }

    public AssociationDeclaration addAssociation()
    {
        AssociationDeclaration declaration = new AssociationDeclaration();
        associationDeclarations.add( declaration );
        return declaration;
    }

    List<CompositeDescriptor> getCompositeDescriptors( CompositeModelFactory compositeModelFactory )
    {
        List<CompositeDescriptor> compositeDescriptors = new ArrayList<CompositeDescriptor>();
        for( CompositeDeclaration compositeDeclaration : compositeDeclarations )
        {
            compositeDescriptors.addAll( compositeDeclaration.getCompositeDescriptors( compositeModelFactory ) );
        }
        return compositeDescriptors;
    }

    Iterable<ObjectDescriptor> getObjectDescriptors( ObjectModelFactory objectModelFactory )
    {
        List<ObjectDescriptor> objectDescriptors = new ArrayList<ObjectDescriptor>();
        for( ObjectDeclaration objectDeclaration : objectDeclarations )
        {
            objectDescriptors.addAll( objectDeclaration.getObjectDescriptors( objectModelFactory ) );
        }

        for( ServiceDescriptor serviceDescriptor : getServiceDescriptors() )
        {
            objectDescriptors.add( new ObjectDescriptor( objectModelFactory.newObjectModel( serviceDescriptor.getServiceProvider() ), Collections.emptySet(), Visibility.module ) );
        }
        return objectDescriptors;
    }

    Iterable<ServiceDescriptor> getServiceDescriptors()
    {
        List<ServiceDescriptor> serviceDescriptors = new ArrayList<ServiceDescriptor>();
        for( ServiceDeclaration serviceDeclaration : serviceDeclarations )
        {
            serviceDescriptors.addAll( serviceDeclaration.getServiceDescriptors() );
        }
        return serviceDescriptors;
    }

    List<PropertyDeclaration> getPropertyDeclarations()
    {
        return propertyDeclarations;
    }

    List<AssociationDeclaration> getAssociationDeclarations()
    {
        return associationDeclarations;
    }

    String getName()
    {
        return name;
    }
}