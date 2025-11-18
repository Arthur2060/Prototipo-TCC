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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "estacionamento")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(nullable = false, length = 8)
    private String CEP;

    @Column(nullable = false, length = 10)
    private String numero;


    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "numero_alvara_de_funcionamento", nullable = false, unique = true, length = 50)
    private String numeroAlvaraDeFuncionamento;

    private Boolean funcionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id", nullable = false)
    private DonoEstacionamento dono;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Gerente> gerentes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Valor> valores;

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;

    @Column(name = "numero_conta_dono", length = 50)
    private String numeroContaDono;

    @Column(name = "valor_arrecadado_do_dia")
    private Double valorArrecadadoDoDia;

    private Double notaMedia;

    @Column(name = "quantidade_de_avaliacoes")
    private Integer quantidadeDeAvaliacoes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reserva> reservas;

    private Double latitude;
    private Double longitude;

    @Column(name = "max_vagas", nullable = false)
    private Integer maxVagas;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "vagas_preferenciais")
    private Integer vagasPreferenciais;
    private LocalDate diaAtual;

    @Column(name = "numero_de_escritura_imovel", unique = true, length = 100)
    private String numeroDeEscrituraImovel;

    @Column(name = "metodo_de_pagamento")
    @Enumerated(EnumType.STRING)
    private Metodo metodoDePagamento;

    private Boolean status;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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