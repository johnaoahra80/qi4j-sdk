/*
 * Copyright 2011 Marc Grue.
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
package org.qi4j.sample.dcicargo.sample_b.communication.web.tracking;

import java.text.SimpleDateFormat;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.ValueMap;
import org.qi4j.sample.dcicargo.sample_b.communication.query.dto.CargoDTO;
import org.qi4j.sample.dcicargo.sample_b.data.structure.delivery.NextHandlingEvent;
import org.qi4j.sample.dcicargo.sample_b.data.structure.handling.HandlingEvent;
import org.qi4j.sample.dcicargo.sample_b.data.structure.handling.HandlingEventType;
import org.qi4j.sample.dcicargo.sample_b.data.structure.location.Location;

/**
 * Next handling event
 *
 * Quite some presentation logic to render 1 line of information!
 */
@StatelessComponent
public class NextHandlingEventPanel extends Panel
{
    public NextHandlingEventPanel( String id, IModel<CargoDTO> cargoModel )
    {
        super( id );

        ValueMap map = new ValueMap();
        Label label = new Label( "text", new StringResourceModel(
            "nextEvent.${nextEvent}", this, new Model<ValueMap>( map ) ) );
        add( label );

        CargoDTO cargo = cargoModel.getObject();
        Location destination = cargo.routeSpecification().get().destination().get();

        if( cargo.itinerary().get() == null )
        {
            map.put( "nextEvent", "ROUTE" );
            return;
        }

        HandlingEvent previousEvent = cargo.delivery().get().lastHandlingEvent().get();
        if( previousEvent == null )
        {
            map.put( "nextEvent", "RECEIVE" );
            map.put( "location", cargo.routeSpecification().get().origin().get().getString() );
            return;
        }

        Location lastLocation = previousEvent.location().get();
        if( previousEvent.handlingEventType().get() == HandlingEventType.CLAIM && lastLocation == destination )
        {
            map.put( "nextEvent", "END_OF_CYCLE" );
            map.put( "location", destination.getString() );
            label.add( new AttributeModifier( "class", "correctColor" ) );
            return;
        }

        NextHandlingEvent nextEvent = cargo.delivery().get().nextHandlingEvent().get();
        if( nextEvent == null )
        {
            map.put( "nextEvent", "UNKNOWN" );
            label.add( new AttributeModifier( "class", "errorColor" ) );
            return;
        }

        map.put( "nextEvent", nextEvent.handlingEventType().get().name() );
        map.put( "location", nextEvent.location().get().getString() );

        if( nextEvent.time() != null )
        {
            map.put( "time", new SimpleDateFormat( "yyyy-MM-dd" ).format( nextEvent.time().get() ) );
        }

        if( nextEvent.voyage().get() != null )
        {
            map.put( "voyage", nextEvent.voyage().get().voyageNumber().get().number().get() );
        }
    }
}
