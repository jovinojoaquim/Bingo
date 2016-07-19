package bingo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsavel por fazer conexao ao SQL-Server
 *
 */
public class Conexao {

    Connection conexao;

    /**
     * @
     * Metodo respons�vel por fazer conexao ao banco de dados
     * @return Conexao com o banco de dados
     */
    public Connection conexao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexao = DriverManager.getConnection("jdbc:mysql://localhost:3306/bingo" , "root", "");
            System.out.println("Conectado");
            return conexao;
        } catch (ClassNotFoundException | SQLException e) {
        }
        return conexao;
    }
    public static void main(String[] args) {
        Conexao c = new Conexao();
        System.out.println(c.conexao());
        
    }
}
