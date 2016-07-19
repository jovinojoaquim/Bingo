package bingo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe que faz a busca e a captura das tabelas no banco de dados
 *
 */
public class BuscarNoBD {
Conexao c = new Conexao();

    private String[] numero;
    private int id;
    //private List<Integer> numeroTabela = new ArrayList<Integer>();

    public String[] getNumero() {
        return numero;
    }

    public void setNumero(String[] numero) {
        this.numero = numero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    /**
     * Metodo que encontra a tabela para o jogador
     *
     * @param tabela : numero da tabela sorteada
     */
    public void selecionaTabela(int tabela) {
        try {
            Conexao c = new Conexao();
            //c.conexao();
            String sql = "SELECT * FROM cartela WHERE id = ?";

            PreparedStatement stmt = c.conexao().prepareStatement(sql);
 
            stmt.setInt(1, tabela);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String situacao = (rs.getString("situacao"));
//                if (situacao.equals("D")) {
                    String cartela = (rs.getString("numeros"));
                    numero = cartela.split(",");
                    id = rs.getInt("id");
                    alterarSituacao(tabela, situacao);

//                }
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alterarSituacao(int tabela, String situacao) {
            Conexao c = new Conexao();
        try {
            String sql = "UPDATE cartela SET situacao = ? WHERE id= ? ";

            PreparedStatement stmt = c.conexao().prepareStatement(sql);
            if(situacao.equals("D")){
                stmt.setString(1, "I");
                stmt.setInt(2, tabela);
                stmt.executeUpdate();
                stmt.close();
            }else if(situacao.equals("I")){
                if(tabela==10){
                    tabela =0;
                }
                selecionaTabela(tabela+1);
            }else{
                stmt.setString(1, "D");
                stmt.setInt(2, tabela);
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
