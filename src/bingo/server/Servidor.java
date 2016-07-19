package bingo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

/**
 * Classe servidor, responsável por receber e enviar os dados para os jogadores
 * e também do controle de fluxo de informações
 */
public class Servidor extends Thread {

    static ServerSocket ss;
    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dos;
    static List<Socket> clientes = new ArrayList<>();
    static List<Integer> numerosSorteados = new ArrayList<Integer>();
    static int contadorBolasSorteadas = 0;
    static int contadorClientes = 0;
    static int quantidadeJogadores;
    static List<Integer> cartelas = new ArrayList<>();

    public static void main(String[] args) {
        misturarCartelas();
        quantidadeJogadores = Integer.parseInt(args[0]);
        try {
            misturarBolas();
            ss = new ServerSocket(12345);
            JOptionPane.showMessageDialog(null, "Servidor Iniciado");
            while (true) {
                //o accept aguarda o jogador entrar, quando o jogador entrar ele vai pra linha seguinte
                s = ss.accept();
                mandarCartelaParaJogadores();
                clientes.add(s);
                contadorClientes++;
                Thread t = new Servidor();
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo responsável por misturarCartelas
     */
    private static void misturarCartelas() {
        for (int i = 1; i <=10; i++) {
            cartelas.add(i);
        }
        Collections.shuffle(cartelas);
    }

    /**
     * Metodo que envia a cartla para o Jogador
     *
     * @throws IOException
     */
    static void mandarCartelaParaJogadores() throws IOException {
        dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF(String.valueOf(cartelas.get(contadorClientes)));
        dos.flush();
    }

    @Override
    public void run() {
        try {

            String mensagem;
            dis = new DataInputStream(s.getInputStream());
            mensagem = dis.readUTF();

            Timer timer = new Timer();
            while (!mensagem.contains("BINGO") && mensagem != null && !mensagem.equals("Nao Houve Ganhador")
                    && !mensagem.contains("Saiu")) {

                if (contadorClientes >= quantidadeJogadores) {

//		Timer é o respons�vel por enviar as bolas de tempos em tempos
//		o numero 1000 ele vai levar 1 seguno pra abrir a cartela
//		o numero 7000 vai mandar as bolas pros quantidadeJogadores a cada 5 segundos
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                if (contadorBolasSorteadas < 75) {
                                    mandarMensagemParaJogadores(String.valueOf(numerosSorteados.get(contadorBolasSorteadas)));
                                    contadorBolasSorteadas++;
                                }
                                if (contadorBolasSorteadas == 75) {
                                    mandarMensagemParaJogadores("ACABARAM AS BOLAS");
                                    contadorBolasSorteadas++;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000, 10000);
                }
                mensagem = dis.readUTF();
            }
//			Se o jogador clicar em BINGO e estiver correto, ele entra neste If
//			e cancela o timer criado caso contr�rio, o jogo continua rodando
            if (mensagem.contains("BINGO")) {
                contadorBolasSorteadas--;
                mandarMensagemParaJogadores(mensagem);
                timer.cancel();
            }

            if (mensagem.contains(" Saiu")) {
                contadorClientes--;
                if (contadorClientes > 1) {
                    mandarMensagemParaJogadores(mensagem + "!Restam " + contadorClientes + " jogadores");
                } else {
                    mandarMensagemParaJogadores("Você é o único Jogador, o jogo será encerrado");
                    timer.cancel();
                }
            }

            System.out.println("Parou");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Envia dados para todos os jogadores presentes
     *
     * @param mensagem : mensagem que será enviada aos jogadores
     * @throws IOException
     */
    static void mandarMensagemParaJogadores(String mensagem) throws IOException {
        if (clientes.size() >= 0) {
            for (Socket socket : clientes) {
                try {
                    dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(mensagem);
                    dos.flush();
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    /**
     * Recebe mensagem dos quantidadeJogadores
     *
     * @throws IOException
     */
    void recebeMensagem() throws IOException {

        dis = new DataInputStream(s.getInputStream());
        String msg = dis.readUTF();
    }

    /**
     * Mistura as bolas
     */
    private static void misturarBolas() {
        for (int i = 1; i <= 75; i++) {
            numerosSorteados.add(i);
        }
        Collections.shuffle(numerosSorteados);
    }

    /**
     * Remove o jogador do servidor
     *
     * @param socket Jogador
     * @throws IOException
     */
    void remove(Socket socket) throws IOException {
        clientes.remove(socket);
    }
}
