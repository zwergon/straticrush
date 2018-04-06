package stratifx.model.filters;

import com.cedarsoftware.util.io.JsonWriter;
import fr.ifp.kronosflow.model.Section;
import stratifx.model.json.JSONSection;
import stratifx.model.persistable.PersistableSection;
import stratifx.model.wrappers.SectionWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONSectionSave {

    protected String filename;

    protected Section section;

    protected boolean formatted = true;

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public void setSection(Section section){
        this.section = section;
    }

    public Section getSection(){
        return section;
    }

    public void setFormatted(boolean formatted) {
        this.formatted = formatted;
    }

    public  boolean execute(){
        if ( null == section){
            return false;
        }

        File file = new File(filename);
        try {
            doExport(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void doExport( File file ) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        SectionWrapper wrapper = new SectionWrapper();
        wrapper.save(section);

        JSONSection jsonSection = new JSONSection((PersistableSection)wrapper.getPersisted() );

        if ( formatted ) {
            output.write(JsonWriter.formatJson(jsonSection.toJSONString()));
        }
        else {
            output.write(jsonSection.toJSONString());
        }

        output.close();
    }

}
