import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class BlocoDeNotasDigiEvoluido extends JFrame {
    private JTabbedPane abas;
    private JFileChooser seletorDeArquivo;
    private JLabel barraDeStatus;

    // Estruturas para controlar arquivos e estados por aba
    private HashMap<Component, File> arquivos = new HashMap<>();
    private HashMap<Component, Boolean> alterados = new HashMap<>();

    public BlocoDeNotasDigiEvoluido() {
        super("Bloco de Notas com Abas");

        abas = new JTabbedPane();
        add(abas, BorderLayout.CENTER);

        seletorDeArquivo = new JFileChooser();

        // ---- Barra de Menus ----
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemNovo = new JMenuItem("Novo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemSalvar = new JMenuItem("Salvar");
        JMenuItem itemSalvarComo = new JMenuItem("Salvar Como...");
        JMenuItem itemFecharAba = new JMenuItem("Fechar Aba");
        JMenuItem itemSair = new JMenuItem("Sair");
        menuArquivo.add(itemNovo);
        menuArquivo.add(itemAbrir);
        menuArquivo.add(itemSalvar);
        menuArquivo.add(itemSalvarComo);
        menuArquivo.add(itemFecharAba);
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

        // Ações Arquivo
        itemNovo.addActionListener(e -> novaAba(null));
        itemAbrir.addActionListener(e -> { if (verificarAlteracoesNaoSalvas()) abrirArquivo(); });
        itemSalvar.addActionListener(e -> salvarArquivo());
        itemSalvarComo.addActionListener(e -> salvarArquivoComo());
        itemFecharAba.addActionListener(e -> fecharAbaAtual());
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

        // Criar primeira aba em branco
        novaAba(null);

        setSize(700, 500);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Criar nova aba
    private void novaAba(File arquivo) {
        JTextArea areaDeTexto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaDeTexto);

        String titulo = (arquivo != null) ? arquivo.getName() : "Sem título";
        abas.addTab(titulo, scrollPane);

        arquivos.put(scrollPane, arquivo);
        alterados.put(scrollPane, false);

        areaDeTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarBarraDeStatus(areaDeTexto);
                setAlterado(scrollPane, true);
            }
        });

        abas.setSelectedComponent(scrollPane);

        if (arquivo != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                areaDeTexto.read(reader, null);
                setAlterado(scrollPane, false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

        atualizarBarraDeStatus(areaDeTexto);
    }

    private JTextArea getAreaAtual() {
        JScrollPane scrollPane = (JScrollPane) abas.getSelectedComponent();
        return (JTextArea) scrollPane.getViewport().getView();
    }

    private JScrollPane getScrollAtual() {
        return (JScrollPane) abas.getSelectedComponent();
    }

    // Atualiza barra de status
    private void atualizarBarraDeStatus(JTextArea area) {
        String texto = area.getText();
        int caracteres = texto.length();
        String[] palavras = texto.trim().isEmpty() ? new String[0] : texto.trim().split("\\s+");
        barraDeStatus.setText("Caracteres: " + caracteres + " | Palavras: " + palavras.length);
    }

    // Arquivo
    private void abrirArquivo() {
        if (seletorDeArquivo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = seletorDeArquivo.getSelectedFile();
            novaAba(arquivo);
        }
    }

    private void salvarArquivo() {
        JScrollPane scroll = getScrollAtual();
        JTextArea area = getAreaAtual();
        File arquivo = arquivos.get(scroll);

        if (arquivo != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                area.write(writer);
                setAlterado(scroll, false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            salvarArquivoComo();
        }
    }

    private void salvarArquivoComo() {
        JScrollPane scroll = getScrollAtual();
        JTextArea area = getAreaAtual();

        if (seletorDeArquivo.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = seletorDeArquivo.getSelectedFile();
            arquivos.put(scroll, arquivo);
            salvarArquivo();
        }
    }

    // Fechar aba atual
    private void fecharAbaAtual() {
        JScrollPane scroll = getScrollAtual();
        if (scroll != null) {
            if (verificarAlteracoesNaoSalvas()) {
                abas.remove(scroll);
                arquivos.remove(scroll);
                alterados.remove(scroll);

                if (abas.getTabCount() == 0) {
                    novaAba(null);
                }
            }
        }
    }

    // Controle de alterações
    private void setAlterado(JScrollPane scroll, boolean alterado) {
        alterados.put(scroll, alterado);
        File arquivo = arquivos.get(scroll);
        String titulo = (arquivo != null) ? arquivo.getName() : "Sem título";
        if (alterado) titulo += "*";
        abas.setTitleAt(abas.indexOfComponent(scroll), titulo);
    }

    private boolean verificarAlteracoesNaoSalvas() {
        JScrollPane scroll = getScrollAtual();
        if (scroll == null) return true;

        boolean alterado = alterados.getOrDefault(scroll, false);
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

    // Funções Editar
    private void confirmarLimpar() {
        JTextArea area = getAreaAtual();
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente limpar o texto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            area.setText("");
            atualizarBarraDeStatus(area);
            setAlterado(getScrollAtual(), true);
        }
    }

    private void localizarTexto() {
        JTextArea area = getAreaAtual();
        String termo = JOptionPane.showInputDialog(this, "Digite o termo a localizar:");
        if (termo != null && !termo.isEmpty()) {
            int pos = area.getText().indexOf(termo);
            if (pos >= 0) {
                area.requestFocus();
                area.select(pos, pos + termo.length());
            } else {
                JOptionPane.showMessageDialog(this, "Termo não encontrado.");
            }
        }
    }

    private void abrirJanelaLocalizarSubstituir() {
        JTextArea area = getAreaAtual();
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

        btnLocalizar.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            if (!termo.isEmpty()) {
                int pos = area.getText().indexOf(termo, area.getCaretPosition());
                if (pos >= 0) {
                    area.requestFocus();
                    area.select(pos, pos + termo.length());
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Nenhuma ocorrência encontrada.");
                }
            }
        });

        btnSubstituir.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            String substituto = campoSubstituir.getText();
            if (!termo.isEmpty() && area.getSelectedText() != null &&
                area.getSelectedText().equals(termo)) {
                area.replaceSelection(substituto);
                setAlterado(getScrollAtual(), true);
            }
        });

        btnSubstituirTodos.addActionListener(e -> {
            String termo = campoLocalizar.getText();
            String substituto = campoSubstituir.getText();
            if (!termo.isEmpty()) {
                String texto = area.getText().replaceAll(termo, substituto);
                area.setText(texto);
                setAlterado(getScrollAtual(), true);
                atualizarBarraDeStatus(area);
            }
        });

        dialogo.setSize(400, 150);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlocoDeNotasDigiEvoluido::new);
    }
}
