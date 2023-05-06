package br.com.app.vacina.Entities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

public class Vacina {
    private String nome;
    private Period idadeMinima;
    private char sexo;
    private String dataValidade;
    private int doseNecessaria;
    private int lote;

    public Vacina(String nome, Period idadeMinima, char sexo, String dataValidade, int doseNecessaria, int lote){
        this.nome = nome;
        this.idadeMinima = idadeMinima;
        this.sexo = sexo;
        this.dataValidade = dataValidade;
        this.doseNecessaria = doseNecessaria;
        this.lote = lote;
    }

    public Vacina(String nome, Period idadeMinima) {
        this.nome = nome;
        this.idadeMinima = idadeMinima;
    }

    public Vacina(String nome) {
        this.nome = nome;
    }

    public Vacina() {
    }


    public String getNome() {
        return nome;
    }

    public char getSexo() {
        return sexo;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public int getDoseNecessaria() {
        return doseNecessaria;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }

    public Period getIdadeMinima() {
        return idadeMinima;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdadeMinima(Period idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public void setDoseNecessaria(int doseNecessaria) {
        this.doseNecessaria = doseNecessaria;
    }

    public Duration getPeriodoValidade() {
        LocalDate dataValidade = LocalDate.parse(this.dataValidade);
        return Duration.between(LocalDate.now(), dataValidade);
    }
}

