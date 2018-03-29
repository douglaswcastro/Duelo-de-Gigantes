package com.senai.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.senai.modelo.Duelo;

public class DueloDao {
	private Connection conexao;

	public DueloDao() {
		this.conexao = new ConnectionFactory().getConnection();
	}
	
	public void salvar(Duelo duelo) {
		String sql = "INSERT INTO duelo (nome1, imagem1, nome2, imagem2) VALUES (?,?,?,?)";
		try {
			PreparedStatement stm = conexao.prepareStatement(sql);
			
			stm.setString(1, duelo.getNome1());
			stm.setString(3, duelo.getNome2());
			stm.setString(2, duelo.getImagem1());
			stm.setString(4, duelo.getImagem2());
			
			stm.execute();
			stm.close();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}

	public Duelo buscarDueloRandomico() {
		Duelo d = new Duelo();
		String sql = "SELECT * FROM duelo ORDER BY RAND() LIMIT 1";
		try {
			PreparedStatement stm = conexao.prepareStatement(sql);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				d = preencherDuelo(rs);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}		
		return d;
	}
	
	public Duelo buscarDuelo(Integer id) {
		Duelo d = new Duelo();
		String sql = "SELECT * FROM duelo WHERE id = ?";
		try {
			PreparedStatement stm = conexao.prepareStatement(sql);
			stm.setInt(1, id);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				d = preencherDuelo(rs);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}		
		return d;
	}
	
	public Duelo registrarVoto(Integer id, String oponente) {
		String sql = "";
		if ("1".equals(oponente)) {
			sql = "UPDATE duelo SET vitorias1 = vitorias1 + 1 WHERE id = ?";
		} else if ("2".equals(oponente)){
			sql = "UPDATE duelo SET vitorias2 = vitorias2 + 1 WHERE id = ?";
		}
		
		try {
			PreparedStatement stm = conexao.prepareStatement(sql);
			stm.setInt(1, id);
			
			stm.execute();
			stm.close();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
		
		return buscarDuelo(id);
		
	}
	
	private Duelo preencherDuelo(ResultSet rs) throws SQLException{
		Duelo duelo = new Duelo();
		duelo.setId(rs.getInt("id"));
		duelo.setNome1(rs.getString("nome1"));
		duelo.setImagem1(rs.getString("imagem1"));
		duelo.setVitorias1(rs.getInt("vitorias1"));
		duelo.setNome2(rs.getString("nome2"));
		duelo.setImagem2(rs.getString("imagem2"));
		duelo.setVitorias2(rs.getInt("vitorias2"));
		return duelo;
	}

	
}
