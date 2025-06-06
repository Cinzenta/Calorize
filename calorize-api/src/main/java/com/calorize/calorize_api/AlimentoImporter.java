package com.calorize.calorize_api;

import com.calorize.calorize_api.Alimento;
import com.calorize.calorize_api.AlimentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class AlimentoImporter implements CommandLineRunner {
    private final AlimentoRepository alimentoRepository;

    public AlimentoImporter(AlimentoRepository alimentoRepository) {
        this.alimentoRepository = alimentoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("alimentos.csv");
        BufferedReader leitor = new BufferedReader(new InputStreamReader(inputStream));

        String linha;

        leitor.readLine(); // pula o cabeçalho

        while ((linha = leitor.readLine()) != null) {
            String[] partes = linha.split(";");

            if (partes.length < 6) continue;

            Alimento alimento = new Alimento();
            alimento.setNome(partes[0].trim().replace("\"", ""));
            alimento.setPorcao(partes[1].trim().replace("\"", ""));
            alimento.setCalorias(Double.parseDouble(partes[2].replace(",", ".")));
            alimento.setCarboidratos(Double.parseDouble(partes[3].replace(",", ".")));
            alimento.setProteinas(Double.parseDouble(partes[4].replace(",", ".")));
            alimento.setGorduras(Double.parseDouble(partes[5].replace(",", ".")));

            alimentoRepository.save(alimento);
        }

        leitor.close();
        System.out.println("Alimentos importados com sucesso.");
    }
}

