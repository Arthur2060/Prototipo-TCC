package com.senai.TCC.model.entities;

import com.senai.TCC.model.exceptions.ComentarioMuitoLongo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.usuarios.Cliente;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Cliente cliente;

    @ManyToOne
    private Estacionamento estacionamento;

    private Short nota;
    private String comentario;
    private LocalDateTime dataDeAvaliacao;

    public void validarTamanhoDoComentario() {
        if (this.comentario.length() > 500) {
            throw new ComentarioMuitoLongo("Comentario muito longo");
        }
    }
}
