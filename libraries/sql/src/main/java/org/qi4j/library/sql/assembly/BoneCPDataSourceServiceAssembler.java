/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
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
package org.qi4j.library.sql.assembly;

import org.qi4j.api.common.Visibility;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.library.sql.datasource.BoneCPDataSourceServiceImporter;

/**
 * Use this Assembler to register a DataSourceService based on BoneCP and its Configuration entity.
 */
public class BoneCPDataSourceServiceAssembler
        extends AbstractPooledDataSourceServiceAssembler
{

    public BoneCPDataSourceServiceAssembler( String dataSourceServiceId, Visibility visibility, ModuleAssembly configModuleAssembly, Visibility configVisibility )
    {
        super( dataSourceServiceId, visibility, configModuleAssembly, configVisibility );
    }

    @Override
    protected void onAssemble( ModuleAssembly module )
    {
        module.services( BoneCPDataSourceServiceImporter.class ).identifiedBy( dataSourceServiceId ).visibleIn( visibility );
    }

}