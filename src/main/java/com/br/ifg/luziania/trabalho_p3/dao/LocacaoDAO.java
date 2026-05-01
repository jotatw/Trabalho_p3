package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;

import java.sql.*;

public class LocacaoDAO {
    public void salvar(Locacao locacao) throws SQLException {
        String sql = "INSERT INTO locacao (cliente_id, veiculo_id, usuario_id, dt_retirada, dt_devolucao_prevista, valor_total) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, locacao.getCliente().getId());
        stmt.setInt(2, locacao.getVeiculo().getId());
        stmt.setInt(3, locacao.getUsuario().getId());
        stmt.setDate(4, Date.valueOf(locacao.getDataRetirada()));
        stmt.setDate(5, Date.valueOf(locacao.getDataDevolucaoPrevista()));
        stmt.setDouble(6, locacao.getValorTotal());
        stmt.executeUpdate();
    }

    public void atualizarDevolucao(Locacao locacao) throws SQLException {
        String sql = "UPDATE locacao SET dt_devolucao_real = ?, multa = ?, status = ? WHERE id = ?";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(locacao.getDataDevolucaoReal()));
        stmt.setDouble(2, locacao.getMultas());
        stmt.setString(3, locacao.getStatus());
        stmt.setInt(4, locacao.getId());
        stmt.executeUpdate();
    }

    public Locacao buscarLocacaoAtivaPorPlaca(String placa) throws SQLException {
        //busca pela tabela placa em outra database a partir do join
        String sql = """
            SELECT l.* FROM locacao l
            JOIN veiculo v ON l.veiculo_id = v.id
            WHERE v.placa = ? AND v.status = 'ATIVO'
        """;

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, placa);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Locacao l = new Locacao();
            l.setId(rs.getInt("id"));
            l.setValorTotal(rs.getDouble("valor_total"));
            l.setStatus(rs.getString("status"));
            l.setDataRetirada(rs.getDate("dt_retirada").toLocalDate());
            l.setDataDevolucaoPrevista(rs.getDate("dt_devolucao_prevista").toLocalDate());
            return l;
        }
        return null;
    }
}
