import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BlocoDeNotasDigiEvoluido extends JFrame {
    private JTextArea areaDeTexto;
    private JFileChooser seletorDeArquivo;
    private File arquivoAtual;
    private JLabel barraDeStatus;
    private boolean alterado = false;

    // Controle de busca
    private int ultimaPosicaoEncontrada = -1;
    private String ultimaBusca = "";

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

        // Listener de alterações
        areaDeTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarBarraDeStatus();
                setAlterado(true);
            }
        });

        // Ações Arquivo
        itemAbrir.addActionListener(e -> { if (verificarAlteracoesNaoSalvas()) abrirArquivo(); });
        itemSalvar.addActionListener(e -> salvarArquivo());
        itemSalvarComo.addActionListener(e -> salvarArquivoComo());
        itemSair.addActionListener(e -> { if (verificarAlteracoesNaoSalvas()) System.exit(0); });

        // Ações Editar
        itemLimpar.addActionListener(e -> confirmarLimpar());
        itemLocalizar.addActionListener(e -> localizarTexto());
        itemLocalizarSubstituir.addActionListener(e -> abrirJanelaLocalizarSubstituir());

        // Verificação ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (verificarAlteracoesNaoSalvas()) dispose();
            }
        });

        setSize(600, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Localizar ---
    private void localizarTexto() {
        String termo = JOptionPane.showInputDialog(this, "Digite o termo a localizar:");
        if (termo != null && !termo.isEmpty()) {
            ultimaBusca = termo;
            ultimaPosicaoEncontrada = areaDeTexto.getText().indexOf(termo);

            if (ultimaPosicaoEncontrada >= 0) {
                areaDeTexto.requestFocus();
                areaDeTexto.select(ultimaPosicaoEncontrada, ultimaPosicaoEncontrada + termo.length());
            } else {
                JOptionPane.showMessageDialog(this, "Termo não encontrado.");
            }
        }
    }

    // --- Localizar e Substituir ---
    private void abrirJanelaLocalizarSubstituir() {
        JDialog dialogo = new JDialog(this, "Localizar e Substituir", true);
        dialogo.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField campoLocalizar = new JTextField();
        JTextField campoSubstituir = new JTextField();

        JButton btnLocalizar = new JButton("Localizar Próximo");
        JButton btnSubstituir = new JButton("Substituir");
        JButton btnSubstituirTodos = new JButton("Substituir Todos");

        dialogo.add(new JLabel("Localizar:"));
        dialogo.add(campoLocalizar);
        dialogo.add(new JLabel("Substituir por:"));
        dialogo.add(campoSubstituir);
        dialogo.add(btnLocalizar);
        dialogo.add(btnSubstituir);
        dialogo.add(btnSubstituirTodos);

        // Ação Localizar Próximo
        btnLocalizar.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            if (!termo.isEmpty()) {
                String texto = areaDeTexto.getText();
                ultimaPosicaoEncontrada = texto.indexOf(termo, areaDeTexto.getCaretPosition());

                if (ultimaPosicaoEncontrada >= 0) {
                    areaDeTexto.requestFocus();
                    areaDeTexto.select(ultimaPosicaoEncontrada, ultimaPosicaoEncontrada + termo.length());
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Nenhuma ocorrência encontrada.");
                }
            }
        });

        // Ação Substituir
        btnSubstituir.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            String substituto = campoSubstituir.getText();
            if (!termo.isEmpty() && areaDeTexto.getSelectedText() != null &&
                areaDeTexto.getSelectedText().equals(termo)) {

                areaDeTexto.replaceSelection(substituto);
                setAlterado(true);
            }
        });

        // Ação Substituir Todos
        btnSubstituirTodos.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            String substituto = campoSubstituir.getText();
            if (!termo.isEmpty()) {
                String texto = areaDeTexto.getText();
                texto = texto.replaceAll(termo, substituto);
                areaDeTexto.setText(texto);
                setAlterado(true);
                atualizarBarraDeStatus();
            }
        });

        dialogo.setSize(400, 150);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    // --- Confirmar limpar ---
    private void confirmarLimpar() {
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente limpar o texto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            areaDeTexto.setText("");
            atualizarBarraDeStatus();
            setAlterado(true);
        }
    }

    // --- Atualizar status ---
    private void atualizarBarraDeStatus() {
        String texto = areaDeTexto.getText();
        int caracteres = texto.length();
        String[] palavras = texto.trim().isEmpty() ? new String[0] : texto.trim().split("\\s+");
        barraDeStatus.setText("Caracteres: " + caracteres + " | Palavras: " + palavras.length);
    }

    // --- Arquivo ---
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
        setTitle(nomeArquivo + (alterado ? "*" : "") + " - Bloco de Notas");
    }

    private boolean verificarAlteracoesNaoSalvas() {
        if (!alterado) return true;
        int resposta = JOptionPane.showConfirmDialog(this,
                "O arquivo foi modificado. Deseja salvar as alterações?",
                "Alterações não salvas",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            salvarArquivo();
            return true;
        } else if (resposta == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlocoDeNotasDigiEvoluido::new);
    }
}
