package stratifx.model.loader;

import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableSection;

public class SectionLoader extends AbstractLoader {


    @Override
    public IPersisted create() {
        return new PersistableSection();
    }

    @Override
    public IPersisted load(JSONObject jsonSection ) {

        PersistableSection persistableSection = (PersistableSection)data;

        persistableSection.setGeologicLibrary(AbstractLoader.loadSubObject(jsonSection, "geologicalLib"));
        persistableSection.setDomainReference(AbstractLoader.loadSubObject(jsonSection, "referencedomain"));
        persistableSection.setPaleobathymetry(AbstractLoader.loadSubObject(jsonSection, "paleobathymetry"));
        persistableSection.setPatches( AbstractLoader.loadObjectArray(jsonSection, "patches") );

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());

        return data;
    }



}
