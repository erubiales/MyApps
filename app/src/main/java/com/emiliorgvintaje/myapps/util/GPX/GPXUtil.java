package com.emiliorgvintaje.myapps.util.GPX;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GPXUtil {
    public final static String path = "/myapps/gpx";

    public static void build(ArrayList<LatLng> locations, Context context, String nombre) {
        File dirGPX = new File(Environment.getExternalStorageDirectory() + path);
        // Si no existe el directorio, lo creamos solo si es publico
        if (!dirGPX.exists()) {
            dirGPX.mkdirs();
        }
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        File file = new File(Environment.getExternalStorageDirectory() + path, System.currentTimeMillis()+".xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {

        }

        Document document = documentBuilder.newDocument();


        Element root = document.createElement("gpx");
        root.setAttribute("ruta", nombre);

        root.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
        root.setAttribute("creator", "EmilioRGMyAPPs");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("version", "1.1");
        root.setAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
        document.appendChild(root);
        Element nbr = document.createElement("number");
        nbr.setAttribute("numpoints", Integer.toString(locations.size()));
        nbr.setAttribute("numwpts", "0");
        root.appendChild(nbr);

        Element track = document.createElement("trk");
        root.appendChild(track);
        Element trackseg = document.createElement("trkseg");
        track.appendChild(trackseg);

        for (LatLng loc : locations) {
            Element trkpt = document.createElement("trkpt");
            trkpt.setAttribute("lat", Double.toString(loc.latitude));
            trkpt.setAttribute("lon", Double.toString(loc.longitude));
            trkpt.setAttribute("grade", Integer.toString(1));
            trackseg.appendChild(trkpt);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file.getPath());


        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "Exportado correctamente en /myapps/gpx", Toast.LENGTH_SHORT).show();
    }


    /**
     * Lectura de un fichero
     *
     * @param context contexto
     * @param doc     fichero
     * @return puntos de localizacion
     */
    public static ArrayList<LatLng> read(Context context, File doc) {
        ArrayList<LatLng> tkpoints = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;

        try {
            document = builder.parse(doc);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        NodeList localizaciones = document.getElementsByTagName("trkpt");

        for (int i = 0; i < localizaciones.getLength(); i++) {
            Node nodo = localizaciones.item(i);
            double lat = Double.parseDouble(nodo.getAttributes().getNamedItem("lat").getNodeValue());
            double lon = Double.parseDouble(nodo.getAttributes().getNamedItem("lon").getNodeValue());
            LatLng loc = new LatLng(lat,lon);


            tkpoints.add(loc);
        }
        Toast.makeText(context, "Ruta cargada", Toast.LENGTH_SHORT).show();
        return tkpoints;
    }

    public static String readNombre(File doc) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;

        try {
            document = builder.parse(doc);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        String res =  document.getElementsByTagName("gpx").item(0).getAttributes().getNamedItem("ruta").getNodeValue();
        System.out.printf(res);
        return  res;

    }


    public static File[] readlist() {
        File dir = new File(Environment.getExternalStorageDirectory() + path);
        File[] files = dir.listFiles();


        return files;
    }


}