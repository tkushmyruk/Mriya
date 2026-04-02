package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.nosql.Post;
import org.example.repository.nosql.PostRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final VectorStore vectorStore;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final ChatClient chatClient;

    public List<Post> getSmartFeed(String userEmail) {
        List<String> lastLikedContents = postService.findLastLikedContentListed(userEmail);

        if (lastLikedContents.isEmpty()) {
            return postRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")))
                    .getContent();
        }

        Set<String> candidateIds = new HashSet<>();

        for (String content : lastLikedContents) {
            SearchRequest request = SearchRequest.query(content)
                    .withTopK(10)
                    .withSimilarityThreshold(0.7);

            vectorStore.similaritySearch(request).forEach(doc -> {
                candidateIds.add(String.valueOf(doc.getMetadata().get("postId")));
            });
        }

        Long currentUserId = userRepository.findByEmail(userEmail).get().getId();

        List<Post> candidatePosts = candidateIds.stream()
                .map(id -> postRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .limit(40)
                .toList();

        if (candidatePosts.isEmpty()) {
            return List.of();
        }

        String fullContext = String.join(" ", lastLikedContents);
        return rerankPosts(candidatePosts, fullContext);
    }
    private List<Post> rerankPosts(List<Post> posts, String userContext) {
        StringBuilder candidatesBuilder = new StringBuilder();
        for (int i = 0; i < posts.size(); i++) {
            candidatesBuilder.append(String.format("ID:%d | Content: %s\n", i, posts.get(i).getContent()));
        }

        String promptText = """
            У мене є користувач, якому подобається наступне: "%s".
            Оціни ці пости за шкалою від 0 до 10 за їх релевантністю інтересам користувача.
            Поверни ТІЛЬКИ список ID через кому в порядку спадання пріоритету.
            Пости:
            %s
            """.formatted(userContext, candidatesBuilder.toString());

        try {
            String response = chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content();

            String lastLine = response.substring(response.lastIndexOf("\n") + 1).trim();

            if (!lastLine.matches(".*\\d.*")) {
                lastLine = Arrays.stream(response.split("\n"))
                        .filter(line -> line.contains(",") && line.matches(".*\\d.*"))
                        .findFirst()
                        .orElse(response);
            }

            List<Integer> sortedIndexes = Arrays.stream(lastLine.split(","))
                    .map(s -> s.replaceAll("[^\\d]", "").trim())
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .filter(i -> i >= 0 && i < posts.size())
                    .distinct()
                    .toList();

            if (sortedIndexes.isEmpty()) {
                return posts.stream().limit(10).toList();
            }

            return sortedIndexes.stream()
                    .map(posts::get)
                    .limit(10)
                    .toList();

        } catch (Exception e) {
            return posts.stream().limit(10).toList();
        }
    }
    public List<String> getRecommendations(String basePostContent) {
        SearchRequest request = SearchRequest.query(basePostContent)
                .withTopK(5)
                .withSimilarityThreshold(0.7);

        return vectorStore.similaritySearch(request)
                .stream()
                .map(doc -> doc.getMetadata().get("postId").toString())
                .toList();
    }

    public void reindexAllPosts() {
        List<Post> allPosts = postRepository.findAll();

        List<Document> docs = allPosts.stream()
                .filter(p -> p.getContent() != null && !p.getContent().isBlank())
                .map(p -> new Document(p.getContent(), Map.of("postId", p.getId())))
                .toList();

        if (!docs.isEmpty()) {
            vectorStore.add(docs);
        }
    }
}
