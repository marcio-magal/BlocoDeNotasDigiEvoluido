import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BlocoDeNotasDigiEvoluido extends JFrame {
    private JTextArea areaDeTexto;
    private JFileChooser seletorDeArquivo;
    private File arquivoAtual;
    private JLabel barraDeStatus; // Barra de informações

    public BlocoDeNotasDigiEvoluido() {
        super("Bloco de Notas Simples");

        areaDeTexto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaDeTexto);
        add(scrollPane, BorderLayout.CENTER);

        seletorDeArquivo = new JFileChooser();

        // Criar barra de menus
        JMenuBar menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemSalvar = new JMenuItem("Salvar");
        JMenuItem itemSalvarComo = new JMenuItem("Salvar Como...");
        JMenuItem itemSair = new JMenuItem("Sair");

        menuArquivo.add(itemAbrir);
        menuArquivo.add(itemSalvar);
        menuArquivo.add(itemSalvarComo);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        // Menu Editar
        JMenu menuEditar = new JMenu("Editar");
        JMenuItem itemLimpar = new JMenuItem("Limpar");
        JMenuItem itemLocalizar = new JMenuItem("Localizar...");
        JMenuItem itemLocalizarSubstituir = new JMenuItem("Localizar e Substituir...");

        menuEditar.add(itemLimpar);
        menuEditar.add(itemLocalizar);
        menuEditar.add(itemLocalizarSubstituir);

        // Adiciona menus na barra
        menuBar.add(menuArquivo);
        menuBar.add(menuEditar);

        // Coloca barra de menus na janela
        setJMenuBar(menuBar);

        // ---- Barra de Status ----
        barraDeStatus = new JLabel("Caracteres: 0 | Palavras: 0");
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelStatus.add(barraDeStatus);
        add(painelStatus, BorderLayout.SOUTH);

        // Listener para atualizar barra de status sempre que o texto mudar
        areaDeTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarBarraDeStatus();
            }
        });

        // Ações do menu Arquivo
        itemAbrir.addActionListener(e -> abrirArquivo());
        itemSalvar.addActionListener(e -> salvarArquivo());
        itemSalvarComo.addActionListener(e -> salvarArquivoComo());
        itemSair.addActionListener(e -> System.exit(0));

        // Ações do menu Editar
        itemLimpar.addActionListener(e -> confirmarLimpar());
        itemLocalizar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função Localizar ainda não implementada."));
        itemLocalizarSubstituir.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função Localizar e Substituir ainda não implementada."));

        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void confirmarLimpar() {
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente limpar o texto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            areaDeTexto.setText("");
            atualizarBarraDeStatus();
        }
    }

    private void atualizarBarraDeStatus() {
        String texto = areaDeTexto.getText();
        int caracteres = texto.length();

        // Contar palavras (separadas por espaço, ignorando múltiplos espaços)
        String[] palavras = texto.trim().isEmpty() ? new String[0] : texto.trim().split("\\s+");
        int qtdPalavras = palavras.length;

        barraDeStatus.setText("Caracteres: " + caracteres + " | Palavras: " + qtdPalavras);
    }

    private void abrirArquivo() {
        if (seletorDeArquivo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            arquivoAtual = seletorDeArquivo.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoAtual))) {
                areaDeTexto.read(reader, null);
                atualizarBarraDeStatus();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarArquivo() {
        if (arquivoAtual != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoAtual))) {
                areaDeTexto.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            salvarArquivoComo();
        }
    }

    private void salvarArquivoComo() {
        if (seletorDeArquivo.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            arquivoAtual = seletorDeArquivo.getSelectedFile();
            salvarArquivo();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlocoDeNotasDigiEvoluido::new);
    }
}
