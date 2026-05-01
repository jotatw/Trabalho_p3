package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VeiculoDAO {
    //salva um novo veiculo
    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, modelo, marca, categoria, valor_diaria) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, veiculo.getPlaca());
        stmt.setString(2, veiculo.getModelo());
        stmt.setString(3, veiculo.getMarca());
        stmt.setString(4, veiculo.getCategoria());
        stmt.setDouble(5, veiculo.getValorLocacao());
        stmt.executeUpdate();
    }
    //busca um veiculo a partir da placa
    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM veiculo WHERE placa = ?";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, placa);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Veiculo v = new Veiculo();
            v.setId(rs.getInt("id"));
            v.setPlaca(rs.getString("placa"));
            v.setModelo(rs.getString("modelo"));
            v.setMarca(rs.getString("marca"));
            v.setCategoria(rs.getString("categoria"));
            v.setValorLocacao(rs.getDouble("valor_diaria"));
            v.setDisponivel(rs.getBoolean("disponivel"));
            return v;
        }
        return null;
    }
}
