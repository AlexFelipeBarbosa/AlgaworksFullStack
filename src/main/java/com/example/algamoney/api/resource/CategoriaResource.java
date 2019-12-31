package com.example.algamoney.api.resource;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
   /*
   public ResponseEntity<?> listar() {
          List<Categoria> categorias = categoriaRepository.findAll();
        return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.notFound().build();
    */
    }

    @PostMapping
    public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(categoriaSalva.getCodigo()).toUri();
        response.setHeader("Location", uri.toASCIIString());

        return ResponseEntity.created(uri).body(categoriaSalva);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscarPeloCodigo(@PathVariable("codigo") Long codigo) {
        Categoria categoria = categoriaRepository.findById(codigo).orElse(null);
        // Retornar 404 Not Found caso não exista a categoria
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(categoria);
    }
}

