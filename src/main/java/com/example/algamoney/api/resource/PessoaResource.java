package com.example.algamoney.api.resource;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
    public List<Pessoa> listar() {
        return pessoaRepository.findAll();
    }

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA')")
    public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
    public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
        Pessoa pessoa = pessoaRepository.findById(codigo).orElse(null);
        return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA')")
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void remover(@PathVariable Long codigo) {
        pessoaRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
        return ResponseEntity.ok(pessoaSalva);
    }

    // Atualizando o Status da Pessoa (Ativo ou Inativo)
    @PutMapping("/{codigo}/ativo")
    @PreAuthorize("hasAuthority('ROLE_CRIAR_PESSOA')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
    }

}
