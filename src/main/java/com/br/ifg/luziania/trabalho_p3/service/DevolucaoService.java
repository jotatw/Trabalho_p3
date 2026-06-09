package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Responsável pelas regras de negócio relacionadas à devolução de veículos.
// Controla busca da locação ativa, cálculo de multa e liberação do veículo.
public class DevolucaoService {

    private final LocacaoDAO locacaoDAO = new LocacaoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Busca uma locação ativa pela placa do veículo.
    // Usado para preencher o resumo da devolução antes da confirmação.
    public Locacao buscarLocacaoAtivaPorPlaca(String placa) throws SQLException {
        return locacaoDAO.buscarLocacaoAtivaPorPlaca(placa);
    }

    // Lista as placas dos veículos que possuem locação ativa.
    // Usado no ComboBox da tela de devolução.
    public List<String> listarPlacasComLocacaoAtiva() throws SQLException {
        return locacaoDAO.listaPlacasComLocacaoAtiva();
    }

    // Registra a devolução de um veículo.
    // A operação calcula a multa, encerra a locação e libera o veículo.
    public Locacao registrarDevolucao(String placa) throws SQLException {
        Locacao locacao = locacaoDAO.buscarLocacaoAtivaPorPlaca(placa);

        // Não é possível devolver um veículo sem locação ativa.
        if (locacao == null) {
            LogUtil.registrarAcao(
                    "DEVOLUCAO_FALHOU",
                    "Nenhuma locação ativa encontrada. PLACA=" + placa
            );

            throw new IllegalArgumentException(
                    "Nenhuma locação ativa encontrada para essa placa informada."
            );
        }

        LocalDate hoje = LocalDate.now();
        locacao.setDataDevolucaoReal(hoje);

        // Calcula a quantidade de dias de atraso.
        // Se o resultado for menor ou igual a zero, não há multa.
        long diasAtraso = ChronoUnit.DAYS.between(
                locacao.getDataDevolucaoPrevista(),
                hoje
        );

        double multa = 0.0;

        if (diasAtraso > 0) {
            // Calcula o período original da locação para descobrir o valor da diária.
            long diasLocacao = ChronoUnit.DAYS.between(
                    locacao.getDataRetirada(),
                    locacao.getDataDevolucaoPrevista()
            );

            if (diasLocacao <= 0) {
                LogUtil.registrarAcao(
                        "DEVOLUCAO_FALHOU",
                        "Período de locação inválido. PLACA=" + placa
                );

                throw new IllegalArgumentException("Período de locação inválido.");
            }

            // Regra de multa: 20% do valor da diária para cada dia de atraso.
            double valorDiaria = locacao.getValorTotal() / diasLocacao;
            multa = diasAtraso * valorDiaria * 0.20;
        }

        locacao.setMultas(multa);
        locacao.setStatus("ENCERRADA");

        // A devolução e a liberação do veículo devem ocorrer na mesma transação.
        // Se uma operação falhar, o rollback evita inconsistência no banco.
        try (Connection conn = DBConnection.getConexao()) {
            try {
                conn.setAutoCommit(false);

                locacaoDAO.atualizarDevolucao(conn, locacao);
                veiculoDAO.atualizarDisponivel(conn, true, locacao.getVeiculo().getId());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LogUtil.registrarErro("DevolucaoService.registrarDevolucao.transacao", null, e);
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        LogUtil.registrarAcao(
                "DEVOLUCAO_REALIZADA",
                "PLACA=" + placa +
                        ", MULTA=" + multa +
                        ", DATA_DEVOLUCAO=" + hoje
        );

        return locacao;
    }
}