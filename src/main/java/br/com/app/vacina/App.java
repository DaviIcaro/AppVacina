package br.com.app.vacina;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import br.com.app.vacina.Entities.Bebe;
import br.com.app.vacina.Entities.ManipuladorArquivos;
import br.com.app.vacina.Entities.Vacina;
import br.com.app.vacina.Exceptions.UsuarioNaoEncontradoException;

public class App {
    private static List<Bebe> bebesCadastrados = new ArrayList<>();
    private static List<Vacina> vacinasCadastradas = new ArrayList<>();

    public static List<Bebe> getBebesCadastrados() {
        return bebesCadastrados;
    }

    public static void setBebesCadastrados(List<Bebe> bebes) {
        bebesCadastrados = bebes;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Lista de vacinas a serem cadastradas

        // Quando o SEXO É 'A' Significa que Ambos os SEXO podem tomar a vacina
        List<Vacina> vacinas = Arrays.asList(
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
                new Vacina("Meningocócica ACWY", Period.ofYears(1), 'A', "2025-12-31", 1, 2345));

        // adiciona as vacinas ao registro global de vacinas
        for (Vacina vacina : vacinas) {
            vacinasCadastradas.add(vacina); 
        }

        App app = new App();

        // Recuperar bebes do arquivo
        try {
            List<Bebe> bebes = ManipuladorArquivos.recuperarBebes("dados.json");
            setBebesCadastrados(bebes);
        } catch (IOException e) {
            System.out.println("Erro ao recuperar dados do arquivo: " + e.getMessage());
        }

        app.executar();
    }

    private void executar() {
        int opcao = 0;
        do {
            String mensagem = "-----------MENU-----------\n"
                    + "1 - Login\n"
                    + "2 - Cadastrar Bebê\n"
                    + "3 - Sair\n "
                    + "Opção: ";
            opcao = Integer.parseInt(JOptionPane.showInputDialog(null, mensagem));
            switch (opcao) {
                case 1:
                    realizarLogin();
                    break;
                case 2:
                    cadastrarBebe();
                    break;
                case 3:
                    // Salvar dados no arquivo
                    try {
                        ManipuladorArquivos.salvarDados("dados.json", bebesCadastrados, vacinas);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null, "Até logo!");
            }
        } while (opcao != 3);
    }

    public void realizarLogin() {
        String cpf = JOptionPane.showInputDialog(null, "Digite o CPF do bebê:");
        try {
            List<Bebe> bebesCadastrados = ManipuladorArquivos.recuperarBebes("dados.json");
            List<Vacina> vacinasCadastradas = ManipuladorArquivos.recuperarVacinas("dados.json");

            Optional<Bebe> bebeEncontradoOpt = ManipuladorArquivos.buscarBebePorCpf(cpf, "dados.json");
            if (!bebeEncontradoOpt.isPresent()) {
                throw new UsuarioNaoEncontradoException("Bebê não encontrado no sistema.");
            }

            Bebe bebeEncontrado = bebeEncontradoOpt.get();

            JOptionPane.showMessageDialog(null, "Bem-vindo, " + bebeEncontrado.getNome() + "!");
            exibirAlertaVacinas(bebeEncontrado);

            int opcaoVacina = 0;
            do {
                String mensagemVacina = "=== MENU DE VACINAS ===\n";

                for (Vacina vacina : vacinas) {
                    mensagemVacina += vacina.getIdadeMinima().toTotalMonths() + ". " + vacina.getNome() + "\n";
                }

                mensagemVacina += "0. Voltar\nSelecione uma opção:";
                opcaoVacina = Integer.parseInt(JOptionPane.showInputDialog(null, mensagemVacina));

                if (opcaoVacina < 0 || opcaoVacina > vacinas.size()) {
                    JOptionPane.showMessageDialog(null, "Opção inválida!");
                } else if (opcaoVacina != 0) {
                    Vacina vacinaSelecionada = vacinas.get(opcaoVacina - 1);

                    cadastrarVacina(bebeEncontrado, vacinaSelecionada);
                }

            } while (opcaoVacina != 0);

        } catch (UsuarioNaoEncontradoException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Não foi possível ler o arquivo de dados.");
        }
    }

