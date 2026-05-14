package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LocacaoService {

    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final LocacaoDAO locacaoDAO = new LocacaoDAO();

    public Locacao realizarLocacao(
            String cpf,
            String placa,
            LocalDate dataRetirada,
            LocalDate dataDevolucao,
            Usuario usuarioLogado
    ) throws SQLException {

        Cliente cliente = clienteService.buscarPorCpf(cpf);
        if (cliente == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Cliente não encontrado. CPF=" + cpf
            );
            throw new IllegalArgumentException("Cliente não encontrado para o CPF informado.");
        }

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
        if (veiculo == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Veículo não encontrado. PLACA=" + placa
            );
            throw new IllegalArgumentException("Veículo não encontrado para a placa informada.");
        }

        if (!veiculo.isDisponivel()) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Veículo indisponível. PLACA=" + placa
            );
            throw new IllegalArgumentException("Veículo não está disponível para locação.");
        }

        long dias = ChronoUnit.DAYS.between(dataRetirada, dataDevolucao);
        if (dias <= 0) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Data inválida. Retirada=" + dataRetirada + ", Devolução=" + dataDevolucao
            );
            throw new IllegalArgumentException("Data de devolução deve ser posterior à data de retirada.");
        }

        double valorTotal = dias * veiculo.getValorLocacao();

        Locacao locacao = new Locacao(
                cliente,
                veiculo,
                usuarioLogado,
                dataRetirada,
                dataDevolucao,
                valorTotal
        );

        locacaoDAO.salvar(locacao);
        veiculoService.atualizarDisponivel(false, veiculo.getId());

        LogUtil.registrar(
                "LOCACAO_REALIZADA",
                usuarioLogado,
                "CPF=" + cpf +
                        ", PLACA=" + placa +
                        ", DIAS=" + dias +
                        ", VALOR_TOTAL=" + valorTotal
        );

        return locacao;
    }
}
