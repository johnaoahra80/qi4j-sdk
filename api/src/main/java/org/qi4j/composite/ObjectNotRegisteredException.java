/*
 * Copyright (c) 2008, Niclas Hedhman. All Rights Reserved.
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
package org.qi4j.composite;

import org.qi4j.structure.Module;

public class ObjectNotRegisteredException extends InvalidApplicationException
{
    private static final long serialVersionUID = -1121690536365682511L;

    private Class<?> objectType;
    private Module module;

    public ObjectNotRegisteredException( Class<?> type, Module module )
    {
        super( "Trying to find unregistered object of type [" + type.getName() + "] in module [" +
               module.name().get() + "]." );
        this.objectType = type;
        this.module = module;
    }

    public Class<?> objectType()
    {
        return objectType;
    }

    public Module module()
    {
        return module;
    }
}