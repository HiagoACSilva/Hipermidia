
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private static HashMap<String, List<Pagina>> cache = new HashMap<>();
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String pesquisa;
        System.out.println("Digite o titulo");
        do{
            pesquisa = scan.nextLine();
            if(cache.get(pesquisa) != null){
                for (Pagina pagina : cache.get(pesquisa)) {
                    if (pagina.getPoints() > 50) {
                        
                        System.out.println("Id: " + pagina.getId() + ", Titulo: " + pagina.title + ", QdR: "
                                + pagina.getQdR());
                    }
                }
            }else{

            //PESQUISA
            
                try {

                    // parse XML file
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document doc = db.parse(new File(FILENAME));
                    NodeList list = doc.getElementsByTagName("page");
                    List<Pagina> paginas = new ArrayList<>();
                    for (int temp = 0; temp < list.getLength(); temp++) {
                        Node node = list.item(temp);

                        if (node.getNodeType() == Node.ELEMENT_NODE) {

                            Element element = (Element) node;
                            // get text
                            String id = element.getElementsByTagName("id").item(0).getTextContent();
                            String[] title = element.getElementsByTagName("title").item(0).getTextContent().split(" ");
                            String[] text = element.getElementsByTagName("text").item(0).getTextContent().toLowerCase().split(" ");
                            
                            for (String string : title) {
                                if(cache.get(string) == null){
                                    cache.put(string, paginas);
                                }
                            }
                            for (String string : text) {
                                
                            }
                            //Busca
                           /*   if (text.toLowerCase().contains(pesquisa.toLowerCase())) {
                                long count = Pattern.compile(pesquisa, Pattern.CASE_INSENSITIVE).matcher(text).results()
                                        .count();
                                long points = count;
                                if (title.toLowerCase().contains(pesquisa.toLowerCase())) {
                                    points += 10;
                                }
                                paginas.add(new Pagina(id, title, count, points));
                            }*/

                        }

                    }
                    if (!paginas.isEmpty()) {
                        Comparator<Pagina> relevancia = Collections.reverseOrder(Comparator.comparing(Pagina::getPoints));
                        Collections.sort(paginas, relevancia);
                        cache.put(pesquisa, paginas);
                        for (Pagina pagina : cache.get(pesquisa)) {
                            if (pagina.getPoints() > 50) {
                                
                                System.out.println("Id: " + pagina.getId() + ", Titulo: " + pagina.title + ", QdR: "
                                        + pagina.getQdR());
                            }
                        }
                    }
                    
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Nova Pesquisa?");
        }while(pesquisa != "sair");
        scan.close();

    }

    static class Pagina {
        public Pagina(String id, String title, long qdR, long points) {
            this.id = id;
            this.title = title;
            this.qdR = qdR;
            this.points = points;
        }

        private String id;
        private String title;
        private long qdR;
        private long points;

        public long getPoints() {
            return points;
        }

        public void setPoints(long points) {
            this.points = points;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public long getQdR() {
            return qdR;
        }
    }

}