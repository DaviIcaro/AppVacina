package br.com.app.vacina.Entities;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ManipuladorArquivos {

    public static Map<String, List<?>> recuperarDados(String nomeArquivo) throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Map<String, List<?>> data = mapper.readValue(new File(nomeArquivo), new TypeReference<Map<String, List<?>>>() {
        });
        return data;
    }

    public static void salvarDados(String nomeArquivo, List<Bebe> bebesCadastrados, List<Vacina> vacinasCadastradas)
            throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
        ObjectNode data = mapper.createObjectNode();
        ArrayNode bebes = mapper.createArrayNode();
        ArrayNode vacinas = mapper.createArrayNode();
        for (Bebe bebe : bebesCadastrados) {
            ObjectNode bebeJson = mapper.createObjectNode();
            bebeJson.put("nome", bebe.getNome());
            bebeJson.put("cpf", bebe.getCpf());
            bebeJson.put("dataNascimento", bebe.getDataNascimento().toString());
            ArrayNode vacinasTomadas = mapper.createArrayNode();
            for (Vacina vacina : bebe.getVacinasTomadas()) {
                ObjectNode vacinaJson = mapper.createObjectNode();
                vacinaJson.put("nome", vacina.getNome());
                vacinaJson.put("idadeMinima", vacina.getIdadeMinima().toString());
                vacinasTomadas.add(vacinaJson);
            }
            bebeJson.set("vacinasTomadas", vacinasTomadas);
            bebes.add(bebeJson);
        }
        for (Vacina vacina : vacinasCadastradas) {
            ObjectNode vacinaJson = mapper.createObjectNode();
            vacinaJson.put("nome", vacina.getNome());
            vacinaJson.put("idadeMinima", vacina.getIdadeMinima().toString());
            vacinas.add(vacinaJson);
        }
        data.set("bebes", bebes);
        data.set("vacinas", vacinas);
        mapper.writeValue(new File(nomeArquivo), data);
    }

    public static void atualizarBebe(String nomeArquivo, String cpf, List<Vacina> vacinasTomadas) throws IOException {
        Map<String, List<?>> dados = recuperarDados(nomeArquivo);
        List<Bebe> bebesCadastrados = (List<Bebe>) dados.get("bebes");
        for (Bebe bebe : bebesCadastrados) {
            if (bebe.getCpf().equals(cpf)) {
                bebe.setVacinasTomadas(vacinasTomadas);
                salvarDados(nomeArquivo, bebesCadastrados, (List<Vacina>) dados.get("vacinas"));
                return;
            }
        }
        throw new IllegalArgumentException("Bebê não encontrado");
    }

    public static List<Bebe> recuperarBebes(String nomeArquivo) throws IOException {
        Map<String, List<?>> data = recuperarDados(nomeArquivo);
        List<Bebe> bebes = new ArrayList<>();
        for (Object obj : data.get("bebes")) {
            LinkedHashMap<String, Object> bebeMap = (LinkedHashMap<String, Object>) obj;
            String nome = (String) bebeMap.get("nome");
            String cpf = (String) bebeMap.get("cpf");
            LocalDate dataNascimento = LocalDate.parse((String) bebeMap.get("dataNascimento"));
            List<Vacina> vacinasTomadas = new ArrayList<>();
            for (Object vacinaObj : (List<?>) bebeMap.get("vacinasTomadas")) {
                LinkedHashMap<String, Object> vacinaMap = (LinkedHashMap<String, Object>) vacinaObj;
                String vacinaNome = (String) vacinaMap.get("nome");
                Period vacinaIdadeMinima = Period.parse((String) vacinaMap.get("idadeMinima"));
                Vacina vacina = new Vacina(vacinaNome, vacinaIdadeMinima);
                vacinasTomadas.add(vacina);
            }
            Bebe bebe = new Bebe(nome, cpf, dataNascimento, vacinasTomadas);
            bebes.add(bebe);
        }
        return bebes;
    }

    public static void atualizarDados(String nomeArquivo, List<Bebe> bebes, List<Vacina> vacinas) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Cria o objeto que irá armazenar os dados a serem salvos no arquivo
        Map<String, Object> dados = new HashMap<String, Object>();
        dados.put("bebes", bebes);
        dados.put("vacinas", vacinas);

        // Converte os dados para JSON
        String json = mapper.writeValueAsString(dados);

        // Escreve os dados no arquivo
        FileWriter writer = new FileWriter(nomeArquivo);
        writer.write(json);
        writer.close();
    }

    /**
     * Método responsável por ler os dados salvos em arquivo JSON e preencher as
     * listas de bebês e vacinas.
     * 
     * @param nomeArquivo        o nome do arquivo JSON
     * @param bebesCadastrados   a lista de bebês a ser preenchida
     * @param vacinasCadastradas a lista de vacinas a ser preenchida
     * @throws IOException se houver algum erro na leitura do arquivo
     */
    public static void carregarDados(String nomeArquivo, List<Bebe> bebesCadastrados, List<Vacina> vacinasCadastradas)
            throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
        ObjectNode data = (ObjectNode) mapper.readTree(new File(nomeArquivo));
        ArrayNode bebes = (ArrayNode) data.get("bebes");
        ArrayNode vacinas = (ArrayNode) data.get("vacinas");
        for (JsonNode bebeJson : bebes) {
            String nome = bebeJson.get("nome").asText();
            String cpf = bebeJson.get("cpf").asText();
            LocalDate dataNascimento = LocalDate.parse(bebeJson.get("dataNascimento").asText());
            ArrayNode vacinasTomadasJson = (ArrayNode) bebeJson.get("vacinasTomadas");
            List<Vacina> vacinasTomadas = new ArrayList<>();
            for (JsonNode vacinaJson : vacinasTomadasJson) {
                String nomeVacina = vacinaJson.get("nome").asText();
                Period idadeMinima = Period.parse(vacinaJson.get("idadeMinima").asText());
                Vacina vacinaEncontrada = null;
                for (Vacina vacina : vacinasCadastradas) {
                    if (vacina.getNome().equals(nomeVacina) && vacina.getIdadeMinima().equals(idadeMinima)) {
                        vacinaEncontrada = vacina;
                        break;
                    }
                }
                if (vacinaEncontrada != null) {
                    vacinasTomadas.add(vacinaEncontrada);
                }
            }
            Bebe bebe = new Bebe(nome, cpf, dataNascimento, vacinasTomadas);
            bebesCadastrados.add(bebe);
        }
        for (JsonNode vacinaJson : vacinas) {
            String nome = vacinaJson.get("nome").asText();
            Period idadeMinima = Period.parse(vacinaJson.get("idadeMinima").asText());
            Vacina vacina = new Vacina(nome, idadeMinima, 'A', "2025-12-31", 1, 0);
            vacinasCadastradas.add(vacina);
        }
    }

    public static Optional<Bebe> buscarBebePorCpf(String cpf, String nomeArquivo) throws IOException {
        List<Bebe> bebesCadastrados = ManipuladorArquivos.recuperarBebes("dados.json");
        return bebesCadastrados.stream().filter(bebe -> bebe.getCpf().equals(cpf)).findFirst();
    }

    public static List<Vacina> recuperarVacinas(String nomeArquivo) throws IOException {
        Map<String, List<?>> data = recuperarDados(nomeArquivo);
        List<Vacina> vacinas = (List<Vacina>) data.get("vacinas");
        return vacinas;
    }
}
