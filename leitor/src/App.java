
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class App {
    private static final String FILENAME = "verbetesWikipedia.xml";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        System.out.println("Digite o titulo");
        String pesquisa = scan.nextLine();
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
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    String text = element.getElementsByTagName("text").item(0).getTextContent();
                    if (title.contains(pesquisa)) {
                        long count = Pattern.compile(pesquisa).matcher(text).results().count();
                        paginas.add(new Pagina(id, title, count));
                    }

                }

            }
            if(!paginas.isEmpty()){
                Comparator<Pagina> relevancia = Collections.reverseOrder(Comparator.comparing(Pagina::getQdR));
                Collections.sort(paginas, relevancia);
            }
            for(Pagina pagina : paginas){
                System.out.println("Id: "  + pagina.getId() + ", Titulo: " + pagina.title + ", QdR: "
                + pagina.getQdR());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        scan.close();

    }

    static class Pagina {
        public Pagina(String id, String title, long qdR) {
            this.id = id;
            this.title = title;
            this.qdR = qdR;
        }

        private String id;
        private String title;
        private long qdR;

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