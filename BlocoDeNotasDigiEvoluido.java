import javax.swing.*;
import java.awt.*;      //borderLayout, GridLayout estao aqui
import java.awt.event.*;//ActionListener, ActionEvent
import java.io.*;       //File, Reader/Write, buffer

public class BlocoDeNotasDigiEvoluido extends JFrame implements ActionListener{
    //------------------CAMPOS DA UI-----------------------
    private JTextArea areaTexto;//Campo grande para escrever
    private JButton btnSalvar, btnAbrir, btnLimpar, btnSalvarComo, btnAbrirEm;
    
    //gera Arquivo Padrão- ficará na pasta projeto.
    private final File arquivoPadrao = new File("texto.txt");
    
    //---------------------CONSTRUTOR---------------------------
    public BlocoDeNotasDigiEvoluido(){
        super("Bloco de Notas simples");
        
        //----------------DEFINIR O LAYOUT DO FRAME-----------------
        //BorderLayout divide o espaço em regiões(NORTH, SOUTH, CENTER, EASE, WEST)
        //Isso deixa a janela reponsiva, sem precisar de setBouds.
        setLayout(new BorderLayout());
        
        
        //--------------2) NORTH: TÍTULO-------------
        JLabel titulo = new JLabel("Bloco de notas - BorderLayout + GridLayout + arquivos", SwingConstants.CENTER);
        //Adiciona no topo (NORTH). A altura do NORTH se ajusta ao conteúdo
        add(titulo, BorderLayout.NORTH);
        
        //-------------3) CENTER: ÁREA DE TEXTO COM ROLAGEM--------
        areaTexto = new JTextArea();
        areaTexto.setLineWrap(true);       //quebra de linha altomaticamente
        areaTexto.setWrapStyleWord(true);  //Quebra por palavra garantindo melhor leitura
        //JScrollPane fornece barras de rolagem ao redor do JTextArea
        JScrollPane scroll = new JScrollPane(areaTexto);
        //A região CENTER (centro da tela) ocupa todo o espaço restante
        add(scroll, BorderLayout.CENTER);
        
        
       //------------ 4) SOUTH: PAINEL COM BOTÕES EM GRADE(grid) ---------
       //Usamos o JPanel com GidLayout para alinhar os 3 botoes igualmente
       //GridLayout(1, 3, 8, 8) => 1 linha, 3 colunas, espaçamento de 8px 
       JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 8, 8));
       btnSalvar = new JButton("Salvar");
       btnAbrir = new JButton("Abrir");
       btnLimpar = new JButton("Limpar");
       btnSalvarComo = new JButton("Salvar como...");
       btnAbrirEm = new JButton("Abrir em...");
       painelBotoes.add(btnSalvar);
       painelBotoes.add(btnAbrir);
       painelBotoes.add(btnLimpar);
       painelBotoes.add(btnSalvarComo);
       painelBotoes.add(btnAbrirEm);
       //Adiciona o painel de botões no SOUTH (Parte de baixo da janela)
       add(painelBotoes, BorderLayout.SOUTH);
       
       //---------- 5) REGITRAR EVENTOS (LISTERNERS) -------------
       //Este frame implementa (implements) ActionListener, então "this" é o ouvinte
       btnSalvar.addActionListener(this);
       btnAbrir.addActionListener(this);
       btnLimpar.addActionListener(this);
       btnSalvarComo.addActionListener(this);
       btnAbrirEm.addActionListener(this);
       
       //------------ 6) CONFIGURAÇÕES FINAIS DA JANELA ---------
       setSize(640, 480);                       //Tamanho Inicial
       setLocationRelativeTo(null);             //Centraliza a janela na tela
       setDefaultCloseOperation(EXIT_ON_CLOSE); //Encerra a aplicação ao clicar em x
       setVisible(true);                        //Torna a janela visivel
       
    }
    //--------------------- TRATAMENTO DOS BOTÕES ---------------------
     @Override
    public void actionPerformed(ActionEvent e) {
        Object origem = e.getSource();//Identificar qual componente gerou o evento 
       if(origem == btnSalvar){
            salvarArquivo();
        } else if(origem == btnAbrir){
            abrirArquivo();
        }else if(origem == btnLimpar){
            limparTexto();
        } else if(origem == btnSalvarComo){
            salvarComo();
        } else if (origem == btnAbrirEm){
            abrirComChooser();
        }
    }
    
    //-------------------MÉTODO DE ARQUIVO-------------------
    /*Salva o conteúdo da area de texto dentro do arquivo "texto.txt"
    Try-with-resources => o Java fechar o arquivo automaticamente ao final do bloco
    */
    private void salvarArquivo(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoPadrao))){
            //bw.write escreve/grava no arquivo
            //getText() pega o texto do JTextArea
          bw.write(areaTexto.getText());  
          //Mensagem amigável para o usuario com o caminho completo
          JOptionPane.showMessageDialog(this, "Arquivo salvo em: \n" + arquivoPadrao.getAbsolutePath());
          //Feedeback visual no título da janela 
          setTitle("Bloco de Notas Simples - (Salvo)");
        } catch (IOException e){
            //Se algo der errado(permissa, disco cheio, caminho inválido
            //Tratamos aqui. messageDialog exibe o erro para o usuário
            JOptionPane.showMessageDialog(this, "Erro ao salvar :" + e.getMessage());
            
            
        }
    }
    
    //Lê todo o conteúdo do arquivo "texto.txt" e coloca na volta na area de texto
    //Também utiliza try-with-resources para fechar o leitor automaticamente
    private void abrirArquivo(){
        //Primeiro, verificamos a existência do arquivo
        if(!arquivoPadrao.exists()){
            JOptionPane.showMessageDialog(this, "Arquivo não encontrado:\n" + arquivoPadrao.getAbsolutePath());
            return; //Não tenta abrir se não existe
        }
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoPadrao))){
            String linha;
            //Lemos linha a linha até acabar (readline() retorna null no fim)
            while((linha = br.readLine())!= null){
              conteudo.append(linha).append("\n");  
            }
            //Mostra o conteúdo no JTextArea
            areaTexto.setText(conteudo.toString());
            setTitle("Bloco de Notas Simples - (Arquivo aberto)");
            
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, "Erro ao abrir:\n" + e.getMessage()); 
        }
    }
    
    //Limpa o conteúdo do JTextArea e retaura o título original
    private void limparTexto(){
        areaTexto.setText("");
        setTitle("Blocos de Notas Simples");
    }
    //Extra
    private void salvarComo() {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
    File destino = chooser.getSelectedFile();
    
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(destino))){
    bw.write(areaTexto.getText());
    JOptionPane.showMessageDialog(this, "Salvo em:\n" + destino.getAbsolutePath());
} catch (IOException e){
    JOptionPane.showMessageDialog(this, "Erro ao Salvar: " + e.getMessage());
        }
    }
}
    private void abrirComChooser(){
        JFileChooser chooser = new JFileChooser();
         if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
             File origem = chooser.getSelectedFile();
             StringBuilder conteudo = new StringBuilder();
             try (BufferedReader br = new BufferedReader(new FileReader(origem))){
                 String linha;
                while ((linha = br.readLine()) != null){
                    conteudo.append(linha).append("\n");
                }
                areaTexto.setText(conteudo.toString());
                setTitle("Bloco de Notas — " + origem.getName());
             } catch (IOException e){
                JOptionPane.showMessageDialog(this, "Erro ao abrir: " + e.getMessage()); 
             }
         }
    }
    //----------------------MAIN------------------------
    public static void main(String[] args) {
        //Boas Práticas : iniciar Swing na Event Dispatch Thread (EDT)
        //Por que? Swing não é thread-safe; isso evita glitches e condições de corrida (race conditions)
        SwingUtilities.invokeLater(() -> new BlocoDeNotasDigiEvoluido());
        
    }
   
}
