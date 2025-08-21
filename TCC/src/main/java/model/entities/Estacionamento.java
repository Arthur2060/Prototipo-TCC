package model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.entities.usuarios.DonoEstacionamento;
import model.entities.usuarios.Gerente;
import model.enums.Metodo;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;

    private File foto;

    private String numeroAlvaraDeFuncionamento;
    private Boolean status;

    @ManyToOne
    private DonoEstacionamento dono;

    @OneToMany
    private List<Gerente> gerentes;

    @OneToMany
    private List<Valor> valores;

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private String numeroContaDono;
    private Double valorArrecadadoDoDia;
    private Double notaMedia;
    private Integer quantidadeDeAvaliacoes;

    @OneToMany
    private List<Avaliacao> avaliacoes;

    private Double latitude;
    private Double longitude;

    private Integer maxVagas;
    private Integer vagasDisponiveis;
    private Integer vagaPreferenciais;
    private LocalDate diaAtual;
}
