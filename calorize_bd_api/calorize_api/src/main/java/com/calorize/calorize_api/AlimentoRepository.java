package com.calorize.calorize_api;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {
    List<Alimento> findByNomeContainingIgnoreCase(String nome);


}

