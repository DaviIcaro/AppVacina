package br.com.app.vacina.Entities;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Bebe {
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private char sexo;
    private List<Vacina> vacinasTomadas;

    public Bebe(String nome, LocalDate dataNascimento, String cpf, char sexo) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.sexo = sexo;
        this.vacinasTomadas = new ArrayList<Vacina>();
    }

    public Bebe(String nome, String cpf, LocalDate dataNascimento, List<Vacina> vacinasTomadas) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.vacinasTomadas = vacinasTomadas;
    }


    public Bebe(String nome, LocalDate dataNascimento, String cpf, char sexo, List<Vacina> vacinasTomadas) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.sexo = sexo;
        this.vacinasTomadas = vacinasTomadas;
    }

    public int getIndex(List<Bebe> bebes, String cpf) {
        for (int i = 0; i < bebes.size(); i++) {
            Bebe bebe = bebes.get(i);
            if (bebe.getCpf().equals(cpf)) {
                return i;
            }
        }
        return -1; // Não encontrado
    }

    public Bebe() {
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public char getSexo() {
        return sexo;
    }

    public List<Vacina> getVacinasTomadas() {
        return vacinasTomadas;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public void setVacinasTomadas(List<Vacina> vacinasTomadas) {
        this.vacinasTomadas = vacinasTomadas;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bebe other = (Bebe) obj;
        return Objects.equals(cpf, other.cpf);
    }

    public boolean vacinaTomada(String nomeVacina) {
        return this.vacinasTomadas.stream().anyMatch(vacina -> vacina.getNome().equals(nomeVacina));
    }


    public void adicionarVacina(Vacina vacina) {
        vacinasTomadas.add(vacina);
    }

    public Period getIdadeMinimaVacina(String nomeVacina) {
        for (Vacina vacina : vacinas) {
            if (vacina.getNome().equals(nomeVacina)) {
                return vacina.getIdadeMinima();
            }
        }
        System.out.println("Vacina não encontrada.");
        return null;
    }

        // lista de vacinas
        private static List<Vacina> vacinas = Arrays.asList(
            new Vacina("BCG", Period.ZERO, 'A', "2025-12-31", 1, 1234),
            new Vacina("Hepatite B", Period.ZERO, 'A', "2025-12-31", 3, 5678),
            new Vacina("Pentavalente", Period.ofMonths(2), 'A', "2025-12-31", 3, 9012),
            new Vacina("VIP/VOP", Period.ofMonths(2), 'A', "2025-12-31", 3, 3456),
            new Vacina("Pneumocócica", Period.ofMonths(2), 'A', "2025-12-31", 3, 7890),
            new Vacina("Rotavírus", Period.ofMonths(2), 'A', "2025-12-31", 2, 2345),
            new Vacina("Meningocócica C", Period.ofMonths(3), 'A', "2025-12-31", 2, 6789),
            new Vacina("Tríplice Viral", Period.ofMonths(12), 'A', "2025-12-31", 2, 1234),
            new Vacina("Hepatite A", Period.ofYears(1), 'A', "2025-12-31", 2, 5678),
            new Vacina("Tetra Viral", Period.ofYears(1), 'A', "2025-12-31", 2, 9012),
            new Vacina("DTP", Period.ofYears(1), 'A', "2025-12-31", 3, 3456),
            new Vacina("Hib", Period.ofYears(1), 'A', "2025-12-31", 3, 7890),
            new Vacina("Meningocócica ACWY", Period.ofYears(1), 'A', "2025-12-31", 1, 2345)
    );
}
