package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;

import java.sql.*;

public class LocacaoDAO extends BaseDAO {
    public void salvar(Locacao locacao) throws SQLException {
        String sql = """
            INSERT INTO locacao (cliente_id, veiculo_id, usuario_id, dt_retirada, dt_devolucao_prevista, valor_total, multas, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, locacao.getCliente().getId());
            stmt.setInt(2, locacao.getVeiculo().getId());
            stmt.setInt(3, locacao.getUsuario().getId());
            stmt.setDate(4, Date.valueOf(locacao.getDataRetirada()));
            stmt.setDate(5, Date.valueOf(locacao.getDataDevolucaoPrevista()));
            stmt.setDouble(6, locacao.getValorTotal());
            stmt.setDouble(7, locacao.getMultas());
            stmt.setString(8, locacao.getStatus());
            stmt.executeUpdate();

        } catch (SQLException e) {
            registrarErro("LocacaoDAO.salvar", e);
            throw e;
        }
    }

    public void atualizarDevolucao(Locacao locacao) throws SQLException {
        String sql = """
            UPDATE locacao SET dt_devolucao_real = ?, multas = ?, status = ? WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setDate(1, Date.valueOf(locacao.getDataDevolucaoReal()));
            stmt.setDouble(2, locacao.getMultas());
            stmt.setString(3, locacao.getStatus());
            stmt.setInt(4, locacao.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            registrarErro("LocacaoDAO.atualizarDevolucao", e);
            throw e;
        }
    }

    public Locacao buscarLocacaoAtivaPorPlaca(String placa) throws SQLException {
        // Busca uma locação ativa usando a placa do veículo.
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
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return mapearLocacao(rs);
                }
            }
        }catch (SQLException e) {
            registrarErro("LocacaoDAO.buscarLocacaoAtivaPorPlaca", e);
            throw e;
        }
        return null;
    }
    private Locacao mapearLocacao(ResultSet rs) throws SQLException {
        Locacao locacao = new Locacao();

        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNome(rs.getString("cliente_nome"));
        locacao.setCliente(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getInt("veiculo_id"));
        locacao.setVeiculo(veiculo);

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
