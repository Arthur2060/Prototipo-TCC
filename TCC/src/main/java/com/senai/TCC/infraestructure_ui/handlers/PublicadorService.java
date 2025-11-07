package com.senai.TCC.infraestructure_ui.handlers;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import org.springframework.stereotype.Service;

@Service
public class PublicadorService {
    /*TODO*/
    //quando implementar com IOT(faz um LED acender só pra mostrar que a catraca abriria
    @MqttPublisher("topico/teste")
    public String publicarMensagem() {
        return "Cancela abrindo!";
    }
}