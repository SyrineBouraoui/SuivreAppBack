package com.example.suivreapp.service;

import com.example.suivreapp.model.*;
import com.example.suivreapp.repository.ChatbotResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotService {

    @Autowired
    private ChatbotResponseRepository chatbotResponseRepository;

    public String getChatbotResponse(String userQuery) {
        List<ChatbotResponse> responses = chatbotResponseRepository.findAll();
        String userQueryLower = userQuery.toLowerCase();

        System.out.println("Requête utilisateur : " + userQueryLower);
        System.out.println("Nombre de réponses dans la base : " + responses.size());

        // Simple string matching: find the question with the most matching words
        ChatbotResponse bestMatch = null;
        int maxMatches = 0;

        for (ChatbotResponse response : responses) {
            String questionLower = response.getQuestion().toLowerCase();
            int matches = countMatchingWords(userQueryLower, questionLower);
            System.out.println("Comparaison avec : " + questionLower + " | Correspondances : " + matches);
            if (matches > maxMatches) {
                maxMatches = matches;
                bestMatch = response;
                System.out.println("Nouvelle meilleure correspondance : " + questionLower + " avec " + maxMatches + " correspondances");
            }
        }

        // If a match is found with at least 2 matching words, return the response
        if (bestMatch != null && maxMatches >= 2) {
            System.out.println("Réponse renvoyée : " + bestMatch.getResponse());
            return bestMatch.getResponse();
        } else {
            System.out.println("Aucune correspondance suffisante trouvée.");
            return "Je n’ai pas compris votre question. Souhaitez-vous contacter un agent de support ?";
        }
    }

    private int countMatchingWords(String userQuery, String question) {
        String[] userWords = userQuery.split("\\s+");
        String[] questionWords = question.split("\\s+");
        int matches = 0;

        // Liste élargie de mots à ignorer
        String[] ignoredWords = {"je", "mon", "ma", "est", "puis", "puis-je", "de", "la", "le", "un", "une", 
                                "sur", "à", "et", "quel", "quelle", "nom", "voir", "mon", "ai", "avec"};

        for (String userWord : userWords) {
            // Ignorer les mots dans la liste
            boolean isIgnored = false;
            for (String ignored : ignoredWords) {
                if (userWord.equals(ignored)) {
                    isIgnored = true;
                    break;
                }
            }
            if (isIgnored) continue;

            for (String questionWord : questionWords) {
                boolean isQuestionWordIgnored = false;
                for (String ignored : ignoredWords) {
                    if (questionWord.equals(ignored)) {
                        isQuestionWordIgnored = true;
                        break;
                    }
                }
                if (isQuestionWordIgnored) continue;

                if (userWord.equals(questionWord)) {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }
}