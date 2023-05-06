package br.com.app.vacina.Entities;

import java.time.LocalDate;

import javax.swing.JOptionPane;


public class RegistroVacinacao {
    private Bebe bebe;
    private Vacina vacina;
    private LocalDate dataTomada;
    private int doseTomada;

    public RegistroVacinacao(Bebe bebe, Vacina vacina, LocalDate dataTomada, int doseTomada) {
        this.bebe = bebe;
        this.vacina = vacina;
        this.dataTomada = dataTomada;
        this.doseTomada = doseTomada;
    }

    public Bebe getBebe() {
        return bebe;
    }

    public Vacina getVacina() {
        return vacina;
    }

    public LocalDate getDataTomada() {
        return dataTomada;
    }

    public int getDoseTomada() {
        return doseTomada;
    }

    public LocalDate getDataProximaVacinacao() {
        return dataTomada.plus(this.vacina.getIdadeMinima());
    }

    public void enviarAlertaProximaVacina(LocalDate dataAtual) {
        // verificar se já tomou todas as doses necessárias da vacina
        if (this.doseTomada >= this.vacina.getDoseNecessaria()) {
            return;
        }

        // calcular a idade do bebê na data da próxima vacina
        LocalDate dataProximaVacina = this.bebe.getDataNascimento().plus(this.vacina.getIdadeMinima());

        // verificar se a data atual é menor que a data recomendada para a próxima
        // vacina
        if (dataAtual.isBefore(dataProximaVacina)) {
            String mensagem = "O bebê " + this.bebe.getNome() + " precisa tomar a vacina " + this.vacina.getNome();
            mensagem += "\nData recomendada: " + dataProximaVacina;
            mensagem += "\nDose: " + (this.doseTomada + 1);
            JOptionPane.showMessageDialog(null, mensagem);
        }
    }

    public boolean estaAtrasado() {
        LocalDate dataValidade = bebe.getDataNascimento().plus(vacina.getIdadeMinima());
        return dataValidade.isBefore(LocalDate.now()) && !vacinacaoCompleta();
    }
    
    public boolean vacinacaoCompleta() {
        return doseTomada >= vacina.getDoseNecessaria();
    }

    public void adicionarVacina(Vacina vacina, LocalDate dataTomada, int doseTomada){
        this.vacina = vacina;
        this.dataTomada = dataTomada;
        this.doseTomada = doseTomada;
    }

    // public Vacina getUltimaVacinaTomada(Vacina vacina) {
    //     Vacina ultimaVacinaTomada = null;
    //     String json = leJson();
    //     if (json != null) {
    //         JsonObject bebeJson = JsonParser.parseString(json).getAsJsonObject();
    //         JsonArray vacinasTomadasJson = bebeJson.getAsJsonArray("vacinasTomadas");
    //         for (JsonElement vacinaJson : vacinasTomadasJson) {
    //             if (vacinaJson.getAsString().equals(vacina.getNome())) {
    //                 ultimaVacinaTomada = vacina;
    //             }
    //         }
    //     }
    //     return ultimaVacinaTomada;
    // }
}