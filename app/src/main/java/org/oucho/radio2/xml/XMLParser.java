package org.oucho.radio2.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

class XMLParser extends DefaultHandler {


    List<XmlValuesModel> list=null;

    private XmlValuesModel radioValues = null;

    private StringBuilder builder;


    @Override
    public void startDocument() throws SAXException {

        list = new ArrayList<>();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {


        builder = new StringBuilder();

        if(localName.equals("radio")){

            radioValues = new XmlValuesModel();
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {


        if(localName.equals("radio")){

            list.add( radioValues );

        } else if(localName.equalsIgnoreCase("url")){

            radioValues.setURL(builder.toString());

        } else if(localName.equalsIgnoreCase("name")){

            radioValues.setName(builder.toString());

        } else if (localName.equalsIgnoreCase("image")){

            radioValues.setImage(builder.toString());
        }

    }


    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        /* *****  Read the characters and append them to the buffer  ***** */
        String tempString=new String(ch, start, length);
        builder.append(tempString);
    }
}
