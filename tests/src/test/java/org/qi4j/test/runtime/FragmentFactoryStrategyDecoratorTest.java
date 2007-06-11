/*
 * Copyright 2007 Rickard Öberg
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qi4j.test.runtime;

import junit.framework.TestCase;
import org.qi4j.api.ObjectFactory;
import org.qi4j.test.model.StandardThis;
import org.qi4j.test.model.StandardThisImpl;
import org.qi4j.runtime.FragmentFactoryStrategyDecorator;
import org.qi4j.runtime.FragmentFactoryImpl;
import org.qi4j.runtime.ObjectFactoryImpl;

public class FragmentFactoryStrategyDecoratorTest extends TestCase
{
    FragmentFactoryStrategyDecorator fragmentFactoryStrategyDecorator;

    public void testNewInstance() throws Exception
    {
        FragmentFactoryStrategyDecorator strategy = new FragmentFactoryStrategyDecorator( new FragmentFactoryImpl() );
        ObjectFactory factory = new ObjectFactoryImpl( strategy );

        strategy.addMapping( StandardThis.class, StandardThisImpl.class );

        StandardThis object = factory.newInstance( StandardThis.class );

        System.out.println( object.bar() );
    }
}