package Repository;

import Domain.Identifiable;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class XMLRepository<ID, T extends Identifiable<ID>> extends FileRepository<ID, T> {

    protected XMLRepository(String xmlFilePath) {
        super(xmlFilePath); // this will set fileName and call readFromFile()
    }

    // Implement in concrete subclasses to provide JAXB element type
    protected abstract Class<T> getEntityClass();

    // Wrapper for a list root for JAXB
    @XmlRootElement(name = "entities")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class XMLWrapper {
        @XmlElement(name = "entity")
        public List<Object> items = new ArrayList<>();
    }



    @Override
    protected void readFromFile() {
        File file = new File(this.fileName);
        if (!file.exists()) {
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(XMLWrapper.class, getEntityClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XMLWrapper wrapper = (XMLWrapper) unmarshaller.unmarshal(file);

            if (wrapper != null && wrapper.items != null) {
                ((MemoryRepository<ID, T>) this).elements.clear();

                for (Object obj : wrapper.items) {
                    // Each obj is now properly typed
                    T entity = (T) obj;
                    ((MemoryRepository<ID, T>) this).elements.put(entity.getID(), entity);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read XML data from " + fileName + ": " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeToFile() {
        File file = new File(this.fileName);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            JAXBContext context = JAXBContext.newInstance(XMLWrapper.class, getEntityClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            XMLWrapper wrapper = new XMLWrapper();

            for (T e : super.getAll()) {
                wrapper.items.add(e);
            }

            marshaller.marshal(wrapper, file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write XML data to " + fileName + ": " + e.getMessage(), e);
        }
    }


}
