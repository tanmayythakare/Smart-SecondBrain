package com.example.backend.ai;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KeywordExtractor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "then", "else", "when",
            "at", "from", "by", "for", "with", "about", "against", "between",
            "into", "through", "during", "before", "after", "above", "below",
            "to", "of", "in", "on", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "i", "me", "my", "you", "your",
            "he", "him", "his", "she", "her", "hers", "it", "its", "we", "us", "our", "they", "them", "their",
            "what", "which", "who", "whom", "this", "that", "these", "those"
    ));

    public List<String> extract(String text) {
        if (text == null) return Collections.emptyList();
        
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").split("\\s+");
        return Arrays.stream(words)
                .filter(word -> word.length() > 2)
                .filter(word -> !STOP_WORDS.contains(word))
                .distinct()
                .collect(Collectors.toList());
    }
}
