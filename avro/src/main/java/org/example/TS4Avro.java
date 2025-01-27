package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.reef.webserver.AvroHttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class TS4Avro {
    public static void main(String[] args) {

        MyAvroClass myAvroClass = new MyAvroClass();
        myAvroClass.setName("Asia");
        myAvroClass.setFavoriteNumber(13);
        myAvroClass.setFavoriteColor("red");
////        myAvroClass.setName("Asia");
//        myAvroClass.setMessage("I love you");
//
////        System.out.println("name: " + myAvroClass.getName());
//        System.out.println("message: " + myAvroClass.getMessage());
//
//
//
        byte[] bytes = serializeMyAvroClassRequestJSON(myAvroClass);
        System.out.println("bytes: " + bytes.length);

    }

    public static byte[] serializeMyAvroClassRequestJSON(
            MyAvroClass request) {

//        DatumWriter<MyAvroClass> writer = new SpecificDatumWriter<>(
//                MyAvroClass.class);

        DatumWriter<MyAvroClass> userDatumWriter = new SpecificDatumWriter<>(MyAvroClass.class);
//        DataFileWriter<MyAvroClass> dataFileWriter = new DataFileWriter<MyAvroClass>(userDatumWriter);

        byte[] data = new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Encoder jsonEncoder = null;
        try {
            jsonEncoder = EncoderFactory.get().jsonEncoder(
                    AvroHttpRequest.getClassSchema(), stream);
            userDatumWriter.write(request, jsonEncoder);
            jsonEncoder.flush();
            data = stream.toByteArray();
        } catch (IOException e) {
            log.error("Serialization error:" + e.getMessage());
        } catch (Exception e) {
            log.error("some error: {}", e.getMessage());
        }
        return data;
    }


}
