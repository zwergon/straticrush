package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import fr.ifp.kronosflow.utils.LOGGER;
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


        for( IPersisted persistedFeature : persistableGeologicLib.getGeologicFeatures() ){

            GeologicFeature feature = (GeologicFeature)WrapperFactory.build(persistedFeature.getPersistedClass());
            if ( null != feature ) {

                if ( ( feature instanceof UnassignedBodyFeature ) ||  ( feature instanceof UnassignedBoundaryFeature ) ){
                    GeologicFeature unassignedFeature = wrapped.findObject(feature.getClass());
                    unassignedFeature.setUID( persistedFeature.getUid() );
                    unassignedFeature.setName( persistedFeature.getName() );
                    unassignedFeature.setRgbColor( ((PersistableGeologicFeature)persistedFeature).getRgbColor() );
                    continue;
                }


                feature.setName(persistedFeature.getName());
                feature.setUID(persistedFeature.getUid());
                feature.setRgbColor(((PersistableGeologicFeature) persistedFeature).getRgbColor());

                wrapped.addGeologicFeature(feature);
            }
            else {
                LOGGER.warning("unable to load feature " + persistedFeature.getPersistedClass(), getClass() );
            }

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
            unit.setRgbColor(((PersistableUnit) persisted).getRgbColor() );

            wrapped.addGeologicFeature(event);

        }




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
            persistableGeologicFeature.setRgbColor( feature.getRgbColor() );
            persisted.add( persistableGeologicFeature );
        }

        //Stratigraphic units where skipped, special treatment for Unassigned Body Feature
        StratigraphicUnit unassignedBody = (StratigraphicUnit)geologicLibrary.getUnassignedBodyFeature();
        PersistableGeologicFeature persistableGeologicFeature = new PersistableGeologicFeature(unassignedBody);
        persistableGeologicFeature.setRgbColor( unassignedBody.getRgbColor() );
        persisted.add( persistableGeologicFeature );

        return persisted;
    }

    public List<IPersisted> extractUnits( StratigraphicColumn column ){

        List<IPersisted> persistedUnits = new ArrayList<>();


        for( StratigraphicUnit unit : column.units() ){
            PersistableUnit persistableUnit = new PersistableUnit(unit);
            persistableUnit.setRgbColor(unit.getRgbColor());


            StratigraphicEvent topEvent = unit.getTop();
            if ( topEvent != null ) {
                PersistableGeologicFeature persistedTopEvent = new PersistableGeologicFeature(topEvent);
                persistedTopEvent.setRgbColor(topEvent.getRgbColor());
                persistableUnit.setTopEvent(persistedTopEvent);

                persistedUnits.add(persistableUnit);
            }
        }

        return persistedUnits;
    }

    public List<IPersisted> extractEvents( StratigraphicColumn column ){

        List<IPersisted> persistedEvents = new ArrayList<>();
        for( StratigraphicEvent event : column.events() ){
            PersistableGeologicFeature persistableGeologicFeature = new PersistableGeologicFeature(event);
            persistableGeologicFeature.setRgbColor(event.getRgbColor());
            persistedEvents.add(persistableGeologicFeature);
        }

        return persistedEvents;
    }
}
