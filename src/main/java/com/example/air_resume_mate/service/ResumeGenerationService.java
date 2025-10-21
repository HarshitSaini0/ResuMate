package com.example.air_resume_mate.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeGenerationService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;

    public ResumeGenerationService(ChatLanguageModel chatModel, EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    public String generateResumeSummary(String jobRole, List<String> userProfileData) {

        // 1. Create a new, temporary in-memory store for THIS request
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 2. Create an ingestor to process and store the user's data
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(100, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // 3. Ingest the user's profile data into the temporary vector store
        List<Document> documents = userProfileData.stream().map(Document::new).toList();
        ingestor.ingest(documents);

        // 4. Convert the jobRole string into an embedding
        Response<Embedding> embeddingResponse = embeddingModel.embed(jobRole);
        Embedding queryEmbedding = embeddingResponse.content();

        // 5. Search for relevant segments using EmbeddingSearchRequest
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .minScore(0.0) // Optional: set minimum similarity score
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // Extract the text segments from the search results
        List<TextSegment> relevantSegments = searchResult.matches().stream()
                .map(EmbeddingMatch::embedded)
                .toList();

        // 6. Build a detailed prompt for the AI
        String promptTemplate = """
            You are a professional resume writer. Based ONLY on the following context, write 3 concise bullet points for a resume targeting a '%s' position.
            Do not use any information not provided in the context.
            
            Context:
            %s
            """;

        String context = relevantSegments.stream()
                .map(TextSegment::text)
                .reduce("", (a, b) -> a + "\n- " + b);
        String finalPrompt = String.format(promptTemplate, jobRole, context);

        // 7. Generate and return the final response
        return chatModel.generate(finalPrompt);
    }
}