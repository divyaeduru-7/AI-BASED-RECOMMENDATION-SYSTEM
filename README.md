# AI-BASED-RECOMMENDATION-SYSTEM
#Task 4: This Task demonstrates the implementation of a User-Based Collaborative Filtering recommendation system built with Java and the Apache Mahout machine learning library. In the modern digital landscape, recommendation engines are the backbone of personalized user experiences, powering suggestions on platforms like Netflix, Amazon, and Spotify. This specific implementation utilizes a "Taste" based architecture to analyze user behavior and predict future preferences. The Core Architecture:

The system is built upon four primary components provided by the Mahout framework: Data Model: The foundation of the system is the FileDataModel. It ingests raw data from a CSV file containing three critical variables: UserID, ItemID, and PreferenceValue (rating). This component is highly optimized for memory efficiency, using specialized data structures to handle millions of data points without the overhead of standard Java objects.

User Similarity: This is the mathematical "brain" of the engine. We utilized the Pearson Correlation Similarity algorithm. This algorithm calculates the statistical relationship between users by comparing their rating patterns. A score of 1.0 indicates identical tastes, while -1.0 indicates complete opposites. It allows the system to identify which users are truly "like-minded."

User Neighborhood: Once similarity is calculated, the NearestNUserNeighborhood component narrows the search field. Instead of analyzing every user in the database, it identifies the "Top N" (in our case, 2) most similar individuals to the target user. This mimics real-life word-of-mouth recommendations, where you are more likely to trust the opinions of a few close friends with similar tastes than the opinions of the entire world.

Recommender Engine: The final component is the GenericUserBasedRecommender. It synthesizes the data from the neighborhood and similarity scores to find items that the similar users liked but that the target user has not yet discovered.

Analysis of the Result:

In our execution, the system provided a recommendation for User 1: Item ID 13 with a Predicted Rating of 2.0. By looking at the sample data, we can see why: User 1 and User 3 both gave "Item 10" a perfect 5.0 rating. This created a strong bond of similarity between them. Because User 3 also enjoyed Item 13, the engine logically concluded that User 1 might find it interesting as well. The predicted rating of 2.0 is an adjusted score based on the weighted averages of the neighborhood; in a larger dataset, this number would fluctuate as the engine gains more "confidence" through more data points. Why Use Java and Apache Mahout?

Java remains one of the most reliable languages for building recommendation systems due to its robust type-safety and high performance in enterprise environments. By using Maven for dependency management, the project becomes easily portable and scalable. Apache Mahout, specifically, is designed to be scalable. While our implementation runs on a local machine using a CSV file, Mahout’s architecture is built to integrate with Apache Hadoop and Spark, allowing the same logic to be applied to petabytes of data across distributed clusters.
<img width="1920" height="1020" alt="Image" src="https://github.com/user-attachments/assets/7c71c58d-527a-4f68-952c-91237a023c7d" />
