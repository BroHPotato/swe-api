package com.redroundrobin.thirema.apirest.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.redroundrobin.thirema.apirest.models.Device;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataFetch {

    final String[] topics = new String[] {"US-GATEWAY-1", "SG-GATEWAY-2", "DE-GATEWAY-3"};

    public static List<JsonObject> getForTopics(String [] topics) throws InterruptedException {
        Consumatore cons = new Consumatore(topics, "localhost:29092");
        List<JsonObject> mex = cons.fetchMessage();
        cons.chiudi();
        return mex;
    }

    public static List<JsonObject> getDevices(){
        List<JsonObject> test = testMessage();
        List<JsonObject> devices = new ArrayList<JsonObject>();
        devices = test.stream()
                .filter(jsonObject -> jsonObject.get("deviceId").getAsString() != "").collect(Collectors.toList());
        return devices;
    }
    public static Device getDevice(String deviceId){
        List<JsonObject> mex = testMessage();
        List<JsonObject> deviceFilter = new ArrayList<JsonObject>();
        Optional<>
        deviceFilter = mex.stream()
                            .filter(jo -> jo.get("deviceId").getAsString().equals(deviceId)).findFirst());


        return new Device(deviceFilter.get(0).get("deviceId").getAsInt());
    }
    public static List<JsonObject> getSensor(String deviceId, String sensorId){
        List<JsonObject> test = testMessage();
        List<JsonObject> sensor = new ArrayList<JsonObject>();

        sensor = test.stream()
                .filter(jsonObject -> jsonObject.get("deviceId").getAsString().equals(deviceId) ).collect(Collectors.toList());

        sensor = sensor.get("sensors").getAsJsonArray();

        sensor = sensor.stream() .filter(jsonObject -> {
            jsonObject.get("sensorId").getAsString().equals(sensorId)})
                .collect(Collectors.toList());
        return sensor;
    }

    public static List<JsonObject> testMessage() {
        // TEST STRING GENERATOR
        // ============================
        String mytest = "[{\"deviceId\":1,\"timestamp\":1582818576871,\"sensors\":[{\"sensorId\":1,\"timestamp\":1582818576620,\"data\":5},{\"sensorId\":2,\"timestamp\":1582818576871,\"data\":5}]}, {\"deviceId\":2,\"timestamp\":1582818577121,\"sensors\":[{\"sensorId\":1,\"timestamp\":1582818577121,\"data\":9}]}]";
        ;
        List<JsonObject> mex = new ArrayList<JsonObject>();
        JsonArray dati = new JsonParser().parseString(mytest).getAsJsonArray();
        for (JsonElement elemento : dati) {
            JsonObject datiDispositivo = elemento.getAsJsonObject();
            mex.add(datiDispositivo);
        }
        return mex;
        // Usare "mex" come List<JsonObject> di output da un possibile consumatore.
        // ============================
    }
}
