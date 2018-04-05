package com.github.diorgejorge.crawler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * Created by Diorge Jorge on 29/03/2018.
 */
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CrawlerDefinition {
    private List<String> rulesRegexToNotVisit = new LinkedList<>();
    private List<Pattern> regexCompiled =  new LinkedList<>();

    public boolean toVisit(URI url){
        AtomicReference<Boolean> visit = new AtomicReference<>(true);
        regexCompiled.stream().forEach( pattern -> {
            if(url.toString().trim().isEmpty()&&pattern.matcher(url.toString()).matches()){
                visit.set(false);
            }
        });
        return visit.get() ;
    }

    private void regexCompiler(){
        this.rulesRegexToNotVisit.parallelStream().forEach(s -> {
            this.regexCompiled.add(Pattern.compile(s));
        });
    }
    public void setRulesRegexToNotVisit(String... a){
       rulesRegexToNotVisit.addAll(Arrays.asList(a));
        regexCompiler();
    }

}
