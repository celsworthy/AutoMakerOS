package celtech.appManager;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.utils.SystemUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ProjectHeader is not used except when loading legacy Project files.
 * @author tony
 */
public class ProjectHeader implements Serializable
{

    private static final long serialVersionUID = 1L;
    private final transient SimpleDateFormat formatter = new SimpleDateFormat("-hhmmss-ddMMYY");
    private String projectUUID = null;
    private StringProperty projectNameProperty = null;
    private String projectPath = null;
    private final ObjectProperty<Date> lastModifiedDate = new SimpleObjectProperty<>();

    public ProjectHeader()
    {
        projectUUID = SystemUtils.generate16DigitID();
        Date now = new Date();
        projectNameProperty = new SimpleStringProperty(Lookup.i18n("projectLoader.untitled")
            + formatter.format(now));
        projectPath = ApplicationConfiguration.getProjectDirectory();
        lastModifiedDate.set(now);
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        out.writeUTF(projectUUID);
        out.writeUTF(projectNameProperty.get());
        out.writeUTF(projectPath);
        out.writeObject(lastModifiedDate.get());
        out.writeObject(new Date());
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        projectUUID = in.readUTF();
        projectNameProperty = new SimpleStringProperty(in.readUTF());
        projectPath = in.readUTF();
        Object lastModifiedDate = new SimpleObjectProperty<>((Date)(in.readObject()));
        Object lastSavedDate = new SimpleObjectProperty<>((Date)(in.readObject()));
    }

    private void readObjectNoData()
            throws ObjectStreamException
    {

    }

   
}
