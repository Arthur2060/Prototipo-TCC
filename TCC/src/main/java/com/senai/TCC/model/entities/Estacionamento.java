package com.senai.TCC.model.entities;

import com.senai.TCC.model.enums.Metodo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import lombok.experimental.SuperBuilder;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;
    private String CEP;
    private String numero;

    private File foto;

    private String numeroAlvaraDeFuncionamento;
    private Boolean funcionamento;

    @ManyToOne
    @JoinColumn(name = "dono_id")
    private DonoEstacionamento dono;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gerente> gerentes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valor> valores;

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private String numeroContaDono;
    private Double valorArrecadadoDoDia;

    private Double notaMedia;
    private Integer quantidadeDeAvaliacoes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;

    private Double latitude;
    private Double longitude;

    private Integer maxVagas;
    private Integer vagasDisponiveis;
    private Integer vagaPreferenciais;
    private LocalDate diaAtual;
    private String numeroDeEscrituraImovel;
    private Metodo metodoDePagamento;

    private Boolean status;
    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acesso> acessos;

    public void calcularNotaMedia() {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            this.quantidadeDeAvaliacoes = 0;
            this.notaMedia = 0.0;
            return;
        }

        this.quantidadeDeAvaliacoes = avaliacoes.size();
        double soma = 0.0;
        for (Avaliacao avaliacao : avaliacoes) {
            soma += avaliacao.getNota().doubleValue();
        }
        notaMedia = soma / this.quantidadeDeAvaliacoes;
    }

}
