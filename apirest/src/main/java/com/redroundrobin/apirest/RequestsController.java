package com.redroundrobin.apirest;

import com.redroundrobin.apirest.models.*;
import org.springframework.web.bind.annotation.*;

import static com.redroundrobin.apirest.Consumatore.rispostaConsumatore;


/*
    Il RequestsController possiamo provare a tenerlo unico, ma se serve
    suddividiamo in più controller indipendenti in base alla difficoltà.
    ---------------------------------------------------------
    Guida generale: https://spring.io/guides/gs/rest-service/
    Domande guida: https://stackoverflow.com/a/31422634
 */

@RestController
public class RequestsController {

    @RequestMapping(value = {"/topic/{topicid:.+}"})
    public Topic topic(@PathVariable("topicid") String ID) throws InterruptedException {
        Topic t = new Topic(ID);
        Consumatore cons = new Consumatore(ID, "ConsumatoreBello", "localhost:29092");
        t.setMessage(rispostaConsumatore(cons));
        return t;
    }

}