    private void exibirAlertaVacinas(Bebe bebe) {

        if (vacinasCadastradas == null) {
            vacinasCadastradas = vacinas;
        }
        // Verifica vacinas em atraso
        List<String> vacinasAtrasadas = vacinasCadastradas.stream()
                .filter(vacina -> ChronoUnit.MONTHS.between(bebe.getDataNascimento(), LocalDate.now()) >= vacina
                        .getIdadeMinima().toTotalMonths())
                .filter(vacina -> bebe.getVacinasTomadas().stream()
                        .noneMatch(v -> v.getNome().equals(vacina.getNome())))
                .map(Vacina::getNome)
                .collect(Collectors.toList());

        // Verifica a próxima vacina
        Vacina proximaVacina = vacinasCadastradas.stream()
                .filter(vacina -> bebe.getVacinasTomadas().stream()
                        .noneMatch(v -> v.getNome().equals(vacina.getNome())))
                .filter(v -> ChronoUnit.MONTHS.between(bebe.getDataNascimento(), LocalDate.now()) >= v.getIdadeMinima()
                        .toTotalMonths())
                .min(Comparator.comparing(v -> v.getIdadeMinima().toTotalMonths()))
                .orElse(null);

        // Verifica a próxima vacina sem filtrar pela idade mínima
        Vacina proximaVacinaSemFiltro = verificarProximaVacina(bebe);

        // Calcula a data e a quantidade de dias da próxima vacina
        LocalDate dataProximaVacina = bebe.getDataNascimento();
        long diasFaltandoProximaVacina = 0;
        if (proximaVacinaSemFiltro != null) {
            dataProximaVacina = LocalDate.from(proximaVacinaSemFiltro.getIdadeMinima().addTo(bebe.getDataNascimento()));
            diasFaltandoProximaVacina = ChronoUnit.DAYS.between(LocalDate.now(), dataProximaVacina);
        }

        // Exibe alerta de vacinas
        JOptionPane.showMessageDialog(null, "Alerta de vacinas para " + bebe.getNome() + ":");

        if (proximaVacina != null) {
            JOptionPane.showMessageDialog(null, "- Próxima vacina: " + proximaVacina.getNome() + " - Idade mínima: "
                    + proximaVacina.getIdadeMinima().toTotalMonths() + " meses");
        } 
        
        if (!vacinasAtrasadas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Atenção! O bebê está com as seguintes vacinas em atraso:");
            vacinasAtrasadas.forEach(v -> JOptionPane.showMessageDialog(null, "- " + v));
        } else {
            JOptionPane.showMessageDialog(null, "O bebê está com todas as vacinas em dia!");
            JOptionPane.showMessageDialog(null, "- Próxima vacina: " + proximaVacinaSemFiltro.getNome() + " - Idade mínima: "
                    + proximaVacinaSemFiltro.getIdadeMinima().toTotalMonths() + " meses - Data: " + dataProximaVacina.toString()
                    + " - Faltam: " + diasFaltandoProximaVacina + " dias");
        }
    }

    public static void cadastrarVacina(Bebe bebe, Vacina vacinaSelecionada) {
        String cpf = bebe.getCpf();

        // Procura pelo objeto Bebe correspondente ao CPF informado na lista de bebês
        // cadastrados
        Bebe bebeEncontrado = null;
        for (Bebe b : bebesCadastrados) {
            if (b.getCpf().equals(cpf)) {
                bebeEncontrado = b;
                break;
            }
        }

        if (bebeEncontrado == null) {
            JOptionPane.showMessageDialog(null, "Bebê não encontrado.");
        } else {
            List<Vacina> vacinasTomadas = bebeEncontrado.getVacinasTomadas();
            if (vacinasTomadas.contains(vacinaSelecionada)) {
                JOptionPane.showMessageDialog(null, "O bebê já tomou essa vacina.");
            } else {
                Period idadeMinima = vacinaSelecionada.getIdadeMinima();
                LocalDate dataMinima = bebeEncontrado.getDataNascimento().plus(idadeMinima);
                if (dataMinima.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(null,
                            "Vacina registrada com sucesso! Porém está em atraso. A vacina deveria ter sido tomada ao nascer.");
                } else {
                    JOptionPane.showMessageDialog(null, "Vacina registrada com sucesso!");
                }
                vacinasTomadas.add(vacinaSelecionada);
                bebeEncontrado.setVacinasTomadas(vacinasTomadas);
                // atualizar lista de bebês cadastrados com o bebê que teve as vacinas tomadas
                // atualizadas
                for (int i = 0; i < bebesCadastrados.size(); i++) {
                    if (bebesCadastrados.get(i).getCpf().equals(bebeEncontrado.getCpf())) {
                        bebesCadastrados.set(i, bebeEncontrado);
                        break;
                    }
                }
            }
        }
    }

    public static void cadastrarBebe() {
        String nome = JOptionPane.showInputDialog(null, "Digite o nome do bebê:");
        String cpf = JOptionPane.showInputDialog(null, "Digite o CPF do bebê:");
        String dataNascimentoStr = JOptionPane.showInputDialog(null,
                "Digite a data de nascimento do bebê (dd/mm/aaaa):");
        LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        char sexo = JOptionPane.showInputDialog(null, "Digite o sexo do bebê:").charAt(0);

        // verifica se já existe um bebê com o mesmo CPF cadastrado
        boolean cpfCadastrado = false;
        for (Bebe bebe : bebesCadastrados) {
            if (bebe.getCpf().equals(cpf)) {
                cpfCadastrado = true;
                break;
            }
        }
        if (cpfCadastrado) {
            JOptionPane.showMessageDialog(null, "Já existe um bebê cadastrado com esse CPF.");
        } else {
            // cria o bebê e o adiciona ao registro de bebês cadastrados
            Bebe novoBebe = new Bebe(nome, dataNascimento, cpf, sexo);
            bebesCadastrados.add(novoBebe);
            JOptionPane.showMessageDialog(null, "Bebê cadastrado com sucesso!");
        }
    }

    private Vacina verificarProximaVacina(Bebe bebe) {
        return vacinas.stream()
                .filter(vacina -> bebe.getVacinasTomadas().stream()
                        .noneMatch(v -> v.getNome().equals(vacina.getNome())))
                .min(Comparator.comparing(v -> ChronoUnit.MONTHS.between(bebe.getDataNascimento(), LocalDate.now())))
                .orElse(null);
    }

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
            new Vacina("Meningocócica ACWY", Period.ofYears(1), 'A', "2025-12-31", 1, 2345));
}