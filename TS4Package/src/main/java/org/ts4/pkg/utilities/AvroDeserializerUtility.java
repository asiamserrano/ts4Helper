package org.ts4.pkg.utilities;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.ts4.avro.WebsiteModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvroDeserializerUtility {

//    public static List<MyAvroClass> deserializeMyAvros(File myAvroListSerializedFile) throws IOException {
//        DatumReader<MyAvroClass> personDatumReader = new SpecificDatumReader<>(MyAvroClass.class);
//        DataFileReader<MyAvroClass> dataFileReader = new DataFileReader<>(myAvroListSerializedFile, personDatumReader);
//        MyAvroClass person = null;
//        List<MyAvroClass> persons = new ArrayList<>();
//        // Reuse person object by passing it to next(). This saves us from allocating and garbage collecting many objects for files with many items.
//        while (dataFileReader.hasNext()) {
//            person = dataFileReader.next(person);
//            persons.add(person);
//        }
//        dataFileReader.close();
//        return persons;
//    }

}
