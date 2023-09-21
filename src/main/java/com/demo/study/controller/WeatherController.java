package com.demo.study.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class WeatherController {

    private String ServiceKey = "BxcWG0ueyR3PlJiksIoqpwFsFQJyLjESYHD0G0HAKVdvre4PLyY04bt73WD3q4Gj0fS4CkStegrF21Ai%2BeDqIw%3D%3D";
    private String weather_url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
    private Date date = new Date();
    private SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
    public String today_date(){
        Calendar c = Calendar.getInstance(Locale.KOREA);
        if( c.get(Calendar.MINUTE)<30 ) c.add(Calendar.HOUR, -1);
        c.add(Calendar.MINUTE, -30);
        format1 = new SimpleDateFormat("yyyyMMddHHmm");
        return format1.format(c.getTime());
    }
    @ResponseBody
    @PostMapping("/now_weather")
    public ArrayList<HashMap<String, Object>> now_weather() throws Exception {
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?";
        return now_weather(nowAPI(url, 55, 127));
    }

    @ResponseBody
    @PostMapping("/town_weather")
    public JSONObject[] town_weather() throws Exception{
        return xml_list(weatherAPI(new StringBuilder("https://www.kma.go.kr/wid/queryDFS.jsp"), 55, 127));
    }

    public JSONObject[] xml_list(String url) throws Exception {
        JSONObject[] datas = new JSONObject[2];
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(XML.toJSONObject(urlRedirect(url)).toString());
            JSONObject json = (JSONObject) obj;
        try {
            if( json.get("wid") != null ) {
                datas[0] = (JSONObject)((JSONObject)json.get("wid")).get("header");
                datas[1] = (JSONObject)((JSONObject)json.get("wid")).get("body");
            }
        }catch(Exception e) {
            System.out.printf("동네예보용 JSON 으로 변환중 오류:{}", e.getMessage());
        }
        return datas;
    }

    public ArrayList<HashMap<String, Object>> now_weather(String url) throws Exception{

        JSONParser parser = new JSONParser();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        Object obj = parser.parse(urlRedirect(url));
        JSONObject json = (JSONObject) obj;
        JSONArray arr = (JSONArray) ((JSONObject)((JSONObject)((JSONObject)json.get("response")).get("body")).get("items")).get("item");

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String,Object>>() {};
        for(int i=0; i<arr.size(); i++){
            list.add(objectMapper.readValue(arr.get(i).toString() ,typeReference));
        }
        return list;
    }
    private String weatherAPI(StringBuilder url, int nx, int ny) {
        url.append("?gridx=" + nx);
        url.append("&gridy=" + ny);
        url.append("&_type=json");
        return url.toString();
    }
    public String nowAPI(String url, int nx, int ny){
        String text = url +
                "serviceKey=" + ServiceKey +
                "&numOfRows=10" +
                "&pageNo=1" +
                "&base_date=" + today_date().substring(0,8) +
                "&base_time=" + today_date().substring(8) +
                "&nx=" + nx +
                "&ny=" + ny +
                "&dataType=json";
        System.out.println(text);
        return text;
    }
    public String urlRedirect(String url) throws IOException, ParseException{
//        String text = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?" +
        URL result = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) result.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }

    private HashMap<String, Object> convertToGrid (double latitude, double longitude) {

        double lat = Double.valueOf(latitude);
        double lon = Double.valueOf(longitude);
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        int XO = 43; // 기준점 X좌표(GRID)
        int YO = 136; // 기1준점 Y좌표(GRID)
        HashMap<String, Object> map = new HashMap<String, Object>();

        try {
            double DEGRAD = Math.PI / 180.0;
            double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD;
            double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD;
            double olat = OLAT * DEGRAD;
            double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);
            double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lon * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;

//			map.put("latitude", latitude);
//			map.put("longitude", longitude);
            map.put("nx", (int)(Math.floor(ra * Math.sin(theta) + XO + 0.5)) );
            map.put("ny", (int)(Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)) );

        } catch (Exception e) {
            System.out.printf("위경도 격자로 변환중 오류:{}", e.getMessage());
        }
        return map;
    }

}
