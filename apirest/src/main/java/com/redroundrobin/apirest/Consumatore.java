package com.redroundrobin.apirest;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
//import java.time.duration;
import com.google.gson.JsonObject;

public class Consumatore {
    private String topic;
    private String bootstrapServers; //Lista di indirizzoIP:porta separati da una virgola.
    private String nome;
    private Consumer<Long, String> consumatore;

    //definisco la classe consumatore e ne istanzio uno
    Consumatore(String topic, String nomeConsumatore, String boostrapServers) {
        this.topic = topic;
        this.bootstrapServers = boostrapServers;
        this.nome = nomeConsumatore;

        //Imposto le proprietà del consumatore da creare
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, nomeConsumatore);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


        Consumer<Long, String> cons = new KafkaConsumer<>(props);

        //Sottoscrivo il consumatore al topic
        cons.subscribe(Collections.singletonList(topic));

        this.consumatore = cons;
    }

    //mi collego a kafka e prendo i records del consumatore
    public List<JsonObject> rispostaConsumatore(Consumatore consumatore) throws InterruptedException {
        System.out.println("Consumatore "+consumatore.nome+" richiesta dati avviata");

        List<JsonObject> jsonObject = new ArrayList<JsonObject>();

        final ConsumerRecords<Long, String> recordConsumatore = consumatore.consumatore.poll(Duration.ofSeconds(5));
        if (recordConsumatore.count()==0) {
            System.out.println("Nessun record trovato");
        }

        for(ConsumerRecord<Long, String> record : recordConsumatore) {
            //produco il file JSON
            JsonObject dato = new JsonParser().parse("{ Valore: "+record.value()+ " }").getAsJsonObject();
            jsonObject.add(dato);
        };





//        for(ConsumerRecord<Long, String> record : recordConsumatore) {
//            //produco il file JSON
//            datiJson+="{";
//            datiJson+="nome: "+consumatore.nome+"; Chiave: "+record.key()+"; Valore: "+record.value()+"; Partizione: "
//                    +record.partition()+"; Offeset: "+record.offset()+"; }";
//        };

        System.out.println("Messaggi disponibili consumati!");
        return jsonObject;
    }

    public static void main(String args[]) throws Exception {

        Consumatore test = new Consumatore("TopicDiProva","consumatoreTest", "localhost:29092");

        Consumatore.rispostaConsumatore(test);
    }
}
