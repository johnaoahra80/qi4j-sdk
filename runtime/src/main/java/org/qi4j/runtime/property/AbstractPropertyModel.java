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

package org.qi4j.runtime.property;

import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.common.QualifiedName;
import static org.qi4j.api.common.TypeName.nameOf;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.constraint.ConstraintViolation;
import org.qi4j.api.constraint.ConstraintViolationException;
import org.qi4j.api.entity.Queryable;
import org.qi4j.api.entity.RDF;
import org.qi4j.api.property.*;
import org.qi4j.api.util.SerializationUtil;
import org.qi4j.runtime.composite.BindingException;
import org.qi4j.runtime.composite.ConstraintsCheck;
import org.qi4j.runtime.composite.Resolution;
import org.qi4j.runtime.composite.ValueConstraintsInstance;
import org.qi4j.runtime.structure.Binder;
import org.qi4j.spi.property.PropertyDescriptor;
import org.qi4j.spi.property.PropertyType;
import org.qi4j.spi.value.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JAVADOC
 */
public abstract class AbstractPropertyModel
    implements Serializable, PropertyDescriptor, ConstraintsCheck, Binder
{
    private static final long serialVersionUID = 1L;

    private final Type type;

    private transient Method accessor; // Interface accessor

    private final QualifiedName qualifiedName;

    private final String rdf;

    private final ValueConstraintsInstance constraints; // May be null

    protected final MetaInfo metaInfo;

    private final Object initialValue;

    private final boolean useDefaults;

    private final boolean immutable;

    private final boolean computed;

    private final boolean needsWrapper;

    private final PropertyInfo builderInfo;

    public AbstractPropertyModel( Method accessor, boolean immutable, ValueConstraintsInstance constraints,
                                  MetaInfo metaInfo, Object initialValue )
    {
        this.immutable = immutable;
        this.metaInfo = metaInfo;
        type = GenericPropertyInfo.getPropertyType( accessor );
        this.accessor = accessor;
        qualifiedName = QualifiedName.fromMethod(accessor);

        // Check for @UseDefaults annotation
        useDefaults = this.metaInfo.get( UseDefaults.class ) != null;

        this.initialValue = initialValue;

        RDF uriAnnotation = this.metaInfo.get( RDF.class );
        rdf = uriAnnotation == null ? null : uriAnnotation.value();

        this.constraints = constraints;

        computed = this.metaInfo.get( Computed.class ) != null;
        needsWrapper = !this.accessor.getReturnType().equals( Property.class );

        builderInfo = new GenericPropertyInfo( this.metaInfo, false, computed, qualifiedName, type );
    }

    protected ValueType createValueType( Type type )
    {
        ValueType valueType;
        if( CollectionType.isCollection( type ) )
        {
            if( type instanceof ParameterizedType )
            {
                ParameterizedType pt = (ParameterizedType) type;
                valueType = new CollectionType( nameOf( type ), createValueType( pt.getActualTypeArguments()[ 0 ] ) );
            }
            else
            {
                valueType = new CollectionType( nameOf( type ), createValueType( Object.class ) );
            }
        }
        else if( ValueCompositeType.isValueComposite( type ) )
        {
            Class valueTypeClass = (Class) type;
            List<PropertyType> types = new ArrayList<PropertyType>();
            for( Method method : valueTypeClass.getMethods() )
            {
                Type returnType = method.getGenericReturnType();
                if( returnType instanceof ParameterizedType && ( (ParameterizedType) returnType ).getRawType().equals( Property.class ) )
                {
                    Type propType = ( (ParameterizedType) returnType ).getActualTypeArguments()[ 0 ];
                    RDF rdfAnnotation = method.getAnnotation( RDF.class );
                    String rdf = rdfAnnotation == null ? null : rdfAnnotation.value();
                    Queryable queryableAnnotation = method.getAnnotation( Queryable.class );
                    boolean queryable = queryableAnnotation == null || queryableAnnotation.value();
                    PropertyType propertyType = new PropertyType(QualifiedName.fromMethod(method), createValueType(propType), rdf, queryable, PropertyType.PropertyTypeEnum.IMMUTABLE);
                    types.add( propertyType );
                }
            }
            valueType = new ValueCompositeType( nameOf(valueTypeClass), types );
        }
        else if( StringType.isString( type ) )
        {
            valueType = new StringType( nameOf(type ) );
        }
        else if( NumberType.isNumber( type ) )
        {
            valueType = new NumberType( nameOf(type ) );
        }
        else if( BooleanType.isBoolean(type ) )
        {
            valueType = new BooleanType( nameOf(type ) );
        }
        else
        {
            // TODO: shouldn't we check that the type is a Serializable?
            valueType = new SerializableType( nameOf( type ) );
        }

        return valueType;
    }

    public <T> T metaInfo( Class<T> infoType )
    {
        return metaInfo.get( infoType );
    }

    public String name()
    {
        return qualifiedName.name();
    }

    public QualifiedName qualifiedName()
    {
        return qualifiedName;
    }

    public Type type()
    {
        return type;
    }

    public Method accessor()
    {
        return accessor;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isComputed()
    {
        return computed;
    }

    public Object initialValue()
    {
        Object value = initialValue;

        // Check for @UseDefaults annotation
        if( value == null && useDefaults )
        {
            value = DefaultValues.getDefaultValue( type );
        }

        return value;
    }

    public String toRDF()
    {
        return rdf;
    }

    public void bind( Resolution resolution ) throws BindingException
    {
        // TODO Select ValueComposite type
    }

    public Property<?> newBuilderInstance()
    {
        // Properties cannot be immutable during construction

        Property<?> property;
        if( computed )
        {
            property = new ComputedPropertyInfo<Object>( builderInfo );
        }
        else
        {
            property = new PropertyInstance<Object>( builderInfo, initialValue(), this );
        }

        return wrapProperty( property );
    }

    public Property<?> newBuilderInstance( Object initialValue )
    {
        // Properties cannot be immutable during construction

        Property<?> property;
        if( computed )
        {
            property = new ComputedPropertyInfo<Object>( builderInfo );
        }
        else
        {
            property = new PropertyInstance<Object>( builderInfo, initialValue, this );
        }

        return wrapProperty( property );
    }

    public Property<?> newInitialInstance()
    {
        // Construct instance without using a builder

        return newInstance( initialValue() );
    }

    public abstract <T> Property<T> newInstance( Object value );

    public void checkConstraints(Object value) throws ConstraintViolationException
    {
        if( constraints != null )
        {
            List<ConstraintViolation> violations = constraints.checkConstraints( value );
            if( !violations.isEmpty() )
            {
                throw new ConstraintViolationException( accessor, violations );
            }
        }
    }

    public void checkConstraints( PropertiesInstance properties)
        throws ConstraintViolationException
    {
        if( constraints != null )
        {
            Object value = properties.getProperty(accessor).get();

            checkConstraints(value);
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        AbstractPropertyModel that = (AbstractPropertyModel) o;

        if( !accessor.equals( that.accessor ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return accessor.hashCode();
    }

    @Override
    public String toString()
    {
        return accessor.toGenericString();
    }

    protected <T> Property<T> wrapProperty( Property<T> property )
    {
        if( needsWrapper && !accessor.getReturnType().isInstance( property ) )
        {
            // Create proxy
            final ClassLoader loader = accessor.getReturnType().getClassLoader();
            final Class[] type = { accessor.getReturnType() };
            property = (Property<T>) Proxy.newProxyInstance( loader, type, new PropertyHandler( property ) );
        }
        return property;
    }

    private void writeObject( ObjectOutputStream out )
        throws IOException
    {
        out.defaultWriteObject();
        try
        {
            SerializationUtil.writeMethod( out, accessor );
        }
        catch( NotSerializableException e )
        {
            System.err.println( "NotSerializable in " + getClass() );
            throw e;
        }
    }

    private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        accessor = SerializationUtil.readMethod( in );
    }

    protected static class ComputedPropertyInfo<T>
        extends ComputedPropertyInstance<T>
    {
        public ComputedPropertyInfo( PropertyInfo aPropertyInfo )
            throws IllegalArgumentException
        {
            super( aPropertyInfo );
        }

        public T get()
        {
            throw new IllegalStateException( "Property [" + qualifiedName().name() + "] must be computed" );
        }
    }

    static class PropertyHandler
        implements InvocationHandler
    {
        Property p;

        public PropertyHandler( Property<?> property )
        {
            p = property;
        }

        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            try
            {
                return method.invoke( p, args );
            }
            catch( InvocationTargetException e )
            {
                throw e.getCause();
            }
        }
    }
}