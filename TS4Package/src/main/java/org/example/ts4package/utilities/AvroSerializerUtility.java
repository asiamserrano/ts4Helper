package org.example.ts4package.utilities;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.MyAvroClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvroSerializerUtility {

    public static void serializeMyAvros(List<MyAvroClass> personList, File myAvroListSerializedFile) throws IOException {
        // We create a DatumWriter, which converts Java objects into an in-memory serialized format.
        // The SpecificDatumWriter class is used with generated classes and extracts the schema from the specified generated type.
        DatumWriter<MyAvroClass> datumWriter = new SpecificDatumWriter<>(MyAvroClass.class);
        // Next we create a DataFileWriter, which writes the serialized records, as well as the schema, to the file specified in the dataFileWriter.create() call.
        // We write our persons to the file via calls to the dataFileWriter.append method. When we are done writing, we close the data file.
        DataFileWriter<MyAvroClass> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(personList.get(0).getSchema(), myAvroListSerializedFile);
        personList.forEach(person -> {
            try {
                dataFileWriter.append(person);
            } catch (IOException e) {
                System.err.println("Error writing my avro with name: " + person.getName());
            }
        });
        dataFileWriter.close();
    }

}
