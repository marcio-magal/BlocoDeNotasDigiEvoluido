import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BlocoDeNotasDigiEvoluido extends JFrame {
    private JTextArea areaDeTexto;
    private JFileChooser seletorDeArquivo;
    private File arquivoAtual;
    private JLabel barraDeStatus;

    // Controle de alterações
    private boolean alterado = false;

    public BlocoDeNotasDigiEvoluido() {
        super("Bloco de Notas Simples");

        areaDeTexto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaDeTexto);
        add(scrollPane, BorderLayout.CENTER);

        seletorDeArquivo = new JFileChooser();

        // ---- Barra de Menus ----
        JMenuBar menuBar = new JMenuBar();

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

        JMenu menuEditar = new JMenu("Editar");
        JMenuItem itemLimpar = new JMenuItem("Limpar");
        JMenuItem itemLocalizar = new JMenuItem("Localizar...");
        JMenuItem itemLocalizarSubstituir = new JMenuItem("Localizar e Substituir...");
        menuEditar.add(itemLimpar);
        menuEditar.add(itemLocalizar);
        menuEditar.add(itemLocalizarSubstituir);

        menuBar.add(menuArquivo);
        menuBar.add(menuEditar);
        setJMenuBar(menuBar);

        // ---- Barra de Status ----
        barraDeStatus = new JLabel("Caracteres: 0 | Palavras: 0");
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelStatus.add(barraDeStatus);
        add(painelStatus, BorderLayout.SOUTH);

        // Listener para atualizações
        areaDeTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarBarraDeStatus();
                setAlterado(true);
            }
        });

        // Ações do menu Arquivo
        itemAbrir.addActionListener(e -> {
            if (verificarAlteracoesNaoSalvas()) abrirArquivo();
        });
        itemSalvar.addActionListener(e -> salvarArquivo());
        itemSalvarComo.addActionListener(e -> salvarArquivoComo());
        itemSair.addActionListener(e -> {
            if (verificarAlteracoesNaoSalvas()) System.exit(0);
        });

        // Ações do menu Editar
        itemLimpar.addActionListener(e -> confirmarLimpar());
        itemLocalizar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função Localizar ainda não implementada."));
        itemLocalizarSubstituir.addActionListener(e -> JOptionPane.showMessageDialog(this, "Função Localizar e Substituir ainda não implementada."));

        // Ao fechar a janela -> verificar alterações
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (verificarAlteracoesNaoSalvas()) {
                    dispose();
                }
            }
        });

        setSize(600, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void confirmarLimpar() {
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente limpar o texto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            areaDeTexto.setText("");
            atualizarBarraDeStatus();
            setAlterado(true);
        }
    }

    private void atualizarBarraDeStatus() {
        String texto = areaDeTexto.getText();
        int caracteres = texto.length();
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
                setAlterado(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarArquivo() {
        if (arquivoAtual != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoAtual))) {
                areaDeTexto.write(writer);
                setAlterado(false);
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

    // --- Controle de alterações ---
    private void setAlterado(boolean alterado) {
        this.alterado = alterado;
        atualizarTitulo();
    }

    private void atualizarTitulo() {
        String nomeArquivo = (arquivoAtual != null) ? arquivoAtual.getName() : "Sem título";
        if (alterado) {
            setTitle(nomeArquivo + "* - Bloco de Notas");
        } else {
            setTitle(nomeArquivo + " - Bloco de Notas");
        }
    }

    private boolean verificarAlteracoesNaoSalvas() {
        if (!alterado) return true;

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "O arquivo foi modificado. Deseja salvar as alterações?",
                "Alterações não salvas",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            salvarArquivo();
            return true;
        } else if (resposta == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false; // Cancelar
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlocoDeNotasDigiEvoluido::new);
    }
}
