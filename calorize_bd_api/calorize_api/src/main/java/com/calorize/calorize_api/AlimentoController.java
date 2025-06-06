package com.calorize.calorize_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alimentos") 
public class AlimentoController {

    @Autowired
    private AlimentoRepository repository;

    // GET: lista todos os alimentos (Corresponde a GET /api/v1/foods)
    @GetMapping
    public List<Alimento> obterTodosAlimentos() { // Nome do metodo para corresponder ao ServicoApi
        return repository.findAll();
    }

    // GET: busca alimentos por nome (Corresponde a GET /api/v1/foods/search?name=...)
    @GetMapping("/search")
    public List<Alimento> buscarAlimentos(@RequestParam("name") String nomeAlimento) { // Nome do parâmetro "name" para corresponder ao ServicoApi
        return repository.findByNomeContainingIgnoreCase(nomeAlimento);
    }

}