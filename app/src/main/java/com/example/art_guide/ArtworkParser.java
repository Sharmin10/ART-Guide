package com.example.art_guide;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArtworkParser {
    public List<Artwork> parseArtwork(Context context) {
        List<Artwork> artworks = new ArrayList<>();
        Resources resources = context.getResources();
        XmlResourceParser xmlParser = resources.getXml(R.xml.artwork);

        try {
            int eventType = xmlParser.getEventType();
            Artwork artwork = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    if("artwork".equals(xmlParser.getName())) {
                        artwork = new Artwork("", "", "", "", "", "", "");
                    } else if (artwork != null) {
                        String tagName = xmlParser.getName();
                        if ("name".equals(tagName)) {
                            artwork.setName(xmlParser.nextText());
                        } else if ("artist".equals(tagName)) {
                            artwork.setArtist(xmlParser.nextText());
                        } else if ("year".equals(tagName)) {
                            artwork.setYear(xmlParser.nextText());
                        } else if ("size".equals(tagName)) {
                            artwork.setSize(xmlParser.nextText());
                        } else if ("materials".equals(tagName)) {
                            artwork.setMaterials(xmlParser.nextText());
                        } else if ("image".equals(tagName)) {
                            artwork.setImage(xmlParser.nextText());
                        } else if ("description".equals(tagName)) {
                            artwork.setDescription(xmlParser.nextText());
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && "artwork".equals(xmlParser.getName())) {
                    artworks.add(artwork);
                    artwork = null;
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException | IOException x) {
            x.printStackTrace();
        } finally {
            xmlParser.close();
        }
        return artworks;
    }
}
