package com.ai.rag_chatter_box.ragChatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Value("/tmp/avpvectorstore.json")
    private String vectorStorePath;

    @Value("classpath:/docs/appleVisonProQuickStart.txt")
    private Resource faq;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) {
        var simpleVectorStore = new SimpleVectorStore(embeddingClient);
        var vectorStoreFile = new File(vectorStorePath);
        Boolean update = false;
        if (vectorStoreFile.exists()) {
            log.info("Vector Store File Exists,");
            simpleVectorStore.load(vectorStoreFile);
        } else {
            if(update) {
                try {
                    Files.deleteIfExists(vectorStoreFile.toPath());
                    log.info("File deleted successfully.");
                } catch (IOException e) {
                    log.error("Error occurred while deleting the file: " + e.getMessage());
                }
            }
            log.info("Vector Store File Does Not Exist, load documents");
            TextReader textReader = new TextReader(faq);
            textReader.getCustomMetadata().put("filename", "appleVisonProQuickStart.txt");
            List<Document> documents = textReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }
}
