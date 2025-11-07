package com.senai.TCC.infraestructure_ui.handlers;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.services.AcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssinanteHandler {
    /*TODO*/
    //Quando implementar com IOT (recebimento do payload da placa
    private final AcessoService acessoService;
    @MqttSubscriber("topico/teste")
    public void receberMensagem(@MqttPayload AcessoRequest dto) {
        acessoService.cadastrarAcesso(dto);
    }
}