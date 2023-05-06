package br.com.app.vacina.Entities;

import java.util.List;

public class DadosVacinasBebes {
    private List<Bebe> bebes;
    private List<Vacina> vacinas;

    public DadosVacinasBebes(List<Bebe> bebes, List<Vacina> vacinas) {
        this.bebes = bebes;
        this.vacinas = vacinas;
    }

    public List<Bebe> getBebes() {
        return bebes;
    }

    public void setBebes(List<Bebe> bebes) {
        this.bebes = bebes;
    }

    public List<Vacina> getVacinas() {
        return vacinas;
    }

    public void setVacinas(List<Vacina> vacinas) {
        this.vacinas = vacinas;
    }
}
