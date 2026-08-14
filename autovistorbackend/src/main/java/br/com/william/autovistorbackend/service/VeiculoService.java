package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.VeiculoCadastroRequest;
import br.com.william.autovistorbackend.dto.VeiculoResponse;
import br.com.william.autovistorbackend.entity.Cliente;
import br.com.william.autovistorbackend.entity.Veiculo;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.ClienteRepository;
import br.com.william.autovistorbackend.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public VeiculoResponse cadastrar(VeiculoCadastroRequest request) {

        if (veiculoRepository.existsByPlaca(request.placa())) {
            throw new RecursoDuplicadoException("Já existe um veículo cadastrado com esta placa.");
        }
        if (veiculoRepository.existsByChassi(request.chassi())) {
            throw new RecursoDuplicadoException("Já existe um veículo cadastrado com este chassi.");
        }

        int anoAtual = Year.now().getValue();
        if (request.anoVeiculo() > anoAtual + 1) {
            throw new IllegalArgumentException("Ano do veículo não pode ser maior que " + (anoAtual + 1) + ".");
        }

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente informado não existe."));

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(request.placa().toUpperCase());
        veiculo.setTipoVeiculo(request.tipoVeiculo());
        veiculo.setNomeVeiculo(request.nomeVeiculo());
        veiculo.setModelo(request.modelo());
        veiculo.setAnoVeiculo(request.anoVeiculo());
        veiculo.setChassi(request.chassi().toUpperCase());
        veiculo.setObservacoes(request.observacoes());
        veiculo.setCliente(cliente);

        Veiculo salvo = veiculoRepository.save(veiculo);
        return toResponse(salvo);
    }

    public VeiculoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    public List<VeiculoResponse> listarPorCliente(Long idCliente) {
        return veiculoRepository.findByClienteId(idCliente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Veiculo buscarEntidadePorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));
    }

    private VeiculoResponse toResponse(Veiculo v) {
        return new VeiculoResponse(
                v.getId(), v.getPlaca(), v.getTipoVeiculo(), v.getNomeVeiculo(),
                v.getModelo(), v.getAnoVeiculo(), v.getChassi(), v.getObservacoes(),
                v.getCliente().getId()
        );
    }
}