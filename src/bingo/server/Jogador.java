package bingo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import bingo.view.TblBingo;

/**
 * @author Classe responsavel pelo Jogador
 *
 */
public class Jogador extends JFrame implements Runnable {
    //private static JTextField txtIP;

    static String nomeJogador = "";

    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dos;
    static TblBingo tb = new TblBingo();
    static List<Integer> bolasSorteadas = new ArrayList<>();
    public static String tabela;

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    static String msgComecaJogo = null;

    public static void main(String[] args) throws UnknownHostException, IOException {
        //Ele iniciará de fato quando todos os jogadores entrarem
        JOptionPane.showMessageDialog(null, "Aguarde um momento, o jogo já irá começar");
        nomeJogador = args[0];

        s = new Socket("127.0.0.1", 12345);
        dis = new DataInputStream(s.getInputStream());
        dos = new DataOutputStream(s.getOutputStream());

        // Recebe o numero da tabela sorteada
        tabela = dis.readUTF();

        // Convete o valor da tabela em um int e captura a tabela do banco de
        // dados
        tb.dao.selecionaTabela(Integer.parseInt(tabela));

        // altera o vetor numero com a cartela selecionada o banco de dados
        tb.setNumero(tb.dao.getNumero());
        
        //altera o id da tabela para poder torná-la disponível
        tb.setTabela(tb.dao.getId());

        // Exibe a tela para o jogador
        tb.montaTela();

        dos.writeUTF("Primeiro");
        new Thread(new Jogador()).start();

    }

    /**
     * Classe responsavel por aguardar respostas do servidor como as bolas
     * sorteadas, e se o jogador ganhou ou nao
     *
     * @throws IOException : Se nao for um numero valido
     */
    public static void escutaServidor() throws IOException {
        while (true) {
            tb.setTitle(nomeJogador);

            String msg = "";
            msg = dis.readUTF(); // Pega a mensagem do servidor;

            if (!msg.contains("ACABARAM AS BOLAS") && !msg.contains("BINGO") && !msg.contains("Saiu")
                    && !msg.contains("Você é o único Jogador, o jogo será encerrado")) {
                int msg2 = Integer.parseInt(msg);
                try {
                    tb.setVisible(true);

                    tb.getjButton26().setText("Bola Sorteada Número " + String.valueOf(msg2));

                    System.out.println(Integer.parseInt(msg));
                    bolasSorteadas.add(Integer.parseInt(msg));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, msg);
                }
            } else {
                String msg3 = nomeJogador + ": " + msg;
                JOptionPane.showMessageDialog(null, msg3);
                if (msg.contains("encerrado")) {
                    tb.dao.alterarSituacao(Integer.parseInt(tabela), "In");
                    System.exit(0);
                }
            }

        }
    }

    /**
     * Verifica se as bolas que estão na cartela já foram sorteadas pelo servidor
     *
     * @throws IOException : Numero Invalido
     */
    public static void verificarBolas() throws IOException {
        int total = 0;
        // System.out.println("Checa Bola");
        for (int i = 0; i < tb.getNumero().length; i++) {
            if (i != 12) {
                for (int j = 0; j < bolasSorteadas.size(); j++) {
                    if (Integer.parseInt(tb.getNumero()[i].trim()) == bolasSorteadas.get(j)) {
                        total++;
                    }
                }
            }
        }
        if (total >= 24) {
            tb.dao.alterarSituacao(Integer.parseInt(tabela), "In");
            dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(nomeJogador + " BINGO");
        } else {
            System.out.println(total);
            JOptionPane.showMessageDialog(null, "Mentira");
        }
    }

    /**
     * Para de escutar o servidor
     *
     * @throws IOException : valor inserido inv�lido
     */
    public static void fechar() throws IOException {
        dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF(nomeJogador + " Saiu");
    }

    @Override
    public void run() {
        try {
            escutaServidor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
