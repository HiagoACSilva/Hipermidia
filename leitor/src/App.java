
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
            // get <staff>
            NodeList list = doc.getElementsByTagName("page");
            System.out.println(list.getLength());
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    // get text
                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    if(title.contains(pesquisa)){
                        System.out.println("Id: " + id + ", titulo " + (temp + 1) + " : " + title);
                    }
                        
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        scan.close();

    }
}