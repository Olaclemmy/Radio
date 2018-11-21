package org.oucho.radio2.xml;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.oucho.radio2.R;
import org.oucho.radio2.db.Radio;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class ReadXML {

    public String readFile(String fichier) {

        String ret = "";

        try {

            FileInputStream inputStream = new FileInputStream(new File(fichier));

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            bufferedReader.close();
            ret = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    public void read(Context context, String XMLData) {

        List<XmlValuesModel> myData;

        try {

            /* ************* Read XML *************/

            BufferedReader bufferedReader = new BufferedReader(new StringReader(XMLData));
            InputSource inputSource = new InputSource(bufferedReader);

            /* ***********  Parse XML **************/
            XMLParser parser = new XMLParser();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            XMLReader reader = saxParser.getXMLReader();
            reader.setContentHandler(parser);
            reader.parse(inputSource);

            /* ************ Get Parse data in a ArrayList **********/
            myData = parser.list;

            if (myData != null) {

                /* ************** Get Data From ArrayList *********/

                for (XmlValuesModel xmlRowData : myData) {

                    if (xmlRowData != null) {

                        String url = xmlRowData.getUrl();
                        String name = xmlRowData.getName().replace("&amp;", "&");
                        String image = xmlRowData.getImage();
                        byte[] img = null;

                        if (image != null)
                            img = Base64.decode(image, Base64.DEFAULT);

                        Radio newRadio = new Radio(url, name, img);
                        Radio.addNewRadio(context, newRadio);
                    }
                }
            }

        } catch(Exception e) {
            Log.e("ReadXML", "Exception parse xml :" + e);
            Toast.makeText(context, context.getString(R.string.importer_erreur), Toast.LENGTH_SHORT).show();
        }
    }

}
