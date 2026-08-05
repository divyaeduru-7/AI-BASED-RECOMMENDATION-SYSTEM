import java.util.*;

public class RecommenderSystem {

    // Ratings data: user -> (movie -> rating out of 5)
    private static Map<String, Map<String, Double>> userRatings = new LinkedHashMap<>();

    public static void main(String[] args) {
        loadSampleData();

        System.out.println("=== User Ratings Data ===");
        printAllRatings();

        String targetUser = "Alice";
        int topN = 3;

        System.out.println("\n=== Finding similar users to " + targetUser + " ===");
        Map<String, Double> similarities = computeSimilarities(targetUser);
        for (Map.Entry<String, Double> entry : similarities.entrySet()) {
            System.out.printf("Similarity(%s, %s) = %.3f%n", targetUser, entry.getKey(), entry.getValue());
        }

        System.out.println("\n=== Recommendations for " + targetUser + " ===");
        List<Map.Entry<String, Double>> recommendations = recommend(targetUser, similarities, topN);
        for (Map.Entry<String, Double> rec : recommendations) {
            System.out.printf("%s (predicted rating: %.2f)%n", rec.getKey(), rec.getValue());
        }
    }

    private static void loadSampleData() {
        userRatings.put("Alice", mapOf("Inception", 5.0, "Titanic", 3.0, "Avengers", 4.0));
        userRatings.put("Bob", mapOf("Inception", 4.0, "Titanic", 2.0, "Avengers", 5.0, "Interstellar", 5.0));
        userRatings.put("Carol", mapOf("Titanic", 5.0, "Avengers", 3.0, "Interstellar", 2.0, "Joker", 4.0));
        userRatings.put("Dave", mapOf("Inception", 5.0, "Interstellar", 4.5, "Joker", 5.0));
        userRatings.put("Eve", mapOf("Avengers", 4.5, "Joker", 3.5, "Titanic", 4.0));
    }

    private static Map<String, Double> mapOf(Object... kv) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put((String) kv[i], (Double) kv[i + 1]);
        }
        return map;
    }

    private static void printAllRatings() {
        for (Map.Entry<String, Map<String, Double>> entry : userRatings.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    // Cosine similarity between the target user and every other user
    private static Map<String, Double> computeSimilarities(String targetUser) {
        Map<String, Double> targetRatings = userRatings.get(targetUser);
        Map<String, Double> similarities = new LinkedHashMap<>();

        for (String otherUser : userRatings.keySet()) {
            if (otherUser.equals(targetUser)) continue;

            Map<String, Double> otherRatings = userRatings.get(otherUser);
            double similarity = cosineSimilarity(targetRatings, otherRatings);
            similarities.put(otherUser, similarity);
        }

        // Sort by similarity descending
        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(similarities.entrySet());
        sortedList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        Map<String, Double> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : sortedList) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    private static double cosineSimilarity(Map<String, Double> a, Map<String, Double> b) {
        Set<String> commonMovies = new HashSet<>(a.keySet());
        commonMovies.retainAll(b.keySet());

        if (commonMovies.isEmpty()) return 0.0;

        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (String movie : commonMovies) {
            dotProduct += a.get(movie) * b.get(movie);
        }
        for (double val : a.values()) normA += val * val;
        for (double val : b.values()) normB += val * val;

        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Predict ratings for movies the target user hasn't rated yet,
    // weighted by similarity to other users
    private static List<Map.Entry<String, Double>> recommend(String targetUser, Map<String, Double> similarities, int topN) {
        Map<String, Double> targetRatings = userRatings.get(targetUser);
        Map<String, Double> weightedSum = new HashMap<>();
        Map<String, Double> similaritySum = new HashMap<>();

        for (Map.Entry<String, Double> simEntry : similarities.entrySet()) {
            String otherUser = simEntry.getKey();
            double similarity = simEntry.getValue();
            if (similarity <= 0) continue;

            Map<String, Double> otherRatings = userRatings.get(otherUser);
            for (Map.Entry<String, Double> ratingEntry : otherRatings.entrySet()) {
                String movie = ratingEntry.getKey();
                if (targetRatings.containsKey(movie)) continue; // skip already-rated movies

                double rating = ratingEntry.getValue();
                weightedSum.merge(movie, similarity * rating, Double::sum);
                similaritySum.merge(movie, similarity, Double::sum);
            }
        }

        Map<String, Double> predictedRatings = new HashMap<>();
        for (String movie : weightedSum.keySet()) {
            double predicted = weightedSum.get(movie) / similaritySum.get(movie);
            predictedRatings.put(movie, predicted);
        }

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(predictedRatings.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return sorted.size() > topN ? sorted.subList(0, topN) : sorted;
    }
}