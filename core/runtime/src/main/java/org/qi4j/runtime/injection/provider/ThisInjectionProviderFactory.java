package org.qi4j.runtime.injection.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.qi4j.api.composite.CompositeDescriptor;
import org.qi4j.api.util.Classes;
import org.qi4j.bootstrap.InvalidInjectionException;
import org.qi4j.functional.Iterables;
import org.qi4j.runtime.injection.DependencyModel;
import org.qi4j.runtime.injection.InjectionContext;
import org.qi4j.runtime.injection.InjectionProvider;
import org.qi4j.runtime.injection.InjectionProviderFactory;
import org.qi4j.runtime.model.Resolution;

import static org.qi4j.functional.Iterables.first;

/**
 * JAVADOC
 */
public final class ThisInjectionProviderFactory
    implements InjectionProviderFactory
{
    public InjectionProvider newInjectionProvider( Resolution bindingContext, DependencyModel dependencyModel )
        throws InvalidInjectionException
    {
        if( bindingContext.model() instanceof CompositeDescriptor )
        {
            // If Composite type then return real type, otherwise use the specified one
            final Class thisType = dependencyModel.rawInjectionType();

            Iterable<Class<?>> injectionTypes = null;
            if( Classes.assignableTypeSpecification( thisType ).satisfiedBy( bindingContext.model() ) )
            {
                injectionTypes = bindingContext.model().types();
            }
            else
            {
                CompositeDescriptor acd = ( (CompositeDescriptor) bindingContext.model() );
                for( Class mixinType : acd.mixinTypes() )
                {
                    if( thisType.isAssignableFrom( mixinType ) )
                    {
                        injectionTypes = acd.types();
                        break;
                    }
                }

                if( injectionTypes == null )
                {
                    throw new InvalidInjectionException( "Composite " + bindingContext.model()
                                                         + " does not implement @This type " + thisType.getName() + " in fragment "
                                                         + dependencyModel.injectedClass().getName() );
                }
            }

            return new ThisInjectionProvider( injectionTypes );
        }
        else
        {
            throw new InvalidInjectionException( "Object " + dependencyModel.injectedClass() + " may not use @This" );
        }
    }

    private class ThisInjectionProvider
        implements InjectionProvider
    {
        Constructor proxyConstructor;
        private Class[] interfaces;

        public ThisInjectionProvider( Iterable<Class<?>> types )
        {
            try
            {
                Class proxyClass;
                if( Proxy.class.isAssignableFrom( first( types ) ) )
                {
                    proxyClass = first( types );
                }
                else
                {
                    Class<?> mainType = first( types );
                    interfaces = Iterables.toArray( Class.class, types );
                    proxyClass = Proxy.getProxyClass( mainType.getClassLoader(), interfaces );
                }

                proxyConstructor = proxyClass.getConstructor( InvocationHandler.class );
            }
            catch( Exception e )
            {
                // Ignore
                e.printStackTrace();
            }
        }

        public Object provideInjection( InjectionContext context )
        {
            try
            {
                InvocationHandler handler = context.compositeInstance();
                if( handler == null )
                {
                    handler = context.proxyHandler();
                }
                return proxyConstructor.newInstance( handler );
            }
            catch( Exception e )
            {
                throw new InjectionProviderException( "Could not instantiate @This proxy", e );
            }
        }
    }
}
