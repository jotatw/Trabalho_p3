package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Responsável pelo acesso à tabela locacao.
// Contém operações de cadastro, devolução, busca e listagem de locações.
public class LocacaoDAO extends BaseDAO {

    // Salva uma locação abrindo uma conexão própria.
    // Esse método pode ser usado em operações simples fora de uma transação externa.
    public void salvar(Locacao locacao) throws SQLException {
        String sql = """
            INSERT INTO locacao (
                cliente_id,
                veiculo_id,
                usuario_id,
                dt_retirada,
                dt_devolucao_prevista,
                valor_total,
                multas,
                status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            preencherParametrosLocacao(stmt, locacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.salvar", e);
            throw e;
        }
    }

    // Salva uma locação usando uma conexão já aberta.
    // Esse método é usado dentro da transação da locação.
    public void salvar(Connection conn, Locacao locacao) throws SQLException {
        String sql = """
            INSERT INTO locacao (
                cliente_id,
                veiculo_id,
                usuario_id,
                dt_retirada,
                dt_devolucao_prevista,
                valor_total,
                multas,
                status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametrosLocacao(stmt, locacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.salvarTransacao", e);
            throw e;
        }
    }

    // Atualiza os dados de devolução abrindo uma conexão própria.
    // Pode ser usado quando a atualização não faz parte de uma transação externa.
    public void atualizarDevolucao(Locacao locacao) throws SQLException {
        String sql = """
            UPDATE locacao
            SET dt_devolucao_real = ?, multas = ?, status = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            preencherParametrosDevolucao(stmt, locacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.atualizarDevolucao", e);
            throw e;
        }
    }

    // Atualiza os dados de devolução usando uma conexão já aberta.
    // Esse método é usado dentro da transação de devolução.
    public void atualizarDevolucao(Connection conn, Locacao locacao) throws SQLException {
        String sql = """
            UPDATE locacao
            SET dt_devolucao_real = ?, multas = ?, status = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametrosDevolucao(stmt, locacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.atualizarDevolucaoTransacao", e);
            throw e;
        }
    }

    // Busca uma locação ativa usando a placa do veículo.
    // Usado na tela de devolução para localizar o contrato em aberto.
    public Locacao buscarLocacaoAtivaPorPlaca(String placa) throws SQLException {
        String sql = """
            SELECT l.id,
                   l.cliente_id,
                   c.nome AS cliente_nome,
                   l.veiculo_id,
                   v.placa AS veiculo_placa,
                   v.modelo AS veiculo_modelo,
                   l.usuario_id,
                   l.dt_retirada,
                   l.dt_devolucao_prevista,
                   l.dt_devolucao_real,
                   l.valor_total,
                   l.multas,
                   l.status
            FROM locacao l
            JOIN cliente c ON l.cliente_id = c.id
            JOIN veiculo v ON l.veiculo_id = v.id
            WHERE v.placa = ?
              AND l.status = 'ATIVA'
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, placa);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearLocacao(rs);
                }
            }
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.buscarLocacaoAtivaPorPlaca", e);
            throw e;
        }

        return null;
    }

    // Lista as placas dos veículos que possuem locação ativa.
    // Usado no ComboBox da tela de devolução.
    public List<String> listaPlacasComLocacaoAtiva() throws SQLException {
        String sql = """
            SELECT v.placa
            FROM locacao l
            JOIN veiculo v ON l.veiculo_id = v.id
            WHERE l.status = 'ATIVA'
            ORDER BY v.placa
            """;

        List<String> placas = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                placas.add(rs.getString("placa"));
            }
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.listarPlacasComLocacaoAtiva", e);
            throw e;
        }

        return placas;
    }

    // Conta quantas locações estão ativas.
    // Usado no dashboard da tela Home.
    public int contarAtivas() throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total
            FROM locacao
            WHERE status = 'ATIVA'
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.contarAtivas", e);
            throw e;
        }

        return 0;
    }

    // Lista um resumo das locações ativas.
    // Usado na tabela de locações em andamento da tela Home.
    public List<Locacao> listarAtivasResumo() throws SQLException {
        String sql = """
            SELECT l.id,
                   l.cliente_id,
                   c.nome AS cliente_nome,
                   l.veiculo_id,
                   v.placa AS veiculo_placa,
                   v.modelo AS veiculo_modelo,
                   l.usuario_id,
                   l.dt_retirada,
                   l.dt_devolucao_prevista,
                   l.dt_devolucao_real,
                   l.valor_total,
                   l.multas,
                   l.status
            FROM locacao l
            JOIN cliente c ON l.cliente_id = c.id
            JOIN veiculo v ON l.veiculo_id = v.id
            WHERE l.status = 'ATIVA'
            ORDER BY l.dt_devolucao_prevista
            LIMIT 5
            """;

        List<Locacao> locacoes = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                locacoes.add(mapearLocacao(rs));
            }
        } catch (SQLException e) {
            registrarErro("LocacaoDAO.listarAtivasResumo", e);
            throw e;
        }

        return locacoes;
    }

    // Preenche os parâmetros comuns usados ao inserir uma locação.
    private void preencherParametrosLocacao(PreparedStatement stmt, Locacao locacao) throws SQLException {
        stmt.setInt(1, locacao.getCliente().getId());
        stmt.setInt(2, locacao.getVeiculo().getId());
        stmt.setInt(3, locacao.getUsuario().getId());
        stmt.setDate(4, Date.valueOf(locacao.getDataRetirada()));
        stmt.setDate(5, Date.valueOf(locacao.getDataDevolucaoPrevista()));
        stmt.setDouble(6, locacao.getValorTotal());
        stmt.setDouble(7, locacao.getMultas());
        stmt.setString(8, locacao.getStatus());
    }

    // Preenche os parâmetros comuns usados ao registrar uma devolução.
    private void preencherParametrosDevolucao(PreparedStatement stmt, Locacao locacao) throws SQLException {
        stmt.setDate(1, Date.valueOf(locacao.getDataDevolucaoReal()));
        stmt.setDouble(2, locacao.getMultas());
        stmt.setString(3, locacao.getStatus());
        stmt.setInt(4, locacao.getId());
    }

    // Converte uma linha retornada pelo ResultSet em um objeto Locacao.
    // O mapeamento inclui objetos parciais de Cliente, Veiculo e Usuario.
    private Locacao mapearLocacao(ResultSet rs) throws SQLException {
        Locacao locacao = new Locacao();

        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNome(rs.getString("cliente_nome"));

        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getInt("veiculo_id"));
        veiculo.setPlaca(rs.getString("veiculo_placa"));
        veiculo.setModelo(rs.getString("veiculo_modelo"));

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id"));

        locacao.setId(rs.getInt("id"));
        locacao.setCliente(cliente);
        locacao.setVeiculo(veiculo);
        locacao.setUsuario(usuario);
        locacao.setDataRetirada(rs.getDate("dt_retirada").toLocalDate());
        locacao.setDataDevolucaoPrevista(rs.getDate("dt_devolucao_prevista").toLocalDate());

        Date dataDevolucaoReal = rs.getDate("dt_devolucao_real");

        if (dataDevolucaoReal != null) {
            locacao.setDataDevolucaoReal(dataDevolucaoReal.toLocalDate());
        }

        locacao.setValorTotal(rs.getDouble("valor_total"));
        locacao.setMultas(rs.getDouble("multas"));
        locacao.setStatus(rs.getString("status"));

        return locacao;
    }
}