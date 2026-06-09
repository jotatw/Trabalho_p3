package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Responsável pelas regras de negócio relacionadas às locações.
// Valida cliente, veículo, usuário, datas e controla a transação da locação.
public class LocacaoService {

    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final LocacaoDAO locacaoDAO = new LocacaoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Conta quantas locações estão ativas.
    // Usado no card de resumo da tela Home.
    public int contarAtivas() throws SQLException {
        return locacaoDAO.contarAtivas();
    }

    // Lista um resumo das locações ativas.
    // Usado na tabela de locações em andamento da tela Home.
    public List<Locacao> listarAtivasResumo() throws SQLException {
        return locacaoDAO.listarAtivasResumo();
    }

    // Realiza uma nova locação.
    // A operação só é concluída se todas as validações forem aprovadas.
    public Locacao realizarLocacao(
            String cpf,
            String placa,
            LocalDate dataRetirada,
            LocalDate dataDevolucao,
            Usuario usuarioLogado
    ) throws SQLException {

        // A locação exige um usuário autenticado para registrar auditoria.
        if (usuarioLogado == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    null,
                    "Tentativa de locação sem usuário autenticado."
            );

            throw new IllegalArgumentException("É necessário estar autenticado para realizar uma locação.");
        }

        // Busca o cliente pelo CPF informado na tela.
        Cliente cliente = clienteService.buscarPorCpf(cpf);

        if (cliente == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Cliente não encontrado. CPF=" + cpf
            );

            throw new IllegalArgumentException("Cliente não encontrado para o CPF informado.");
        }

        // Clientes inativos não podem realizar novas locações.
        if (!cliente.isAtivo()) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Cliente inativo. CPF=" + cpf
            );

            throw new IllegalArgumentException("Cliente inativo não pode realizar locação.");
        }

        // Busca o veículo pela placa informada ou selecionada na tela.
        Veiculo veiculo = veiculoService.buscarPorPlaca(placa);

        if (veiculo == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Veículo não encontrado. PLACA=" + placa
            );

            throw new IllegalArgumentException("Veículo não encontrado para a placa informada.");
        }

        // Somente veículos disponíveis podem ser alugados.
        if (!veiculo.isDisponivel()) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Veículo indisponível. PLACA=" + placa
            );

            throw new IllegalArgumentException("Veículo não está disponível para locação.");
        }

        // As datas são obrigatórias para calcular o período da locação.
        if (dataRetirada == null || dataDevolucao == null) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Datas não informadas. Retirada=" + dataRetirada + ", Devolução=" + dataDevolucao
            );

            throw new IllegalArgumentException("Informe a data de retirada e a data de devolução.");
        }

        // Calcula a quantidade de dias entre a retirada e a previsão de devolução.
        long dias = ChronoUnit.DAYS.between(dataRetirada, dataDevolucao);

        if (dias <= 0) {
            LogUtil.registrar(
                    "LOCACAO_FALHOU",
                    usuarioLogado,
                    "Data inválida. Retirada=" + dataRetirada + ", Devolução=" + dataDevolucao
            );

            throw new IllegalArgumentException("Data de devolução deve ser posterior à data de retirada.");
        }

        // O valor total é calculado multiplicando os dias pelo valor da diária do veículo.
        double valorTotal = dias * veiculo.getValorLocacao();

        Locacao locacao = new Locacao(
                cliente,
                veiculo,
                usuarioLogado,
                dataRetirada,
                dataDevolucao,
                valorTotal
        );

        // A locação e a alteração de disponibilidade do veículo precisam ocorrer juntas.
        // Se uma delas falhar, o rollback desfaz toda a operação.
        try (Connection conn = DBConnection.getConexao()) {
            try {
                conn.setAutoCommit(false);

                locacaoDAO.salvar(conn, locacao);
                veiculoDAO.atualizarDisponivel(conn, false, veiculo.getId());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LogUtil.registrarErro("LocacaoService.realizarLocacao.transacao", usuarioLogado, e);
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

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