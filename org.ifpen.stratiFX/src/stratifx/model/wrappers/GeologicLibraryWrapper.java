package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import stratifx.model.persistable.*;

import java.util.ArrayList;
import java.util.List;

public class GeologicLibraryWrapper implements IWrapper<GeologicLibrary> {

    PersistableGeologicLib persistableGeologicLib;

    @Override
    public void setPersisted(Object persisted) {
        this.persistableGeologicLib = (PersistableGeologicLib)persisted;
    }

    @Override
    public Object getPersisted() {
        return persistableGeologicLib;
    }

    @Override
    public boolean load(GeologicLibrary wrapped) {
        if (null == persistableGeologicLib) {
            return false;
        }

        wrapped.setName( persistableGeologicLib.getName() );
        wrapped.setUID( persistableGeologicLib.getUid() );

        for( IPersisted persistedFeature : persistableGeologicLib.getGeologicFeatures() ){

            GeologicFeature feature = (GeologicFeature)WrapperFactory.build(persistedFeature.getPersistedClass());
            feature.setName( persistedFeature.getName() );
            feature.setUID( persistedFeature.getUid() );
            feature.setAwtcolor( ((PersistableGeologicFeature)persistedFeature).getAwtColor() );

            wrapped.addGeologicFeature(feature);

        }


        PersistableStratiColumn persistedColumn = (PersistableStratiColumn)persistableGeologicLib.getStratiColumn();
        StratigraphicColumn column = (StratigraphicColumn)WrapperFactory.build(persistedColumn.getPersistedClass());
        wrapped.add( column );

        for ( IPersisted persisted : persistedColumn.getUnits() ){

            PersistableUnit persistedUnit =(PersistableUnit)persisted;

            IPersisted persistedTopEvent = persistedUnit.getTopEvent();

            StratigraphicEvent event = (StratigraphicEvent)WrapperFactory.build(persistedTopEvent.getPersistedClass());
            event.setUID( persistedTopEvent.getUid() );
            event.setName( persistedTopEvent.getName() );

            StratigraphicUnit unit = column.appendUnit(event);
            unit.setUID( persistedUnit.getUid() );
            unit.setAwtcolor(((PersistableUnit) persisted).getAwtColor() );

            wrapped.addGeologicFeature(event);

        }

        //so far units are recreated and not reassigned to persisted ones.


        return true;

    }

    @Override
    public boolean save(GeologicLibrary geologicLibrary) {

        persistableGeologicLib.setGeologicFeatures( extractGeologicFeature( geologicLibrary ) );

        StratigraphicColumn stratiColumn = geologicLibrary.findObject( StratigraphicColumn.class);
        PersistableStratiColumn persistableStrati = new PersistableStratiColumn(stratiColumn);
        persistableStrati.setUnits( extractUnits(stratiColumn) );

        persistableGeologicLib.setStratiColumn( persistableStrati );

        return true;
    }

    public List<IPersisted> extractGeologicFeature(GeologicLibrary geologicLibrary ){
        List<IPersisted> persisted = new ArrayList<>();

        for(GeologicFeature feature : geologicLibrary.getGeologicFeatures() ){
            if ( (feature instanceof StratigraphicUnit) || feature instanceof StratigraphicEvent ){
                continue;
            }
            PersistableGeologicFeature persistableGeologicFeature = new PersistableGeologicFeature(feature);
            persistableGeologicFeature.setAwtColor( feature.getAwtcolor() );
            persistableGeologicFeature.setGeologicType( feature.getGeologicalType() );

            if ( feature instanceof BoundaryFeature ){
                persistableGeologicFeature.setExtendable( ((BoundaryFeature) feature).isExtendable() );
            }

            persisted.add( persistableGeologicFeature );
        }

        return persisted;
    }

    public List<IPersisted> extractUnits( StratigraphicColumn column ){

        List<IPersisted> persistedUnits = new ArrayList<>();


        for( StratigraphicUnit unit : column.units() ){
            PersistableUnit persistableUnit = new PersistableUnit(unit);
            persistableUnit.setAwtColor(unit.getAwtcolor());
            persistableUnit.setGeologicType(unit.getGeologicalType());

            StratigraphicEvent topEvent = unit.getTop();
            PersistableGeologicFeature persistedTopEvent = new PersistableGeologicFeature(topEvent);
            persistedTopEvent.setGeologicType(topEvent.getGeologicalType());
            persistedTopEvent.setAwtColor(topEvent.getAwtcolor());
            persistableUnit.setTopEvent(persistedTopEvent);

            persistedUnits.add(persistableUnit);
        }

        return persistedUnits;
    }

    public List<IPersisted> extractEvents( StratigraphicColumn column ){

        List<IPersisted> persistedEvents = new ArrayList<>();
        for( StratigraphicEvent event : column.events() ){
            PersistableGeologicFeature persistableGeologicFeature = new PersistableGeologicFeature(event);
            persistableGeologicFeature.setAwtColor(event.getAwtcolor());
            persistableGeologicFeature.setGeologicType(event.getGeologicalType());
            persistableGeologicFeature.setExtendable(event.isExtendable());

            persistedEvents.add(persistableGeologicFeature);
        }

        return persistedEvents;
    }
}
