package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "review_id")
    private long reviewId;
    @Column(name = "rating")
    private int rating;
    @Column(name = "textual_review")
    private String textualReview;
    @Column(name = "user_name")
    private String username;
    @Column(name = "course_code")
    private String courseCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Review review = (Review) o;
        return rating == review.rating
                && Objects.equals(textualReview, review.textualReview)
                && Objects.equals(username, review.username)
                && Objects.equals(courseCode, review.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, textualReview, username, courseCode);
    }
}
