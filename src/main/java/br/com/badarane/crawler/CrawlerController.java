package br.com.badarane.crawler;

import br.com.badarane.bean.LinksFound;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.service.Firebase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Diorge Jorge on 29/03/2018.
 */
@EqualsAndHashCode
@ToString
public class CrawlerController {

    private CrawlerDefinition crawlerDefinition;
    public HashSet<URI> urlVisited = new LinkedHashSet<>();
    public  HashSet<URI> urlToVisit = new LinkedHashSet<>();
    private Firebase firebase;
    private HashMap<String, Object> map = new LinkedHashMap<>();
    public Collection<String> matchingWords = new LinkedList<>();
    public int visitingDeepness = 5;
    public int visitIterator = 0;

    public CrawlerController( String dataBaseUrl, CrawlerDefinition crawlerDefinition) {
        try {
            firebase = new Firebase(dataBaseUrl);
            this.crawlerDefinition = crawlerDefinition;
        } catch (FirebaseException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            filtering();
            urlToVisit.parallelStream().forEach(url -> {
                visit(url);
                urlVisited.add(url);
                urlToVisit.remove(url);
            });
            if(urlToVisit.isEmpty()){
                try {
                    firebase.put(map);
                } catch (JacksonUtilityException | FirebaseException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

    }
    private void filtering(){
        //remove not visitable urls
       this.urlToVisit = urlToVisit.parallelStream().filter(key -> crawlerDefinition.toVisit(key) && !urlVisited.contains(key)).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public void visit(URI url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String html = new BasicResponseHandler().handleResponse(response);
            List<Element> linksA = Jsoup.parse(html).select("a");
            checkMatchingWordsOnLinks(linksA, url);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkMatchingWordsOnLinks(List<Element> elements, URI url) {
        elements.parallelStream().forEach(elementA -> {
            String href = elementA.attr("href");
            try {
                if(getVisitingDeepness() >= visitIterator) {
                    urlToVisit.add(new URI(href.replace(" ", "%20")));
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            matchingWords.parallelStream().filter(s -> elementA.text().toLowerCase().indexOf(s.toLowerCase()) != -1).forEach(s ->
                    {
                        LinksFound linksFound = LinksFound.builder()
                                    .htmlText(elementA.html())
                                    .urlEncontrada(URI.create(href))
                                    .urlOrigem(url)
                                    .date(new Date().toString())
                                    .build();
                            map.put(elementA.text().toUpperCase().replaceAll("[^\\dA-Za-z ]", " "), linksFound);
                    }
            );
        });
        visitIterator++;
    }

    public void setMatchingWords(String... words) {
        matchingWords.addAll(Arrays.asList(words));
    }

    public void setUrls(URI... urls) {
        urlToVisit.addAll(Arrays.asList(urls));
    }

    public int getVisitingDeepness() {
        return visitingDeepness;
    }

    public void setVisitingDeepness(int visitingDeepness) {
        this.visitingDeepness = visitingDeepness;
    }
}
