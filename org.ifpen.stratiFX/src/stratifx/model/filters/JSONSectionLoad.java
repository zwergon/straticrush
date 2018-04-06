package stratifx.model.filters;

import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stratifx.model.loader.AbstractLoader;
import stratifx.model.persistable.PersistableSection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSONSectionLoad {


    protected String filename;

    protected Section section;

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public Section getSection() {
        return section;
    }

    public boolean execute( ){
        if (filename == null) {
            return false;
        }

        File file = new File(filename);
        if ( !file.exists() ){
            return false;
        }

        try {
            doExecute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void doExecute() throws Exception {

        JSONParser parser = new JSONParser();

        try {

            JSONObject jsonSection = (JSONObject)parser.parse(new FileReader(filename));

            PersistableSection persistableSection = (PersistableSection)AbstractLoader.loadObject(jsonSection);

            section = (Section)WrapperFactory.build( (String)jsonSection.get("persistedClass"));

            WrapperFactory.load(section, persistableSection);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
