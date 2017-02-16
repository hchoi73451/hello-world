package hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @RequestMapping(value={"/search/", "/search"})
    public Map greetingDefault() {
        return getData("");
    }

    @RequestMapping("/search/{term}")
    public Map greeting(@PathVariable String term) {
        return getData(term);
    }

    private Map<String, Object> getData(String term){
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            URL url = new URL("http://api.giphy.com/v1/gifs/search?q=" + term + "&api_key=dc6zaTOxFJmzC&limit=5");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();

            // TODO add to logging
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }

            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(builder.toString(), Map.class);

            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            // TODO add to logging
            System.out.println(s);

            Map pagination = (Map)map.get("pagination");
            int count = Integer.valueOf(pagination.get("count").toString());

            List newList = new ArrayList();
            if (count >= 5){
                List list = (List)map.get("data");
                for (Object o : list) {
                    Map m = (Map) o;
                    Map<String, Object> map2 = new LinkedHashMap<String, Object>();
                    map2.put("gif_id", m.get("id"));
                    map2.put("url", m.get("url"));
                    newList.add(map2);
                }
            }

            map.clear();
            map.put("data", newList);

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }



}
