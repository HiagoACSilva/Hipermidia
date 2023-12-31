
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class App {
    private static final String FILENAME = "src\\verbetesWikipedia.xml";
    private static HashMap<Integer, Pagina> Paginas = new HashMap<>(); // HASHMAP DAS PAGINAS
    // <INTEGER, PAGINA> INTEGER : O NUMERO DA PAGINA, PAGINA: A PAGINA EM QUESTAO

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        cache();// FUNCAO QUE ADICIONA O ARQUIVO PARA O CACHE
        String[] pesquisa; // VETOR DAS PALAVRAS BUSCADAS, SEPARADAS POR " "
        System.out.println("Digite o titulo");
        do {
            // INICIANDO A PESQUISA, USANDO O VETOR DE PESQUISA PARA SABER SE NO CACHE
            // EXISTE

            ArrayList<Pesquisa> Resultados = new ArrayList<>(); // CRIANDO A LISTA DE RESULTADOS
            pesquisa = scan.nextLine().toLowerCase().split(" ");// SEPARANDO OS TERMOS A SEREM BUSCADOS

            if (!pesquisa[0].equals("sair")) { // ENQUANTO A PRIMEIRA PALAVRA FOR DIFERENTE DE "SAIR"
                for (int i = 0; i < Paginas.size(); i++) {
                    int[] points = new int[pesquisa.length];// SE CRIA O VETOR DE PONTOS DE CADA PALAVRA
                    for (int j = 0; j < pesquisa.length; j++) {
                        // VERIFICA SE EXISTE A PALAVRA BUSCADA NO HASHMAP DA PAGINA
                        if (Paginas.get(i).getWords().containsKey(pesquisa[j])) {
                            // SE EXISTE, ENTAO SE SOMA O VALOR DE RELEVANCIA DA PALAVRA NO VETOR DE PONTOS
                            points[j] = 0;
                            points[j] += Paginas.get(i).getWords().get(pesquisa[j]).intValue();

                        }
                    }
                    if (points != null) {
                        int pointstotal = 1;
                        // MULTIPLICA OS VALORES DOS PONTOS ADQUIRIDOS DE CADA PALAVRA
                        // ISSO ACARRETA EM, SE UMA PALAVRA NAO EXISTIR, SEU VALOR DE PONTOS SERÁ 0
                        // LOGO QUALQUER VALOR MULTIPLICADO POR 0 DARIA 0
                        // LOGO SE FOR PESQUISADO RABBIT COMPUTER E NA PAGINA A PONTUAÇÃO SER
                        // 0 PARA RABBIT E 120 PARA COMPUTER, O PESO SERÁ 0 POIS 120x0=0
                        for (int k = 0; k < points.length; k++) {
                            pointstotal *= points[k];
                        }
                        // E AQUI É FILTRADO PARA SÓ APARECER A PAGINA SE SUA PONTUAÇÃO NÃO FOR 0
                        if (pointstotal > 1) {
                            Pesquisa atual = new Pesquisa(Paginas.get(i).getId(), Paginas.get(i).getTitle(),
                                    pointstotal);
                            Resultados.add(atual);
                        }
                    }
                }
                // AQUI É FEITO O PRINT DA PESQUISA, MAS ANTES USADO O COMPARATOR PARA ORGANIZAR
                // O VETOR
                if (!Resultados.isEmpty()) {
                    Comparator<Pesquisa> relevancia = Collections
                            .reverseOrder(Comparator.comparing(Pesquisa::getPoints));
                    Collections.sort(Resultados, relevancia);
                    for (int i = 0; i < Resultados.size(); i++) {
                        if (i <= 20) {
                            System.out.println(
                                    "Id: " + Resultados.get(i).getId() + ", Titulo: " + Resultados.get(i).getTitulo()
                                            + ", QdP: "
                                            + Resultados.get(i).getPoints());
                        }
                    }
                } else {
                    System.out.println("Nada Encontrado");
                }
                System.out.println("Nova Pesquisa?");
            }
        } while (!pesquisa[0].equals("sair"));
        scan.close();

    }

    public static void cache() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(FILENAME));

            // COMEÇO PEGANDO AS PAGINAS E TRANSFORMANDO ELAS EM ELEMENTOS DE UMA LISTA DE
            // PAGINAS(NOS)

            NodeList list = doc.getElementsByTagName("page");
            // PERCORRENDO PAGINA POR PAGINA PELA LISTA
            for (int temp = 0; temp < list.getLength(); temp++) {
                // CONVERTENDO O NO PARA PODER SER UTILIZADO
                Node node = list.item(temp);

                // SE ELE FOR O TIPO NO, COMEÇA
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // TRANSFORMANDO O NO EM ELEMENTO
                    Element element = (Element) node;

                    // PEGANDO O ID DA PAGINA
                    String id = element.getElementsByTagName("id").item(0).getTextContent();

                    // PEGANDO O TITULO E TRANSOFRMANDO NUM VETOR DE STRINGS SEPARADO PELO " "
                    String[] title = element.getElementsByTagName("title").item(0).getTextContent().split(" ");

                    // PEGANDO O TEXTO E TRANSFORMANDO NUM VETOR DE STRINGS SEPARADOS PELO " "
                    String[] text = element.getElementsByTagName("text").item(0).getTextContent().toLowerCase()
                            .split(" ");

                    // CRIANDO O HASHMAP DA PAGINA
                    HashMap<String, Integer> Words = new HashMap<>();

                    // PARA CADA PALAVRA DO TITULO
                    for (int value = 0; value < title.length; value++) {
                        String PALAVRA = title[value].toLowerCase();
                        if (PALAVRA.length() >= 3) {
                            if (Words.containsKey(PALAVRA)) {
                                Words.replace(PALAVRA, Words.get(PALAVRA), Words.get(PALAVRA) + 10);
                            } else {
                                Words.put(PALAVRA, 10);
                            }
                        }
                    }
                    for (int value = 0; value < text.length; value++) {
                        String PALAVRA = text[value].toLowerCase();
                        if (PALAVRA.length() >= 3) {
                            if (Words.containsKey(PALAVRA)) {
                                Words.replace(PALAVRA, Words.get(PALAVRA), Words.get(PALAVRA) + 1);
                            } else {
                                Words.put(PALAVRA, 1);
                            }
                        }
                    }
                    Pagina pagina = new Pagina(id, element.getElementsByTagName("title").item(0).getTextContent(),
                            Words);
                    Paginas.put(temp, pagina);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    static class Pagina {
        public Pagina(String id, String title, HashMap<String, Integer> Words) {
            this.id = id;
            this.title = title;
            this.Words = Words;
        }

        private String id;
        private String title;
        private HashMap<String, Integer> Words;

        public HashMap<String, Integer> getWords() {
            return Words;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

    }

    static class Pesquisa {
        private String id;
        private String titulo;
        private int points;

        public Pesquisa(String id, String titulo, int points) {
            this.id = id;
            this.titulo = titulo;
            this.points = points;
        }

        public String getTitulo() {
            return titulo;
        }

        public int getPoints() {
            return points;
        }

        public String getId() {
            return id;
        }
    }

}