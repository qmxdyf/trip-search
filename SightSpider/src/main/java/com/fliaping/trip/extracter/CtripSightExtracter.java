package com.fliaping.trip.extracter;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payne on 5/30/16.
 */
public class CtripSightExtracter implements SightExtracter {

    Sight sight = null;
    Html html = null;
    ConfReader conf =  new ConfReader("conf/ctrip.xml");

    @Override
    public Sight extractHtml(Html html) {

        this.html = html;
        sight = new Sight();

        extSite_id();

        extName();

        extImages();

        extScore();

        extIntro();

        extAddress();

        extComments();

        extComment_num();

        extNear_hotels();

        extTickets();

        extTraffic();

        extCoordinate();

        extTypes();

        extPlace();

        extPageurl();

        return sight;
    }

    @Override
    public int extSite_id() {
        int id = -1;
        String site_id = byXPath("site_id");
        if(site_id != null &&  site_id.matches("[0-9]+")){
            id = Integer.parseInt(site_id);
            if(id > 0){
                sight.setSite_id(id);
                return id;
            }
        }


        //解析site_id
        String pageurl =  html.getDocument().getElementsByAttributeValue("rel","canonical").attr("href");
        String tmp[] = pageurl.split("/");
        id = Integer.parseInt(tmp[tmp.length-1].split("\\.")[0]);

        //System.out.println(id);
        sight.setSite_id(id);
        return 0;
    }

    @Override
    public String extName() {
        String name = null;
        name = byXPath("name");
        if(name != null){
            sight.setName(name);
            return name;
        }
        return name;
    }

    @Override
    public String[] extImages() {
        String images[] = null;
        images = byXPath("images",true);
        if(images != null && images.length > 0){

            sight.setImages(images);
            return images;
        }

        /*Element detailCarousel = html.getDocument().getElementById("detailCarousel");
        Elements items = detailCarousel.getElementsByClass("item");
        images = new String[items.size()];
        for (int i = 0 ; i < items.size() ; i++) {
            images[i] = items.get(i).child(0).child(0).attr("src");
        }
        sight.setImages(images);*/
        return images;
    }

    @Override
    public float extScore() {
        float score = -1;
        String score_string = byXPath("score");
        if(score_string != null && score_string.matches("^(([0-9]+)|([0-9]+.[0-9]+))$")){
            score = Float.parseFloat(score_string);
            if(score > 0){
                sight.setScore(score);
                return score;
            }
        }
        return score;
    }

    @Override
    public String extIntro() {
        String intro = null;
        intro = byXPath("intro");
        if(intro != null){
            sight.setIntro(intro);
            return intro;
        }
        return null;
    }

    @Override
    public String extAddress() {
        String address = null;
        address = byXPath("address");
        if(address != null){
            sight.setAddress(address);
            return address;
        }
        return null;
    }

    @Override
    public String[] extComments() {
        String comments[] = null;
        comments = byXPath("comments",true);
        if(comments != null && comments.length > 0){
            sight.setComments(comments);
            return comments;
        }


        return null;
    }

    @Override
    public int extComment_num() {
        int comment_num = -1;
        String num = byXPath("comment_num");
        if(num != null && num.matches("[0-9]+")){
            comment_num = Integer.parseInt(num);
        }

        if(comment_num > 0){
            sight.setComment_num(comment_num);
            return comment_num;
        }
        return 0;
    }

    @Override
    public String[] extNear_hotels() {
        String near_hotels[] = null;
        near_hotels = byXPath("near_hotels",true);
        if(near_hotels != null && near_hotels.length > 0){
            sight.setNear_hotels(near_hotels);
            return near_hotels;
        }


        return null;
    }

    @Override
    public String[] extTickets() {
        String tickets[] = null;
        tickets = byXPath("tickets",true);
        if(tickets != null && tickets.length > 0){
            sight.setTickets(tickets);
            return tickets;
        }
        return null;
    }

    @Override
    public String extTraffic() {
        String traffic = null;
        traffic = byXPath("traffic");
        if(traffic != null){
            sight.setTraffic(traffic);
            return traffic;
        }
        return null;
    }

    @Override
    public String extCoordinate() {
        String coordinate = null;
        coordinate = byXPath("coordinate");
        if(coordinate != null){
            int start = coordinate.indexOf("center=")+7;
            int end = coordinate.indexOf("&",start)-1;
            coordinate = coordinate.substring(start,end);
            sight.setCoordinate(coordinate);
            return coordinate;
        }
        return coordinate;
    }

    @Override
    public String[] extTypes() {
        String types[] = null;
        String tmp[] = byXPath("types",true);
        if(tmp != null){
            List tmpList = new ArrayList<String>();
            for (int i = 0; i < tmp.length; i++) {
                if(tmp[i].trim().matches("[\\u4e00-\\u9fa5]*")) //只要中文的
                {
                    tmpList.add(tmp[i].trim());
                    //System.out.println(tmp[i].trim());
                }
            }
            types = Utils.listToArray(tmpList);
        }

        if(types != null && types.length > 0){
            sight.setTypes(types);
            return types;
        }
        return null;
    }

    @Override
    public String[] extPlace() {
        String place[] = null;
        place = byXPath("place",true);
        if(place != null && place.length > 0){
            sight.setPlace(place);
            return place;
        }

        //解析palce
        Elements breadbar = html.getDocument().getElementsByClass("breadbar_v1");
        Element ul = breadbar.first().child(0);
        Elements lis = ul.children();
        place = new String[lis.size()-3];
        int index = 0;
        for (int i = 0; i < lis.size() ; i++) {
            if(i<2 || i == (lis.size() - 1)) continue;
            String placename = lis.get(i).child(0).text();
            if (placename.endsWith("景点")){
                placename = placename.substring(0,placename.length()-2);
            }
            place[index++] = placename;
        }
        sight.setPlace(place);
        return place;
    }

    @Override
    public String extPageurl() {
        String pageurl = null;
        pageurl = byXPath("pageurl");
        if(pageurl != null){
            sight.setPageurl(pageurl);
            return pageurl;
        }


        return null;
    }


    private String byXPath(String item){
        String result = null;
        if(item != null){
            List<ConfReader.ExtaRule> rules = conf.getItemRules(item);
            for (int i = 0; i < rules.size() ; i++){
                if(rules.get(i).getMethod().equals("xpath")){
                    String values[] = rules.get(i).getValues();

                    for (int j = 0; j < values.length ; j++){
                        if(values[j] == "") continue;
                        result = html.xpath(values[j]).toString();
                        if(null != result && !"".equals(result)){
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private String[] byXPath(String item,boolean multi){
        List<String> resultList = new ArrayList<>();
        if(item != null){
            List<ConfReader.ExtaRule> rules = conf.getItemRules(item);
            for (int i = 0; i < rules.size() ; i++){
                if(rules.get(i).getMethod().equals("xpath")){
                    String values[] = rules.get(i).getValues();

                    for (int j = 0; j < values.length ; j++){
                        if(values[j] == "") continue;
                        List tmpList = html.xpath(values[j]).all();
                        for (int k = 0; k < tmpList.size(); k++) {
                            String tmp = tmpList.get(k).toString();

                            //System.out.println(tmp);
                            if(null != tmp && !"".equals(tmp)){
                                resultList.add(tmp);
                             }
                        }
                    }
                }
            }
        }

        String[] result = null;
        if(resultList.size() > 0){
            result = new String[resultList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                result[i] = resultList.get(i);
            }
        }
        return result;
    }


}
